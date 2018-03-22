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

    private lateinit var core: LoadingCore

    init {
        val loadingLayoutRes = LoadingLayoutRes.defaultLayout()
        loadingLayoutViews = loadingLayoutRes.bind(context, this)
    }

    fun init(layoutManager: LayoutInitializer) {
        this.core = LoadingCore(loadingLayoutViews, layoutManager)
    }

    fun loadData() = core.loadData()

    fun refresh() = core.refresh()

    override fun onDetachedFromWindow() {
        core.onDetachedFromWindow()
        super.onDetachedFromWindow()
    }
}