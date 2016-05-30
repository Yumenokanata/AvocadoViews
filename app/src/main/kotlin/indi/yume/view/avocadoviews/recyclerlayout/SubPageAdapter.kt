package indi.yume.view.avocadoviews.recyclerlayout

import indi.yume.tools.adapter_renderer.recycler.RendererAdapter
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by yume on 16-5-12.
 */
class SubPageAdapter<T>(val listAdapter: RendererAdapter<T>,
                        val subPageUtil: SubPageUtil<T>) {

    constructor(listAdapter: RendererAdapter<T>,
                dataProvider: (Int) -> Observable<List<T>>)
    : this(listAdapter, SubPageUtil(dataProvider))

    constructor(listAdapter: RendererAdapter<T>,
                initData: List<T>,
                initPage: Int = 1,
                dataProvider: (Int) -> Observable<List<T>>)
    : this(listAdapter, SubPageUtil<T>(dataProvider, initData, initPage))

    init {
        listAdapter.contentList = subPageUtil.cachePageData
    }

    fun refreshData(): Observable<List<T>> =
            subPageUtil.refreshPageData()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext {
                        listAdapter.contentList = it
                        listAdapter.notifyDataSetChanged()
                    }

    fun loadMoreData(): Observable<List<T>> =
            subPageUtil.loadNextPage()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext {
                        listAdapter.contentList = it
                        listAdapter.notifyDataSetChanged()
                    }

    fun getPageNum(): Int = subPageUtil.getPageNum()
}