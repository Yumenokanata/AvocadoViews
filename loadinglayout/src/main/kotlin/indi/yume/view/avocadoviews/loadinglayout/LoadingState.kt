package indi.yume.view.avocadoviews.loadinglayout

import io.reactivex.Single

/**
 * Created by yume on 17-4-20.
 */

data class LoadingState (
        val firstPageNum: Int = 0,
        val enableLoadMore: Boolean = true,

        val pageNumber: Int = firstPageNum,
        val data: List<*> = emptyList<Any>(),
        val hasMore: Boolean = true,
        val isRefresh: Boolean = false,
        val isLoadingMore: Boolean = false
) {
    companion object {
        fun empty(): LoadingState = LoadingState()
    }
}

data class RealWorld(
        val provider: (Int) -> Single<LoadingResult<List<*>>>
)

