package indi.yume.view.avocadoviews.dsladapter.functions;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Created by yume on 17-4-20.
 */
public class Functions {
    public static <T1, T2> BiConsumer<T1, T2> nullBiConsumer() {
        return new BiConsumer<T1, T2>() {
            @Override
            public void accept(Object o, Object o2) {}
        };
    }

    public static <T> Consumer<T> nullConsumer() {
        return new Consumer<T>() {
            @Override
            public void accept(@NonNull T t) {}
        };
    }

    public static <T> Supplier<T> staticSupplier(final T data) {
        return new Supplier<T>() {
            @Override
            public T get() {
                return data;
            }
        };
    }

    public static <T, R> Function<T, R> staticFunction(final R r) {
        return new Function<T, R>() {
            @NonNull
            @Override
            public R apply(@NonNull T t) {
                return r;
            }
        };
    }

    public static <T> Function<T, T>  identityFunction() {
        return new Function<T, T>() {

            @NonNull
            @Override
            public T apply(@NonNull T t) {
                return t;
            }
        };
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public static <T> Function<T, List<T>> itemAsList() {
        return new Function<T, List<T>>() {

            @NonNull
            @Override
            public List<T> apply(@NonNull T t) {
                return Collections.singletonList(t);
            }
        };
    }
}
