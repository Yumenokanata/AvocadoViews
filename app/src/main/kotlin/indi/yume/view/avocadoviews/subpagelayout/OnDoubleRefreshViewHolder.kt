package indi.yume.view.avocadoviews.subpagelayout

import android.view.View

/**
 * Created by yume on 16-5-12.
 */
interface OnDoubleRefreshViewHolder {
    fun getView(): View

    fun onLoading()
    fun onNoContents()
    fun onNotReachability()
    fun onReachability()
}