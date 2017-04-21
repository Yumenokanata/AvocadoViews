package indi.yume.view.avocadoviews.loadinglayout

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.FrameLayout

/**
 * Created by yume on 17-4-20.
 */

class LoadingLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    constructor(context: Context): this(context, null)

    val loadingLayoutViews: LoadingLayoutViews

    private lateinit var manager: LayoutInitializer

    init {
        val loadingLayoutRes = LoadingLayoutRes.defaultLayout()
        loadingLayoutViews = loadingLayoutRes.bind(context, this)
    }

    fun init(layoutManager: LayoutInitializer) {
        this.manager = layoutManager
        init()
    }

    fun loadData() = manager.store.dispatch(LoadNext())

    fun refresh() = manager.store.dispatch(Refresh())

    private fun init() {
        loadingLayoutViews.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && !loadingLayoutViews.swipeRefreshLayout.isRefreshing)
                    manager.store.dispatch(RenderAction)
            }
        })
        loadingLayoutViews.swipeRefreshLayout.setOnRefreshListener { refresh() }
        loadingLayoutViews.recyclerView.adapter = manager.adapter
        loadingLayoutViews.recyclerView.layoutManager = manager.layoutManager
        manager.store.renderCallback = this::render

        manager.store.dispatch(RenderAction)
    }

    override fun onDetachedFromWindow() {
        manager.store.unsubscribe()
        super.onDetachedFromWindow()
    }

    private fun render(state: LoadingState) {
        loadingLayoutViews.swipeRefreshLayout.apply {
            visibility = if(state.data.isEmpty() && state.isRefresh) View.INVISIBLE else View.VISIBLE
            isRefreshing = !state.data.isEmpty() && state.isRefresh
        }
        loadingLayoutViews.noContentLoadProgress.apply {
            visibility = if(state.data.isEmpty() && state.isRefresh) View.VISIBLE else View.INVISIBLE
        }
        manager.showData(state.data)
        manager.doForLoadMoreView.apply {
            when {
                !state.enableLoadMore || !state.hasMore || state.data.isEmpty() || state.isRefresh -> invoke(LoadMoreStatus.DISABLE)
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
}