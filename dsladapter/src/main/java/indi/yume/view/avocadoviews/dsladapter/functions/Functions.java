package indi.yume.view.avocadoviews.dsladapter.functions;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Created by yume on 17-4-20.
 */
public class Functions {
    public static <T1, T2> BiConsumer<T1, T2> nullBiConsumer() {
        return (t1, t2) -> {};
    }

    public static <T> Consumer<T> nullConsumer() {
        return t -> {};
    }

    public static <T> Supplier<T> staticSupplier(T data) {
        return () -> data;
    }

    public static <T, R> Function<T, R> staticFunction(R r) {
        return t -> r;
    }

    public static <T> Function<T, T>  identityFunction() {
        return t -> t;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public static <T> Function<T, List<T>> itemAsList() {
        return Collections::singletonList;
    }
}
