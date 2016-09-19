@file:JvmName("SavedStatusUtil")

package indi.yume.view.avocadoviews.recyclerlayout

import android.os.Bundle
import indi.yume.tools.adapter_renderer.recycler.RendererAdapter
import rx.Observable
import java.util.*

/**
 * Created by yume on 16-9-19.
 */
data class SavedStatus(val subPageAdapter: SubPageAdapter<out Any>?,
                       val enableLoadMore: Boolean,
                       val autoLoadMore: Boolean,
                       val canLoadMoreFlag: Boolean)

fun DoubleRefreshRecyclerLayout.saveStatus(): SavedStatus =
        SavedStatus(listAdapter, enableLoadMore, autoLoadMore, canLoadMoreFlag)

fun DoubleRefreshRecyclerLayout.restoreStatus(savedStatus: SavedStatus) {
    enableLoadMore = savedStatus.enableLoadMore
    autoLoadMore = savedStatus.autoLoadMore
    canLoadMoreFlag = savedStatus.canLoadMoreFlag
    savedStatus.subPageAdapter?.let { initData(it) }
}

private val ENABLE_LOAD_MORE = "enableLoadMore"
private val AUTO_LOAD_MORE = "autoLoadMore"
private val CAN_LOAD_MORE_FLAG = "canLoadMoreFlag"
private val PAGE_NUM = "pageNum"
private val FIRST_PAGE_NUM = "firstPageNum"

fun DoubleRefreshRecyclerLayout.saveStatus(savedData: Bundle) {
    savedData.putBoolean(ENABLE_LOAD_MORE, enableLoadMore)
    savedData.putBoolean(AUTO_LOAD_MORE, autoLoadMore)
    savedData.putBoolean(CAN_LOAD_MORE_FLAG, canLoadMoreFlag)
    savedData.putInt(PAGE_NUM, listAdapter.subPageUtil.getPageNum())
    savedData.putInt(FIRST_PAGE_NUM, listAdapter.subPageUtil.firstPageNum)
}

fun <T> DoubleRefreshRecyclerLayout.restoreStatus(savedData: Bundle,
                                                  provideObservable: (Int) -> Observable<List<T>>,
                                                  initData: List<T> = Collections.emptyList(),
                                                  adapter: RendererAdapter<T>) {
    enableLoadMore = savedData.getBoolean(ENABLE_LOAD_MORE, enableLoadMore)
    autoLoadMore = savedData.getBoolean(AUTO_LOAD_MORE, autoLoadMore)
    canLoadMoreFlag = savedData.getBoolean(CAN_LOAD_MORE_FLAG, canLoadMoreFlag)

    val pageNum = savedData.getInt(PAGE_NUM, listAdapter.subPageUtil.getPageNum())
    val firstPageNum = savedData.getInt(FIRST_PAGE_NUM, listAdapter.subPageUtil.getPageNum())

    initData(SubPageAdapter<T>(adapter,
            SubPageUtil<T>(provideObservable, initData, pageNum, firstPageNum)) as SubPageAdapter<Any>)
}