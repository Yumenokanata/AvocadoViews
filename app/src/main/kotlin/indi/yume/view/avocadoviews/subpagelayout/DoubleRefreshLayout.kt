package indi.yume.view.avocadoviews.subpagelayout

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.ListView
import com.jakewharton.rxbinding.widget.AdapterViewItemClickEvent
import com.jakewharton.rxbinding.widget.RxAdapterView
import indi.yume.tools.adapter_renderer.BaseRenderer
import indi.yume.tools.adapter_renderer.BaseRendererBuilder
import indi.yume.tools.adapter_renderer.RendererAdapter
import indi.yume.tools.avocado.util.LogUtil
import indi.yume.view.avocadoviews.R
import lombok.Setter
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import org.jetbrains.anko.find
import org.jetbrains.anko.onScrollListener
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.functions.Action1
import rx.functions.Func1
import java.io.IOException
import java.util.*

/**
 * Created by yume on 16-5-12.
 */
class DoubleRefreshLayout(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    constructor(context: Context?): this(context, null)

    val listView: ListView by lazy { find<ListView>(R.id.list_view) }
    val swipeRefreshLayout: SwipeRefreshLayout by lazy { find<SwipeRefreshLayout>(R.id.swipy_layout) }
    val noContentLoadProgressView: MaterialProgressBar by lazy { find<MaterialProgressBar>(R.id.no_content_load_progress_view) }

    private lateinit var loadMoreViewHolder: LoadMoreViewHolder

    var onDoubleRefreshViewHolder: OnDoubleRefreshViewHolder? = null
        set(value) {
            if(value == null) return

            field?.apply { removeView(getView()) }

            addView(value.getView())
            field = value
        }

    var headerView: View? = null
        set(value) {
            if (field != null)
                listView.removeHeaderView(field)

            listView.addHeaderView(value)
            field = value
        }

    private lateinit var listAdapter: SubPageAdapter<out Any>

    private var loadMoreSub: Subscription? = null

    val onNextListener: ((List<Any>) -> Unit)? = null
    val onErrorListener: ((Throwable) -> Unit)? = null

    private var canLoadMoreFlag = true
        set(value) {
            field = value
            loadMoreViewHolder.enableLoadMore(value)
        }
    private var isLoading = false

    private val onLoadOverSub = object : Subscriber<List<Any>>() {
        override fun onCompleted() {

        }

        override fun onError(e: Throwable) {
            LogUtil.e(e)

            onErrorListener?.invoke(e)

            isLoading = false

            if (e is NoMoreDataException) {
                canLoadMoreFlag = false
                switchStopContentView()
            } else if (e is IOException) {
                showNetworkErrorView()
            }
        }

        override fun onNext(list: List<Any>) {
            switchStopContentView(list)

            isLoading = false

            onNextListener?.invoke(list)
        }
    }

    init {
        View.inflate(context, R.layout.double_refresh_layout, this)

        swipeRefreshLayout.setColorSchemeResources(R.color.color_re1)

        loadMoreViewHolder = DefaultLoadMoreViewHolder(context!!, listView)
        setLoadMoreView(loadMoreViewHolder)

        showNoContentView()
    }

    fun setLoadMoreView(loadMoreView: LoadMoreViewHolder) {
        listView.removeFooterView(loadMoreViewHolder.view)

        listView.addFooterView(loadMoreView.view)
        loadMoreView.enableLoadMore(canLoadMoreFlag)

        loadMoreViewHolder = loadMoreView
    }

    fun enablePullDownRefresh(enable: Boolean) {
        swipeRefreshLayout.isEnabled = enable
    }

    /**
     * init list view's adapter and data.

     * @param listAdapter provide sub page data and list view's adapter.
     */
    fun initData(listAdapter: SubPageAdapter<out Any>) {
        this.listAdapter = listAdapter
        listView.adapter = listAdapter.listAdapter

        swipeRefreshLayout.setOnRefreshListener { this.refreshData() }
        listView.onScrollListener {
            onScrollStateChanged { listViewTemp, scrollState ->
                if (canLoadMoreFlag)
                    loadMoreViewHolder.startLoadMore()

                if (canLoadMoreFlag && !isLoading && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                    if((0..listView.footerViewsCount - 1)
                        .map { listView.childCount - it - 1 }
                        .map { listView.getChildAt(it) }
                        .any { it === loadMoreViewHolder.view })
                            loadData()
            }
        }
    }

    fun stopLoad() {
        loadMoreSub?.unsubscribe()
    }

    fun refreshData() {
        stopLoad()

        canLoadMoreFlag = true
        loadMoreViewHolder.stopLoadMore()

        isLoading = true
        loadMoreSub = listAdapter.refreshData()
                .subscribe(Action1<List<Any>> { onLoadOverSub.onNext(it) },
                        Action1<Throwable> { onLoadOverSub.onError(it) })
        switchRefreshContentView()
    }

    fun loadData() {
        if (listAdapter.listAdapter.count == 0)
            refreshData()
        else
            loadMoreData()
    }

    private fun loadMoreData() {
        stopLoad()

        isLoading = true
        showListView()
        if (canLoadMoreFlag)
            loadMoreViewHolder.startLoadMore()

        loadMoreSub = listAdapter.loadMoreData()
                .subscribe(Action1<List<Any>> { onLoadOverSub.onNext(it) },
                        Action1<Throwable> { onLoadOverSub.onError(it) })
    }

    /**
     * set ItemClickListener for list view.

     * @param listener [AdapterView.OnItemClickListener]
     */
    fun setItemClickListener(listener: AdapterView.OnItemClickListener) {
        listView.onItemClickListener = listener
    }

    fun asItemClickObservable(): Observable<AdapterViewItemClickEvent> {
        return RxAdapterView.itemClickEvents(listView)
    }

    fun addFooterView(footer: View) {
        listView.addFooterView(footer, null, false)
    }

    private fun showNoContentLoadProgress() {
        noContentLoadProgressView.visibility = View.VISIBLE
        swipeRefreshLayout.visibility = View.INVISIBLE
        listView.visibility = View.INVISIBLE

        onDoubleRefreshViewHolder?.onLoading()
    }

    private fun showNoContentView() {
        noContentLoadProgressView.visibility = View.INVISIBLE
        swipeRefreshLayout.visibility = View.INVISIBLE
        listView.visibility = View.INVISIBLE

        onDoubleRefreshViewHolder?.onNoContents()
    }

    private fun showNetworkErrorView() {
        noContentLoadProgressView.visibility = View.INVISIBLE
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.visibility = View.INVISIBLE
        listView.visibility = View.INVISIBLE

        onDoubleRefreshViewHolder?.onNotReachability()
    }

    private fun showListView() {
        noContentLoadProgressView.visibility = View.INVISIBLE
        swipeRefreshLayout.visibility = View.VISIBLE
        listView.visibility = View.VISIBLE

        onDoubleRefreshViewHolder?.onReachability()
        onDoubleRefreshViewHolder?.getView()?.visibility = INVISIBLE
    }

    private fun switchStopContentView(sumListData: List<Any>?) {
        if (sumListData == null || sumListData.isEmpty()) {
            showNoContentView()
        } else {
            swipeRefreshLayout.isRefreshing = false
            showListView()
        }
    }

    private fun switchStopContentView() {
        if (listAdapter.listAdapter.count == 0) {
            showNoContentView()
        } else {
            swipeRefreshLayout.isRefreshing = false
            showListView()
        }
    }

    private fun switchRefreshContentView() {
        if (listAdapter.listAdapter.count == 0) {
            showNoContentLoadProgress()
        } else {
            showListView()
            swipeRefreshLayout.isRefreshing = true
        }
    }
}

interface LoadMoreViewHolder {
    val view: View

    fun startLoadMore()
    fun stopLoadMore()
    fun enableLoadMore(enable: Boolean)
}

internal class DefaultLoadMoreViewHolder(context: Context, parent: ViewGroup) : LoadMoreViewHolder {
    override val view: View = LayoutInflater.from(context).inflate(R.layout.load_more_footer_layout, parent, false)
    private val loadingMoreView: MaterialProgressBar  = view.findViewById(R.id.loading_more_view) as MaterialProgressBar

    private var enable = true

    override fun startLoadMore() {
        if (enable)
            loadingMoreView.visibility = View.VISIBLE
    }

    override fun stopLoadMore() {
        if (enable)
            loadingMoreView.visibility = View.GONE
    }

    override fun enableLoadMore(enable: Boolean) {
        this.enable = enable
        if (!enable)
            loadingMoreView.visibility = View.GONE
        else
            loadingMoreView.visibility = View.VISIBLE
    }
}