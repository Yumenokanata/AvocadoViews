package indi.yume.view.avocadoviews.adapterdatabinding;

import android.support.annotation.NonNull;

import java.util.List;

import indi.yume.view.avocadoviews.dsladapter.BaseRenderer;
import indi.yume.view.avocadoviews.dsladapter.builder.BaseRendererCompilerStates;
import indi.yume.view.avocadoviews.dsladapter.functions.Function;

public interface DataBindingRendererCompilerStates {

  interface DBRPMain<T> extends BaseRendererCompilerStates.RPItemCompile<T> {

    @NonNull
    DBRPMain<T> itemId(int itemId);

    @NonNull
    DBRPMain<T> itemIdForItem(@NonNull Function<T, Integer> itemIdForItem);

    @NonNull
    DBRPMain<T> handler(int handlerId, @NonNull Object handler);

    @NonNull
    DBRPMain<T> onRecycle(@RecycleConfig int recycleConfig);

    @NonNull
    DBRPMain<T> collectionId(int collectionId);

    @NonNull
    DBRPMain<T> stableIdForItem(@NonNull Function<? super T, Long> stableIdForItem);

    @NonNull
    BaseRendererCompilerStates.RPItemCompile<T> stableId(long stableId);

    @NonNull
    BaseRenderer<List<T>> forList();

    @NonNull
    <TCol> BaseRenderer<TCol> forCollection(@NonNull Function<TCol, List<T>> converter);
  }
}

