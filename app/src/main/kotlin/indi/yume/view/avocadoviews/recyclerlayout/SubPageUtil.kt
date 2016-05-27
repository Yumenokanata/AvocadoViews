package indi.yume.view.avocadoviews.recyclerlayout

import lombok.Setter
import rx.Observable
import java.util.*

/**
 * Created by yume on 16-5-12.
 */
class SubPageUtil<T>(private val provideObservable: (Int) -> Observable<List<T>>,
                     initData: List<T> = Collections.emptyList(),
                     private val firstPageNum: Int = 1) {
    private var cachePageData: List<T> = initData;
    private var pageNum: Int = firstPageNum - 1;

    var doForEveryPageData: ((List<T>) -> List<T>)? = { it }

    constructor(provideObservable: (Int) -> Observable<List<T>>): this(provideObservable, Collections.emptyList(), 1)

    constructor(provideObservable: (Int) -> Observable<List<T>>,
                firstPageNum: Int)
    : this(provideObservable, Collections.emptyList(), firstPageNum)

    fun loadNextPage(): Observable<List<T>> =
            provideObservable(pageNum + 1)
                    .doOnNext { list ->
                        cachePageData = cachePageData + (doForEveryPageData?.invoke(list) ?: list)
                        pageNum++ }
                    .map { getAllData() }

    fun refreshPageData(): Observable<List<T>> =
            provideObservable(firstPageNum)
                    .doOnNext { list ->
                        cachePageData = doForEveryPageData?.invoke(list) ?: list
                        pageNum = firstPageNum }
                    .map { getAllData() }

    fun getAllData(): List<T> = Collections.unmodifiableList(cachePageData)
}