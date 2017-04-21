package indi.yume.view.avocadoviews.dsladapter.builder;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import indi.yume.view.avocadoviews.dsladapter.functions.Consumer;

import static indi.yume.view.avocadoviews.dsladapter.functions.Functions.nullConsumer;

/**
 * Created by yume on 17-4-20.
 */

public abstract class BaseLayoutRenderer {

    @LayoutRes
    public abstract int getLayoutResId();

    public abstract void bind(@NonNull final View view);

    public void recycle(@NonNull final View view) {}


    public static final class Builder {
        @NonNull
        private Consumer recycler = nullConsumer();
        @NonNull
        private Consumer<View> updater = nullConsumer();
        @LayoutRes
        private final int layoutId;

        Builder(@LayoutRes final int layoutId) {
            this.layoutId = layoutId;
        }

        @NonNull
        public Builder bindWith(@NonNull final Consumer<View> binder) {
            this.updater = binder;
            return this;
        }

        @NonNull
        public Builder recycleWith(@NonNull final Consumer<View> recycler) {
            this.recycler = recycler;
            return this;
        }

        @NonNull
        public BaseLayoutRenderer build() {
            return new CompiledLayoutPresenter(layoutId, recycler, updater);
        }
    }

    private static final class CompiledLayoutPresenter extends BaseLayoutRenderer {
        @LayoutRes
        private int layoutId;
        @NonNull
        private Consumer<View> recycler;
        @NonNull
        private Consumer<View> updater;

        CompiledLayoutPresenter(
                @LayoutRes final int layoutId,
                @NonNull final Consumer<View> recycler,
                @NonNull final Consumer<View> updater) {
            this.layoutId = layoutId;
            this.recycler = recycler;
            this.updater = updater;
        }

        @Override
        public int getLayoutResId() {
            return layoutId;
        }

        @Override
        public void bind(@NonNull final View view) {
            updater.accept(view);
        }

        @Override
        public void recycle(@NonNull final View view) {
            recycler.accept(view);
        }
    }
}
