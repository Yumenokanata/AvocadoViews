package indi.yume.view.avocadoviews.dsladapter.builder;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.List;

import indi.yume.view.avocadoviews.dsladapter.BaseRenderer;
import indi.yume.view.avocadoviews.dsladapter.functions.BiConsumer;
import indi.yume.view.avocadoviews.dsladapter.functions.Consumer;
import indi.yume.view.avocadoviews.dsladapter.functions.Function;

import static indi.yume.view.avocadoviews.dsladapter.functions.Functions.identityFunction;
import static indi.yume.view.avocadoviews.dsladapter.functions.Functions.itemAsList;
import static indi.yume.view.avocadoviews.dsladapter.functions.Functions.nullBiConsumer;
import static indi.yume.view.avocadoviews.dsladapter.functions.Functions.nullConsumer;
import static indi.yume.view.avocadoviews.dsladapter.functions.Functions.staticFunction;
import static indi.yume.view.avocadoviews.dsladapter.Predicates.checkNotNull;
import static java.util.Collections.emptyList;

/**
 * Created by yume on 17-4-20.
 */

@SuppressWarnings({"unchecked, rawtypes"})
public final class RenderCompiler implements BaseRendererCompilerStates.RPLayout, BaseRendererCompilerStates.RPMain, BaseRendererCompilerStates.RPTypedCollectionCompile {
    @NonNull
    private Function<Object, Integer> layoutForItem;
    @NonNull
    private BiConsumer binder = nullBiConsumer();
    @NonNull
    private Consumer recycler = nullConsumer();
    @NonNull
    private Function<Object, Long> stableIdForItem = staticFunction(RecyclerView.NO_ID);
    @NonNull
    private BiConsumer collectionBiConsumer = nullBiConsumer();

    @NonNull
    @Override
    public BaseRenderer forItem() {
        return new CompiledRenderer(layoutForItem, binder, stableIdForItem, recycler,
                itemAsList(), collectionBiConsumer);
    }

    @NonNull
    @Override
    public BaseRenderer<List> forList() {
        return new CompiledRenderer(layoutForItem, binder, stableIdForItem, recycler,
                (Function) identityFunction(), collectionBiConsumer);
    }

    @NonNull
    @Override
    public BaseRendererCompilerStates.RPTypedCollectionCompile bindCollectionWith(@NonNull final BiConsumer collectionBiConsumer) {
        this.collectionBiConsumer = collectionBiConsumer;
        return this;
    }

    @NonNull
    @Override
    public BaseRenderer forCollection(@NonNull final Function converter) {
        return new CompiledRenderer(layoutForItem, binder, stableIdForItem, recycler,
                converter, collectionBiConsumer);
    }

    @NonNull
    @Override
    public Object layout(@LayoutRes final int layoutId) {
        this.layoutForItem = staticFunction(layoutId);
        return this;
    }

    @NonNull
    @Override
    public Object layoutForItem(@NonNull final Function layoutForItem) {
        this.layoutForItem = checkNotNull(layoutForItem);
        return this;
    }

    @NonNull
    @Override
    public BaseRendererCompilerStates.RPMain bindWith(@NonNull final BiConsumer binder) {
        this.binder = binder;
        return this;
    }

    @NonNull
    @Override
    public BaseRendererCompilerStates.RPMain stableIdForItem(@NonNull final Function stableIdForItem) {
        this.stableIdForItem = stableIdForItem;
        return this;
    }

    @NonNull
    @Override
    public BaseRendererCompilerStates.RPItemCompile stableId(final long stableId) {
        this.stableIdForItem(staticFunction(stableId));
        return this;
    }

    @NonNull
    @Override
    public BaseRendererCompilerStates.RPMain recycleWith(@NonNull final Consumer recycler) {
        this.recycler = recycler;
        return this;
    }

    private static final class CompiledRenderer extends BaseRenderer {
        @NonNull
        private final Function<Object, List<Object>> converter;
        @NonNull
        private final BiConsumer<Object, View> collectionBiConsumer;
        @NonNull
        private final Function<Object, Integer> layoutId;
        @NonNull
        private final BiConsumer<Object, View> binder;
        @NonNull
        private final Function<Object, Long> stableIdForItem;
        @NonNull
        private final Consumer<View> recycler;
        @NonNull
        private WeakReference<Object> dataRef = new WeakReference<>(null);
        @NonNull
        private List items = emptyList();

        CompiledRenderer(
                @NonNull final Function<Object, Integer> layoutId,
                @NonNull final BiConsumer<Object, View> binder,
                @NonNull final Function<Object, Long> stableIdForItem,
                @NonNull final Consumer<View> recycler,
                @NonNull final Function<Object, List<Object>> converter,
                @NonNull final BiConsumer<Object, View> collectionBiConsumer) {
            this.collectionBiConsumer = collectionBiConsumer;
            this.converter = converter;
            this.layoutId = layoutId;
            this.binder = binder;
            this.stableIdForItem = stableIdForItem;
            this.recycler = recycler;
        }

        @Override
        public int getItemCount(@NonNull final Object data) {
            return getItems(data).size();
        }

        @Override
        public int getLayoutResId(@NonNull final Object data, final int index) {
            return layoutId.apply(getItems(data).get(index));
        }

        @Override
        public void bind(@NonNull final Object data, final int index,
                         @NonNull final RecyclerView.ViewHolder holder) {
            final Object item = getItems(data).get(index);
            collectionBiConsumer.accept(data, holder.itemView);
            binder.accept(item, holder.itemView);
        }

        @Override
        public void recycle(@NonNull final RecyclerView.ViewHolder holder) {
            recycler.accept(holder.itemView);
        }

        @Override
        public long getItemId(@NonNull final Object data, final int index) {
            return stableIdForItem.apply(getItems(data).get(index));
        }

        @NonNull
        private List getItems(@NonNull final Object data) {
            if (this.dataRef.get() != data) {
                items = converter.apply(data);
                this.dataRef = new WeakReference<>(data);
            }
            return items;
        }
    }
}
