package indi.yume.view.avocadoviews.loadinglayout

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AbsListView

/**
 * Created by yume on 18-3-22.
 */

class LoadingCore(val loadingLayoutViews: LoadingLayoutViews) {
    lateinit var manager: LayoutInitializer

    private val onScrollListener: RecyclerView.OnScrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                    && !(loadingLayoutViews.swipeRefreshLayout?.isRefreshing ?: false)
                    && !manager.store.bind().map { it.isRefresh }.first(false).blockingGet())
                manager.store.dispatch(RenderAction)
        }
    }

    init {
        loadingLayoutViews.recyclerView.addOnScrollListener(onScrollListener)
        loadingLayoutViews.swipeRefreshLayout?.setOnRefreshListener { refresh() }
    }

    fun init(initializer: LayoutInitializer) {
        manager = initializer
        init()
    }

    private fun init() {
        loadingLayoutViews.recyclerView.adapter = manager.adapter
        loadingLayoutViews.recyclerView.layoutManager = manager.layoutManager

        manager.store.renderCallback = this::render
        manager.store.dispatch(RenderAction)
    }

    fun loadData() = manager.store.dispatch(LoadNext())

    fun refresh() = manager.store.dispatch(Refresh())

    fun clearData() = manager.store.dispatch(ClearData())

    fun dispatch(action: Action) = manager.store.dispatch(action)

    private fun render(state: LoadingState) {
        if (!manager.renderOtherView(state)) {
                if (state.data is HasData) {
                    loadingLayoutViews.emptyView?.visibility = View.INVISIBLE
                    loadingLayoutViews.swipeRefreshLayout?.apply {
                        visibility = if (state.data.t.isEmpty() && state.isRefresh) View.INVISIBLE else View.VISIBLE
                        isRefreshing = !state.data.t.isEmpty() && state.isRefresh
                    }
                    loadingLayoutViews.recyclerView.apply {
                        visibility = if (state.data.t.isEmpty() && state.isRefresh) View.INVISIBLE else View.VISIBLE
                    }
                    loadingLayoutViews.noContentLoadView?.apply {
                        visibility = if (state.data.t.isEmpty() && state.isRefresh) View.VISIBLE else View.INVISIBLE
                    }
                } else {
                    if (loadingLayoutViews.emptyView == null) {
                        loadingLayoutViews.swipeRefreshLayout?.apply {
                            visibility = if (state.isRefresh && loadingLayoutViews.noContentLoadView != null) View.INVISIBLE else View.VISIBLE
                            isRefreshing = state.isRefresh
                        }
                        loadingLayoutViews.recyclerView.apply {
                            visibility = if (state.isRefresh && loadingLayoutViews.noContentLoadView != null) View.INVISIBLE else View.VISIBLE
                        }
                        loadingLayoutViews.noContentLoadView?.apply {
                            visibility = if (state.isRefresh) View.VISIBLE else View.INVISIBLE
                        }
                    } else {
                        loadingLayoutViews.emptyView.visibility = if (!state.isRefresh) View.VISIBLE else View.INVISIBLE
                        loadingLayoutViews.swipeRefreshLayout?.apply {
                            visibility = if (state.isRefresh && loadingLayoutViews.noContentLoadView != null) View.INVISIBLE else View.VISIBLE
                            isRefreshing = state.isRefresh
                        }
                        loadingLayoutViews.recyclerView.visibility =
                                if (state.isRefresh && loadingLayoutViews.noContentLoadView == null) View.VISIBLE else View.INVISIBLE
                        loadingLayoutViews.noContentLoadView?.apply {
                            visibility = if (state.isRefresh) View.VISIBLE else View.INVISIBLE
                        }
                    }
                }
        }

        manager.showData(state.data)
        manager.doForLoadMoreView.apply {
            when {
                !state.enableLoadMore || !state.hasMore || state.data.fold({ true }, { it.isEmpty() }) || state.isRefresh -> invoke(LoadMoreStatus.DISABLE)
                state.isLoadingMore -> invoke(LoadMoreStatus.LOADING)
                else ->
                    if(manager.loadMoreViewShownPred(loadingLayoutViews.recyclerView,
                                    manager.adapter))
                        invoke(LoadMoreStatus.NORMAL)
                    else
                        invoke(LoadMoreStatus.INVISIBLE)
            }
        }
    }

    fun onDetachedFromWindow() {
        manager.store.unsubscribe()
    }

    companion object {
        @JvmStatic
        var enableLog = BuildConfig.DEBUG
    }
}