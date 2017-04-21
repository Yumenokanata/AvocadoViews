package indi.yume.view.avocadoviews.dsladapter.builder;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

import indi.yume.view.avocadoviews.dsladapter.BaseRenderer;
import indi.yume.view.avocadoviews.dsladapter.functions.BiConsumer;
import indi.yume.view.avocadoviews.dsladapter.functions.Consumer;
import indi.yume.view.avocadoviews.dsladapter.functions.Function;

/**
 * Created by yume on 17-4-20.
 */

public interface BaseRendererCompilerStates {
    interface RPLayout<TVal, TRet> {
        @NonNull
        TRet layout(@LayoutRes int layoutId);

        @NonNull
        TRet layoutForItem(@NonNull Function<TVal, Integer> layoutForItem);
    }

    interface RPItemCompile<TVal> {

        @NonNull
        BaseRenderer<TVal> forItem();
    }

    interface RPTypedCollectionCompile<TVal, TCol> {
        @NonNull
        <TColE extends TCol> BaseRenderer<TColE> forCollection(
                @NonNull Function<TColE, List<TVal>> converter);
    }

    interface RPMain<T> extends RPItemCompile<T> {

        @NonNull
        RPMain<T> bindWith(@NonNull BiConsumer<T, View> viewBiConsumer);

        @NonNull
        RPMain<T> recycleWith(@NonNull Consumer<View> recycler);

        @NonNull
        RPMain<T> stableIdForItem(@NonNull Function<? super T, Long> stableIdForItem);

        @NonNull
        RPItemCompile<T> stableId(long stableId);

        @NonNull
        <TCol> RPTypedCollectionCompile<T, TCol> bindCollectionWith(
                @NonNull BiConsumer<TCol, View> collectionBiConsumer);

        @NonNull
        BaseRenderer<List<T>> forList();

        @NonNull
        <TCol> BaseRenderer<TCol> forCollection(@NonNull Function<TCol, List<T>> converter);
    }
}
