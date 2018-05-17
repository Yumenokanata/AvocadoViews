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
        @IdRes val swipeRefreshLayoutId: Int?,
        @IdRes val emptyViewId: Int?,
        @IdRes val noContentLoadProgressId: Int?) {

    fun bind(context: Context, parent: ViewGroup): LoadingLayoutViews {
        val view = View.inflate(context, layoutResId, parent)
        return LoadingLayoutViews(
                recyclerView = view.findViewById(recyclerViewId),
                swipeRefreshLayout = view.find<SwipeRefreshLayout>(swipeRefreshLayoutId),
                emptyView = view.find(emptyViewId),
                noContentLoadView = view.find<View>(noContentLoadProgressId))
    }

    class Builder(@LayoutRes val layoutResId: Int) {
        private var recyclerViewId: Int = 0
        private var swipeRefreshLayoutId: Int? = null
        private var emptyViewId: Int? = null
        private var noContentLoadProgressId: Int? = null

        fun withRecyclerViewId(@IdRes value: Int): Builder {
            recyclerViewId = value
            return this
        }

        fun withSwipeRefreshLayoutId(@IdRes value: Int): Builder {
            swipeRefreshLayoutId = value
            return this
        }

        fun withEmptyViewId(@IdRes value: Int): Builder {
            emptyViewId = value
            return this
        }

        fun withNoContentLoadProgressId(@IdRes value: Int): Builder {
            noContentLoadProgressId = value
            return this
        }

        fun build(): LoadingLayoutRes {
            return LoadingLayoutRes(
                    layoutResId = layoutResId,
                    recyclerViewId = recyclerViewId,
                    swipeRefreshLayoutId = swipeRefreshLayoutId,
                    emptyViewId = emptyViewId,
                    noContentLoadProgressId = noContentLoadProgressId)
        }
    }

    companion object {
        @JvmStatic
        fun builder(@LayoutRes layoutResId: Int): Builder = Builder(layoutResId)

        @JvmStatic
        fun defaultLayout(): LoadingLayoutRes =
                LoadingLayoutRes(
                        layoutResId = R.layout.double_refresh_recycler_layout,
                        recyclerViewId = R.id.recycler_view,
                        swipeRefreshLayoutId = R.id.swipe_layout,
                        emptyViewId = null,
                        noContentLoadProgressId = R.id.no_content_load_progress_view
                )
    }
}

data class LoadingLayoutViews(
        val recyclerView: RecyclerView,
        val swipeRefreshLayout: SwipeRefreshLayout? = null,
        val emptyView: View? = null,
        val noContentLoadView: View? = null)