package indi.yume.view.avocadoviews.loadinglayout

import io.reactivex.Observable

/**
 * Created by yume on 17-4-20.
 */

sealed class Action {
    abstract fun effect(realWorld: RealWorld, oldState: LoadingState): Observable<LoadingState>
}

fun LoadingResult<List<*>>.dealResult(oldState: LoadingState,
                                      onSuccess: (List<*>, LoadingState) -> LoadingState): LoadingState =
        when(this) {
            is Success -> onSuccess(data, oldState)
            is LastData -> onSuccess(data, oldState).copy(hasMore = false)
            is NoMoreData -> oldState.copy(hasMore = false)
            is Failure -> oldState
        }

object EmptyAction : Action() {
    override fun effect(realWorld: RealWorld, oldState: LoadingState): Observable<LoadingState> =
            Observable.empty()
}

object RenderAction : Action() {
    override fun effect(realWorld: RealWorld, oldState: LoadingState): Observable<LoadingState> =
            Observable.just(oldState)
}

data class RestoreAction(val state: LoadingState) : Action() {
    override fun effect(realWorld: RealWorld, oldState: LoadingState): Observable<LoadingState> =
            Observable.just(state)
}

data class ModifyAction(val firstPageNum: Int = 0,
                        val enableLoadMore: Boolean = true) : Action() {
    override fun effect(realWorld: RealWorld, oldState: LoadingState): Observable<LoadingState> =
            Observable.just(oldState.copy(firstPageNum = firstPageNum,
                    enableLoadMore = enableLoadMore))
}

class LoadNext : Action() {
    override fun effect(realWorld: RealWorld, oldState: LoadingState): Observable<LoadingState> {
        val nextPage = oldState.pageNumber + 1
        if(oldState.isLoadingMore || oldState.isRefresh)
            return Observable.empty()

        return Observable.concat(Observable.just(oldState.copy(isLoadingMore = true)),
                realWorld.provider(nextPage)
                        .map { result ->
                            result.dealResult (oldState) { data, state ->
                                state.copy(pageNumber = nextPage,
                                        data = state.data + data,
                                        isLoadingMore = false)
                            }
                        }
                        .toObservable())
    }
}

class Refresh : Action() {
    override fun effect(realWorld: RealWorld, oldState: LoadingState): Observable<LoadingState> {
        val nextPage = oldState.firstPageNum
        if(oldState.isLoadingMore || oldState.isRefresh)
            return Observable.empty()

        return Observable.concat(Observable.just(oldState.copy(isRefresh = true)),
                realWorld.provider(nextPage)
                        .map { result ->
                            result.dealResult (oldState) { data, state ->
                                state.copy(pageNumber = nextPage,
                                        data = data,
                                        isRefresh = false)
                            }
                        }
                        .toObservable())
    }
}