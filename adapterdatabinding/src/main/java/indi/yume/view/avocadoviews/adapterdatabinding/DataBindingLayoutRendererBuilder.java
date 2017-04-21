package indi.yume.view.avocadoviews.adapterdatabinding;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;

import indi.yume.view.avocadoviews.dsladapter.builder.BaseLayoutRenderer;

import static indi.yume.view.avocadoviews.adapterdatabinding.RecycleConfig.CLEAR_HANDLERS;
import static indi.yume.view.avocadoviews.adapterdatabinding.RecycleConfig.DO_NOTHING;

public final class DataBindingLayoutRendererBuilder {

  @SuppressWarnings("unchecked")
  @NonNull
  public static Builder dataBindingLayoutPresenterFor(@LayoutRes final int layoutId) {
    return new Builder(layoutId);
  }

  @SuppressWarnings("unchecked")
  public static final class Builder {
    @NonNull
    private final SparseArray<Object> handlers;
    @LayoutRes
    private final int layoutId;
    @RecycleConfig
    private int recycleConfig = DO_NOTHING;

    private Builder(final int layoutId) {
      this.layoutId = layoutId;
      this.handlers = new SparseArray<>();
    }

    @NonNull
    public Builder handler(final int handlerId, @NonNull final Object handler) {
      handlers.put(handlerId, handler);
      return this;
    }

    @NonNull
    public Builder onRecycle(@RecycleConfig final int recycleConfig) {
      this.recycleConfig = recycleConfig;
      return this;
    }

    @NonNull
    public BaseLayoutRenderer build() {
      return new DataBindingLayoutPresenter(handlers, layoutId, recycleConfig);
    }

    private static class DataBindingLayoutPresenter extends BaseLayoutRenderer {
      @NonNull
      private final SparseArray<Object> handlers;
      private final int layoutId;
      private final int recycleConfig;

      DataBindingLayoutPresenter(@NonNull final SparseArray<Object> handlers,
                                 final int layoutId, final int recycleConfig) {
        this.handlers = handlers;
        this.layoutId = layoutId;
        this.recycleConfig = recycleConfig;
      }

      @Override
      public int getLayoutResId() {
        return layoutId;
      }

      @Override
      public void bind(@NonNull final View view) {
        final ViewDataBinding viewDataBinding = DataBindingUtil.bind(view);
        for (int i = 0; i < handlers.size(); i++) {
          final int variableId = handlers.keyAt(i);
          viewDataBinding.setVariable(variableId, handlers.get(variableId));
        }
        viewDataBinding.executePendingBindings();
      }

      @Override
      public void recycle(@NonNull final View view) {
        if ((recycleConfig & CLEAR_HANDLERS) != 0) {
          final ViewDataBinding viewDataBinding = DataBindingUtil.bind(view);
          for (int i = 0; i < handlers.size(); i++) {
            viewDataBinding.setVariable(handlers.keyAt(i), null);
          }
          viewDataBinding.executePendingBindings();
        }
      }
    }
  }

  private DataBindingLayoutRendererBuilder() {}
}
