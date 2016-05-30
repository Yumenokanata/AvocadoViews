package indi.yume.view.avocadoviews.recyclerlayout

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.FrameLayout
import com.jakewharton.rxbinding.widget.AdapterViewItemClickEvent
import com.jakewharton.rxbinding.widget.RxAdapterView
import indi.yume.tools.adapter_renderer.recycler.OnItemClickListener
import indi.yume.tools.adapter_renderer.recycler.RendererAdapter
import indi.yume.tools.avocado.util.LogUtil
import indi.yume.view.avocadoviews.R
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import org.jetbrains.anko.find
import org.jetbrains.anko.onScrollListener
import org.jetbrains.anko.recyclerview.v7.onScrollListener
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.functions.Action1
import java.io.IOException

/**
 * Created by yume on 16-5-25.
 */
class DoubleRefreshRecyclerLayout(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    constructor(context: Context?): this(context, null)

    val listView: RecyclerView by lazy { find<RecyclerView>(R.id.recycler_view) }
    val swipeRefreshLayout: SwipeRefreshLayout by lazy { find<SwipeRefreshLayout>(R.id.swipy_layout) }
    val noContentLoadProgressView: MaterialProgressBar by lazy { find<MaterialProgressBar>(R.id.no_content_load_progress_view) }

    private lateinit var loadMoreViewHolder: LoadMoreViewHolder

    private lateinit var listAdapter: SubPageAdapter<out Any>

    private var loadMoreSub: Subscription? = null

    val onNextListener: ((List<Any>) -> Unit)? = null
    val onErrorListener: ((Throwable) -> Unit)? = null

    private var isLoading = false

    var onDoubleRefreshViewHolder: OnDoubleRefreshViewHolder? = null
        set(value) {
            if(value == null) return

            field?.apply { removeView(getView()) }

            addView(value.getView())
            field = value
        }

    private var canLoadMoreFlag = true
        set(value) {
            field = value
            loadMoreViewHolder.enableLoadMore(value)
        }

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
            if(checkLoadMoreViewCanSee(listView, listAdapter.listAdapter, loadMoreViewHolder))
                loadMoreViewHolder.stopLoadMore()

            isLoading = false

            onNextListener?.invoke(list)
        }
    }

    init {
        View.inflate(context, R.layout.double_refresh_recycler_layout, this)

        swipeRefreshLayout.setColorSchemeResources(R.color.color_re1)

        listView.layoutManager = LinearLayoutManager(context)
        loadMoreViewHolder = DefaultLoadMoreViewHolder(context!!, listView)

        showNoContentView()
    }

    fun setLoadMoreView(loadMoreView: LoadMoreViewHolder) {
        listAdapter.listAdapter.removeFooterView(loadMoreViewHolder.view)

        listAdapter.listAdapter.addFooterView(loadMoreView.view)
        loadMoreView.enableLoadMore(canLoadMoreFlag)

        loadMoreViewHolder = loadMoreView
    }

    fun enablePullDownRefresh(enable: Boolean) {
        swipeRefreshLayout.isEnabled = enable
    }

    fun doForRecyclerView(doForRecyclerView: RecyclerView.() -> Unit) {
        listView.doForRecyclerView()
    }

    fun initData(listAdapter: SubPageAdapter<out Any>, layoutManager: RecyclerView.LayoutManager) {
        listView.layoutManager = layoutManager
        initData(listAdapter)
    }

    fun initData(listAdapter: SubPageAdapter<out Any>, initRecyclerView: RecyclerView.() -> Unit) {
        doForRecyclerView(initRecyclerView)
        initData(listAdapter)
    }

    /**
     * init list view's adapter and data.

     * @param listAdapter provide sub page data and list view's adapter.
     */
    fun initData(listAdapter: SubPageAdapter<out Any>) {
        this.listAdapter = listAdapter
        listView.adapter = listAdapter.listAdapter
        listAdapter.listAdapter.notifyDataSetChanged()

        swipeRefreshLayout.setOnRefreshListener { this.refreshData() }
        listView.onScrollListener {
            onScrollStateChanged { listViewTemp, scrollState ->
                if (canLoadMoreFlag && listAdapter.listAdapter.contentLength != 0)
                    loadMoreViewHolder.startLoadMore()

                if (canLoadMoreFlag && !isLoading && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                    if(checkLoadMoreViewCanSee(listView, listAdapter.listAdapter, loadMoreViewHolder))
                        loadData()
            }
        }
        setLoadMoreView(loadMoreViewHolder)
        loadMoreViewHolder.stopLoadMore()
        switchStopContentView()
    }

    fun getSubPageAdapter() = listAdapter

    private fun checkLoadMoreViewCanSee(recyclerView: RecyclerView,
                                        adapter: RendererAdapter<out Any>,
                                        loadMoreView: LoadMoreViewHolder): Boolean =
            (0..adapter.footerViewCount - 1)
                    .map { recyclerView.childCount - it - 1 }
                    .map { recyclerView.getChildAt(it) }
                    .any { it === loadMoreView.view }

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
        if (listAdapter.listAdapter.contentLength == 0)
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
    fun setItemClickListener(listener: OnItemClickListener) {
        listAdapter.listAdapter.setOnItemClickListener(listener)
    }

    fun addFooterView(footer: View) {
        listAdapter.listAdapter.addFooterView(footer)
    }

    private fun showNoContentLoadProgress() {
        noContentLoadProgressView.visibility = View.VISIBLE
        swipeRefreshLayout.visibility = View.INVISIBLE
        listView.visibility = View.INVISIBLE

        onDoubleRefreshViewHolder?.onLoading()
    }

    private fun showNoContentView() {
        noContentLoadProgressView.visibility = View.INVISIBLE

        if(onDoubleRefreshViewHolder != null) {
            swipeRefreshLayout.visibility = View.INVISIBLE
            listView.visibility = View.INVISIBLE
            onDoubleRefreshViewHolder!!.onNoContents()
        } else {
            swipeRefreshLayout.visibility = View.VISIBLE
            listView.visibility = View.VISIBLE
        }
    }

    private fun showNetworkErrorView() {
        noContentLoadProgressView.visibility = View.INVISIBLE
        swipeRefreshLayout.isRefreshing = false

        if(onDoubleRefreshViewHolder != null) {
            swipeRefreshLayout.visibility = View.INVISIBLE
            listView.visibility = View.INVISIBLE
            onDoubleRefreshViewHolder!!.onNotReachability()
        } else {
            swipeRefreshLayout.visibility = View.VISIBLE
            listView.visibility = View.VISIBLE
        }
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
        if (listAdapter.listAdapter.contentLength == 0) {
            showNoContentView()
        } else {
            swipeRefreshLayout.isRefreshing = false
            showListView()
        }
    }

    private fun switchRefreshContentView() {
        if (listAdapter.listAdapter.contentLength == 0) {
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