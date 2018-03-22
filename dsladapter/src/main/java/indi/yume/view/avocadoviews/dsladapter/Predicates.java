package indi.yume.view.avocadoviews.dsladapter;

import android.support.annotation.NonNull;

/**
 * Created by yume on 17-4-20.
 */
public class Predicates {
    public static void checkArgument(final boolean expression, @NonNull final String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    public static <T> T checkNotNull(@NonNull final T object) {
        if (object == null) {
            throw new NullPointerException();
        }
        return object;
    }
}
