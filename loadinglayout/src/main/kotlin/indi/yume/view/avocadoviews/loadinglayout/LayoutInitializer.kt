package indi.yume.view.avocadoviews.loadinglayout

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import io.reactivex.Single

/**
 * Created by yume on 17-4-21.
 */
data class LayoutInitializer(
        val store: Store,
        val adapter: RecyclerView.Adapter<*>,
        val layoutManager: RecyclerView.LayoutManager,
        val loadMoreViewShownPred: (RecyclerView, RecyclerView.Adapter<*>) -> Boolean,
        val doForLoadMoreView: (LoadMoreStatus) -> Unit,
        val renderOtherView: (LoadingState) -> Boolean,
        val showData: (LoadingData<List<*>>) -> Unit) {

    class Builder<VH: RecyclerView.ViewHolder, T> {
        private lateinit var provider: (LoadingData<List<T>>, Int) -> Single<LoadingResult<List<T>>>
        private lateinit var adapter: RecyclerView.Adapter<VH>
        private lateinit var layoutManager: RecyclerView.LayoutManager
        private var loadMoreViewShownPred: (RecyclerView, RecyclerView.Adapter<VH>) -> Boolean = defaultLoadMoreShownPred
        private lateinit var doForLoadMoreView: Effect<LoadMoreStatus>
        private lateinit var showData: Effect<LoadingData<List<T>>>
        private var renderOtherView: (LoadingState) -> Boolean = { false }

        fun provider(provider: (LoadingData<List<T>>, Int) -> Single<LoadingResult<List<T>>>): Builder<VH, T> {
            this.provider = provider
            return this
        }

        fun adapter(adapter: RecyclerView.Adapter<VH>): Builder<VH, T> {
            this.adapter = adapter
            return this
        }

        fun layoutManager(layoutManager: RecyclerView.LayoutManager): Builder<VH, T> {
            this.layoutManager = layoutManager
            return this
        }

        fun loadMoreViewShownPred(loadMoreViewShownPred: (RecyclerView, RecyclerView.Adapter<VH>) -> Boolean)
                : Builder<VH, T> {
            this.loadMoreViewShownPred = loadMoreViewShownPred
            return this
        }

        fun loadMoreViewShownPred(loadMoreView: View): Builder<VH, T> {
            this.loadMoreViewShownPred = { recyclerView, adapter ->
                (0..recyclerView.childCount - 1)
                        .map { recyclerView.childCount - it - 1 }
                        .map { recyclerView.getChildAt(it) }
                        .any { it === loadMoreView }
            }
            return this
        }

        fun doForLoadMoreView(doForLoadMoreView: Effect<LoadMoreStatus>): Builder<VH, T> {
            this.doForLoadMoreView = doForLoadMoreView
            return this
        }

        fun renderOtherView(renderOtherView: (LoadingState) -> Boolean): Builder<VH, T> {
            this.renderOtherView = renderOtherView
            return this
        }

        fun showData(showData: Effect<LoadingData<List<T>>>): Builder<VH, T> {
            this.showData = showData
            return this
        }

        @Suppress("UNCHECKED_CAST")
        fun build(): LayoutInitializer =
                LayoutInitializer(
                        store = Store(RealWorld(provider as (LoadingData<List<*>>, Int) -> Single<LoadingResult<List<*>>>)),
                        adapter = adapter,
                        layoutManager = layoutManager,
                        loadMoreViewShownPred = loadMoreViewShownPred as (RecyclerView, RecyclerView.Adapter<*>) -> Boolean,
                        renderOtherView = renderOtherView,
                        doForLoadMoreView = { doForLoadMoreView.apply(it) },
                        showData = { showData.apply(it as LoadingData<List<T>>) })

        companion object {
            @JvmStatic
            val defaultLoadMoreShownPred: (RecyclerView, RecyclerView.Adapter<*>) -> Boolean =
                    { recyclerView, adapter ->
                        val manager = recyclerView.layoutManager
                        val lastVisiablePos = when(manager) {
                            is LinearLayoutManager -> manager.findLastVisibleItemPosition()
                            is GridLayoutManager -> manager.findLastVisibleItemPosition()
                            else -> recyclerView.run { getChildViewHolder(getChildAt(childCount - 1)).adapterPosition }
                        }

                        adapter.itemCount == lastVisiablePos + 1
                    }
        }
    }

    companion object {
        @JvmStatic
        fun <VH: RecyclerView.ViewHolder, T> builder(): Builder<VH, T> = Builder()
    }
}