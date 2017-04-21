package indi.yume.view.avocadoviews.dsladapter;

import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import indi.yume.view.avocadoviews.dsladapter.builder.BaseLayoutRenderer;
import indi.yume.view.avocadoviews.dsladapter.functions.Supplier;

import static indi.yume.view.avocadoviews.dsladapter.functions.Functions.staticSupplier;
import static indi.yume.view.avocadoviews.dsladapter.Predicates.checkArgument;
import static indi.yume.view.avocadoviews.dsladapter.Predicates.checkNotNull;
import static java.lang.Boolean.TRUE;

/**
 * Created by yume on 17-4-20.
 */

public class RendererAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @NonNull
    public static Builder repositoryAdapter() {
        return new Builder();
    }

    public static final class Builder {
        @NonNull
        final List<Supplier<Object>> suppliers = new ArrayList<>();
        @NonNull
        final List<BaseRenderer<Object>> presenters = new ArrayList<>();
        @NonNull
        final LongSparseArray<Boolean> staticPresenters = new LongSparseArray<>();

        @NonNull
        public <T> Builder add(@NonNull final Supplier<T> supplier,
                               @NonNull final BaseRenderer<T> presenter) {
            @SuppressWarnings("unchecked")
            final Supplier<Object> untypedRepository = (Supplier<Object>) supplier;
            suppliers.add(untypedRepository);
            @SuppressWarnings("unchecked")
            final BaseRenderer<Object> untypedPresenter =
                    (BaseRenderer<Object>) checkNotNull(presenter);
            presenters.add(untypedPresenter);
            return this;
        }

        @NonNull
        public <T> Builder addItem(@NonNull final T item,
                                   @NonNull final BaseRenderer<T> presenter) {
            suppliers.add(staticSupplier((Object) item));
            @SuppressWarnings("unchecked")
            final BaseRenderer<Object> untypedPresenter =
                    (BaseRenderer<Object>) checkNotNull(presenter);
            staticPresenters.put(presenters.size(), TRUE);
            presenters.add(untypedPresenter);
            return this;
        }

        @NonNull
        public Builder addLayout(@NonNull final BaseLayoutRenderer presenter) {
            suppliers.add(staticSupplier((Object) RendererAdapter.class));
            staticPresenters.put(presenters.size(), TRUE);
            presenters.add(new LayoutBaseRenderer(presenter));
            return this;
        }

        @NonNull
        public RendererAdapter build() {
            return new RendererAdapter(this);
        }

        private Builder() {}
    }

    private final int repositoryCount;
    @NonNull
    private final Supplier<Object>[] suppliers;
    @NonNull
    private final Object[] data;
    @NonNull
    private final BaseRenderer<Object>[] presenters;
    @NonNull
    private final LongSparseArray<Boolean> staticPresenters;
    @NonNull
    private final Map<RecyclerView.ViewHolder, BaseRenderer<Object>> presenterForViewHolder;
    @NonNull
    private final int[] endPositions;

    private boolean dataInvalid;
    private int resolvedRepositoryIndex;
    private int resolvedItemIndex;

    public RendererAdapter(@NonNull final Builder builder) {
        final int count = builder.suppliers.size();
        checkArgument(count > 0, "Must add at least one repository");
        checkArgument(builder.presenters.size() == count,
                "Unexpected repository and presenter count mismatch");

        @SuppressWarnings("unchecked")
        final Supplier<Object>[] suppliers = builder.suppliers.toArray(
                (Supplier<Object>[]) new Supplier[count]);

        @SuppressWarnings("unchecked")
        final BaseRenderer<Object>[] presenters = builder.presenters.toArray(
                (BaseRenderer<Object>[]) new BaseRenderer[count]);

        this.data = new Object[count];
        this.repositoryCount = count;
        this.suppliers = suppliers;
        this.presenters = presenters;
        this.staticPresenters = builder.staticPresenters;
        this.presenterForViewHolder = new IdentityHashMap<>();
        this.endPositions = new int[count];
        this.dataInvalid = true;
    }

    public final void update() {
        dataInvalid = true;
        notifyDataSetChanged();
    }

    @Override
    public final int getItemCount() {
        if (dataInvalid) {
            int lastEndPosition = 0;
            for (int i = 0; i < repositoryCount; i++) {
                data[i] = suppliers[i].get();
                lastEndPosition += presenters[i].getItemCount(data[i]);
                endPositions[i] = lastEndPosition;
            }
            dataInvalid = false;
        }
        return endPositions[repositoryCount - 1];
    }

    @Override
    public final int getItemViewType(final int position) {
        resolveIndices(position);
        int resolvedRepositoryIndex = this.resolvedRepositoryIndex;
        int resolvedItemIndex = this.resolvedItemIndex;
        return presenters[resolvedRepositoryIndex].getLayoutResId(
                data[resolvedRepositoryIndex], resolvedItemIndex);
    }

    @Override
    public final long getItemId(final int position) {
        resolveIndices(position);
        final int resolvedRepositoryIndex = this.resolvedRepositoryIndex;
        final BaseRenderer<Object> presenter = presenters[resolvedRepositoryIndex];
        final long itemId = presenter.getItemId(data[resolvedRepositoryIndex], this.resolvedItemIndex);
        if (staticPresenters.size() > 0) {
            if (staticPresenters.get(resolvedRepositoryIndex) == null) {
                return itemId + staticPresenters.size();
            }
            return staticPresenters.indexOfKey(resolvedRepositoryIndex);
        }
        return itemId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent,
                                                      final int layoutResourceId) {
        return new RecyclerView.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false)) {};
    }

    @Override
    public final void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        resolveIndices(position);
        int resolvedRepositoryIndex = this.resolvedRepositoryIndex;
        int resolvedItemIndex = this.resolvedItemIndex;
        final BaseRenderer<Object> presenter = presenters[resolvedRepositoryIndex];
        presenterForViewHolder.put(holder, presenter);
        presenter.bind(data[resolvedRepositoryIndex], resolvedItemIndex, holder);
    }

    @Override
    public boolean onFailedToRecycleView(final RecyclerView.ViewHolder holder) {
        recycle(holder);
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onViewRecycled(final RecyclerView.ViewHolder holder) {
        recycle(holder);
    }

    private void recycle(@NonNull final RecyclerView.ViewHolder holder) {
        final BaseRenderer<Object> presenter = presenterForViewHolder.remove(holder);
        if (presenter != null) {
            presenter.recycle(holder);
        }
    }

    private void resolveIndices(final int position) {
        int itemCount = getItemCount();
        if (position < 0 || position >= itemCount) {
            throw new IndexOutOfBoundsException(
                    "Asked for position " + position + " while count is " + itemCount);
        }

        int arrayIndex = Arrays.binarySearch(endPositions, position);
        if (arrayIndex >= 0) {
            do {
                arrayIndex++;
            } while (endPositions[arrayIndex] == position);
        } else {
            arrayIndex = ~arrayIndex;
        }

        resolvedRepositoryIndex = arrayIndex;
        resolvedItemIndex = arrayIndex == 0 ? position : position - endPositions[arrayIndex - 1];
    }

    private static final class LayoutBaseRenderer extends BaseRenderer<Object> {
        private final BaseLayoutRenderer presenter;

        LayoutBaseRenderer(@NonNull final BaseLayoutRenderer presenter) {
            this.presenter = presenter;
        }

        @Override
        public int getItemCount(@NonNull final Object data) {
            return 1;
        }

        @Override
        public int getLayoutResId(@NonNull final Object data, final int index) {
            return presenter.getLayoutResId();
        }

        @Override
        public void bind(@NonNull final Object data, final int index,
                         @NonNull final RecyclerView.ViewHolder holder) {
            presenter.bind(holder.itemView);
        }

        @Override
        public void recycle(@NonNull final RecyclerView.ViewHolder holder) {
            presenter.recycle(holder.itemView);
        }

        @Override
        public long getItemId(@NonNull final Object data, final int index) {
            return 0;
        }
    }
}
