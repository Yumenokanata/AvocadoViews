package indi.yume.view.avocadoviews.adapterdatabinding;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import indi.yume.view.avocadoviews.dsladapter.BaseRenderer;
import indi.yume.view.avocadoviews.dsladapter.functions.Function;

import static indi.yume.view.avocadoviews.adapterdatabinding.RecycleConfig.*;
import static indi.yume.view.avocadoviews.dsladapter.functions.Functions.identityFunction;
import static indi.yume.view.avocadoviews.dsladapter.functions.Functions.itemAsList;
import static indi.yume.view.avocadoviews.dsladapter.functions.Functions.staticFunction;
import static indi.yume.view.avocadoviews.dsladapter.Predicates.checkNotNull;
import static java.util.Collections.emptyList;
import static indi.yume.view.avocadoviews.adapterdatabinding.DataBindingRendererCompilerStates.*;
import static indi.yume.view.avocadoviews.dsladapter.builder.BaseRendererCompilerStates.*;

@SuppressWarnings("unchecked")
final class DataBindingBaseRendererCompiler
        implements DBRPMain, RPLayout, RPTypedCollectionCompile {
    private static final int BR_NO_ID = -1;
    @NonNull
    private final SparseArray<Object> handlers;
    private Function<Object, Integer> layoutFactory;
    private Set<Pair<Function<Object, Integer>, Function<Object, Object>>> itemSetters = new HashSet<>();
    private int collectionId = BR_NO_ID;
    @NonNull
    private Function<Object, Long> stableIdForItem = staticFunction(RecyclerView.NO_ID);
    @RecycleConfig
    private int recycleConfig = DO_NOTHING;

    DataBindingBaseRendererCompiler() {
        this.handlers = new SparseArray<>();
    }

    @NonNull
    @Override
    public DBRPMain handler(final int handlerId, @NonNull final Object handler) {
        handlers.put(handlerId, handler);
        return this;
    }

    @NonNull
    @Override
    public DBRPMain itemId(final int itemId) {
        this.itemSetters.add(new Pair<>(staticFunction(itemId), identityFunction()));
        return this;
    }

    @NonNull
    @Override
    public DBRPMain itemId(int itemId, Function mapper) {
        this.itemSetters.add(new Pair<>(staticFunction(itemId), (Function<Object, Object>) mapper));
        return this;
    }

    @NonNull
    @Override
    public DBRPMain itemIdForItem(@NonNull final Function itemIdForItem) {
        this.itemSetters.add(new Pair<>(checkNotNull((Function<Object, Integer>)itemIdForItem), identityFunction()));
        return this;
    }

    @NonNull
    @Override
    public DBRPMain itemIdForItem(@NonNull Function itemIdForItem, @NonNull Function mapper) {
        this.itemSetters.add(new Pair<>(checkNotNull((Function<Object, Integer>)itemIdForItem), (Function<Object, Object>) mapper));
        return this;
    }

    @NonNull
    @Override
    public BaseRenderer forItem() {
        return new CompiledRepositoryPresenter(itemSetters, layoutFactory, stableIdForItem,
                handlers, recycleConfig, itemAsList(), collectionId);
    }

    @NonNull
    @Override
    public BaseRenderer<List<Object>> forList() {
        return new CompiledRepositoryPresenter(itemSetters, layoutFactory, stableIdForItem,
                handlers, recycleConfig, (Function) identityFunction(), collectionId);
    }

    @NonNull
    @Override
    public BaseRenderer forCollection(@NonNull final Function converter) {
        return new CompiledRepositoryPresenter(itemSetters, layoutFactory, stableIdForItem, handlers,
                recycleConfig, converter, collectionId);
    }

    @NonNull
    @Override
    public Object layout(@LayoutRes int layoutId) {
        this.layoutFactory = staticFunction(layoutId);
        return this;
    }

    @NonNull
    @Override
    public Object layoutForItem(@NonNull final Function layoutForItem) {
        this.layoutFactory = checkNotNull(layoutForItem);
        return this;
    }

    @NonNull
    @Override
    public DBRPMain stableIdForItem(@NonNull final Function stableIdForItem) {
        this.stableIdForItem = stableIdForItem;
        return this;
    }

    @NonNull
    @Override
    public RPItemCompile stableId(final long stableId) {
        this.stableIdForItem = staticFunction(stableId);
        return this;
    }

    @NonNull
    @Override
    public DBRPMain onRecycle(@RecycleConfig final int recycleConfig) {
        this.recycleConfig = recycleConfig;
        return this;
    }

    @NonNull
    @Override
    public DBRPMain collectionId(final int collectionId) {
        this.collectionId = collectionId;
        return this;
    }

    private static final class CompiledRepositoryPresenter extends BaseRenderer {
        @NonNull
        private final Set<Pair<Function<Object, Integer>, Function<Object, Object>>> itemSetters;
        @NonNull
        private final Function<Object, List<Object>> converter;
        @NonNull
        private final Function<Object, Integer> layoutId;
        @NonNull
        private final Function<Object, Long> stableIdForItem;
        @RecycleConfig
        private final int recycleConfig;
        private final int collectionId;
        @NonNull
        private SparseArray<Object> handlers;
        @NonNull
        private WeakReference<Object> dataRef = new WeakReference<>(null);
        @NonNull
        private List items = emptyList();

        CompiledRepositoryPresenter(
                @NonNull final Set<Pair<Function<Object, Integer>, Function<Object, Object>>> itemSetters,
                @NonNull final Function<Object, Integer> layoutId,
                @NonNull final Function<Object, Long> stableIdForItem,
                @NonNull final SparseArray<Object> handlers,
                final int recycleConfig,
                @NonNull final Function<Object, List<Object>> converter,
                @NonNull final int collectionId) {
            this.itemSetters = itemSetters;
            this.collectionId = collectionId;
            this.converter = converter;
            this.layoutId = layoutId;
            this.stableIdForItem = stableIdForItem;
            this.recycleConfig = recycleConfig;
            this.handlers = handlers;
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
            final View view = holder.itemView;
            final ViewDataBinding viewDataBinding = DataBindingUtil.bind(view);
            for(Pair<Function<Object, Integer>, Function<Object, Object>> pair : itemSetters) {
                final Integer itemVariable = pair.first.apply(item);
                if (itemVariable != BR_NO_ID) {
                    viewDataBinding.setVariable(itemVariable, pair.second.apply(item));
                    view.setTag(R.id.avocado__adapterdatabinding__item_id, itemVariable);
                }
            }
            if (collectionId != BR_NO_ID) {
                viewDataBinding.setVariable(collectionId, data);
                view.setTag(R.id.avocado__adapterdatabinding__collection_id, collectionId);
            }
            for (int i = 0; i < handlers.size(); i++) {
                final int variableId = handlers.keyAt(i);
                viewDataBinding.setVariable(variableId, handlers.valueAt(i));
            }
            viewDataBinding.executePendingBindings();
        }

        @Override
        public void recycle(@NonNull final RecyclerView.ViewHolder holder) {
            if (recycleConfig != 0) {
                final View view = holder.itemView;
                final ViewDataBinding viewDataBinding = DataBindingUtil.bind(view);
                if ((recycleConfig & CLEAR_ITEM) != 0) {
                    final Object tag = view.getTag(R.id.avocado__adapterdatabinding__item_id);
                    view.setTag(R.id.avocado__adapterdatabinding__item_id, null);
                    if (tag instanceof Integer) {
                        viewDataBinding.setVariable((int) tag, null);
                    }
                }
                if ((recycleConfig & CLEAR_COLLECTION) != 0) {
                    final Object collectionTag = view.getTag(R.id.avocado__adapterdatabinding__collection_id);
                    view.setTag(R.id.avocado__adapterdatabinding__collection_id, null);
                    if (collectionTag instanceof Integer) {
                        viewDataBinding.setVariable((int) collectionTag, null);
                    }
                }
                if ((recycleConfig & CLEAR_HANDLERS) != 0) {
                    for (int i = 0; i < handlers.size(); i++) {
                        viewDataBinding.setVariable(handlers.keyAt(i), null);
                    }
                }
                viewDataBinding.executePendingBindings();
            }
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
