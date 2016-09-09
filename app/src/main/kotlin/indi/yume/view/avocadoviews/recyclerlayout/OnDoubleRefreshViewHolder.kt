package indi.yume.view.avocadoviews.recyclerlayout

import android.view.View

/**
 * Created by yume on 16-5-12.
 */
interface OnDoubleRefreshViewHolder {
    fun getView(): View

    fun onLoading()
    fun onNoContents(): Boolean
    fun onNotReachability(): Boolean
    fun onReachability()
}