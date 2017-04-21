package indi.yume.view.avocadoviews.loadinglayout

import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Created by yume on 17-4-21.
 */
data class LoadingLayoutRes(
        @LayoutRes val layoutResId: Int,
        @IdRes val recyclerViewId: Int,
        @IdRes val swipeRefreshLayoutId: Int,
        @IdRes val noContentLoadProgressId: Int) {

    fun bind(context: Context, parent: ViewGroup): LoadingLayoutViews {
        val view = View.inflate(context, layoutResId, parent)
        return LoadingLayoutViews(
                layout = view,
                recyclerView = view.find<RecyclerView>(recyclerViewId),
                swipeRefreshLayout = view.find<SwipeRefreshLayout>(swipeRefreshLayoutId),
                noContentLoadProgress = view.find<View>(noContentLoadProgressId))
    }

    class Builder {
        private var layoutResId: Int = 0
        private var recyclerViewId: Int = 0
        private var swipeRefreshLayoutId: Int = 0
        private var noContentLoadProgressId: Int = 0

        fun withLayoutResId(value: Int): Builder {
            layoutResId = value
            return this
        }

        fun withRecyclerViewId(value: Int): Builder {
            recyclerViewId = value
            return this
        }

        fun withSwipeRefreshLayoutId(value: Int): Builder {
            swipeRefreshLayoutId = value
            return this
        }

        fun withNoContentLoadProgressId(value: Int): Builder {
            noContentLoadProgressId = value
            return this
        }

        fun build(): LoadingLayoutRes {
            return LoadingLayoutRes(
                    layoutResId = layoutResId,
                    recyclerViewId = recyclerViewId,
                    swipeRefreshLayoutId = swipeRefreshLayoutId,
                    noContentLoadProgressId = noContentLoadProgressId)
        }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()

        @JvmStatic
        fun defaultLayout(): LoadingLayoutRes =
                LoadingLayoutRes(
                        layoutResId = R.layout.double_refresh_recycler_layout,
                        recyclerViewId = R.id.recycler_view,
                        swipeRefreshLayoutId = R.id.swipe_layout,
                        noContentLoadProgressId = R.id.no_content_load_progress_view
                )
    }
}

data class LoadingLayoutViews(
        val layout: View,
        val recyclerView: RecyclerView,
        val swipeRefreshLayout: SwipeRefreshLayout,
        val noContentLoadProgress: View)