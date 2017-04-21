package indi.yume.view.avocadoviews.dsladapter.functions;

import android.support.annotation.NonNull;

/**
 * Created by yume on 17-4-20.
 */

public interface Function<T, R> {
    @NonNull
    R apply(@NonNull T t);
}
