package indi.yume.view.avocadoviews.adapterdatabinding;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.experimental.UtilityClass;

import static indi.yume.view.avocadoviews.dsladapter.builder.BaseRendererCompilerStates.*;
import static indi.yume.view.avocadoviews.adapterdatabinding.DataBindingRendererCompilerStates.*;

@UtilityClass
public final class DataBindingRendererBuilder {

    @SuppressWarnings("unchecked")
    @NonNull
    public static <T> RPLayout<T, DBRPMain<T>> dataBindingRepositoryPresenterOf(
            @Nullable final Class<T> type) {
        return new DataBindingBaseRendererCompiler();
    }
}
