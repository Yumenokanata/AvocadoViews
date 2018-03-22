package indi.yume.view.avocadoviews.dsladapter.builder;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static indi.yume.view.avocadoviews.dsladapter.builder.BaseRendererCompilerStates.*;

/**
 * Created by yume on 17-4-20.
 *
 */
public class RendererBuilder {
    @SuppressWarnings({"unchecked", "UnusedParameters"})
    @NonNull
    public static <T> RPLayout<T, RPMain<T>> rendererOf(@Nullable final Class<T> type) {
        return new RenderCompiler();
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public static BaseLayoutRenderer.Builder layoutPresenterFor(@LayoutRes int layoutId) {
        return new BaseLayoutRenderer.Builder(layoutId);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public static BaseLayoutRenderer layout(@LayoutRes int layoutId) {
        return new BaseLayoutRenderer.Builder(layoutId).build();
    }
}
