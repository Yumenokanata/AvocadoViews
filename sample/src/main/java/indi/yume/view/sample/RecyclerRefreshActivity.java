package indi.yume.view.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import indi.yume.tools.adapter_renderer.recycler.RendererAdapter;
import indi.yume.view.avocadoviews.recyclerlayout.DoubleRefreshRecyclerLayout;
import indi.yume.view.avocadoviews.recyclerlayout.LastDataException;
import indi.yume.view.avocadoviews.recyclerlayout.LoadMoreStatus;
import indi.yume.view.avocadoviews.recyclerlayout.LoadMoreViewHolder;
import indi.yume.view.avocadoviews.recyclerlayout.NoMoreDataException;
import indi.yume.view.avocadoviews.recyclerlayout.OnDoubleRefreshViewHolder;
import indi.yume.view.avocadoviews.recyclerlayout.SubPageAdapter;
import rx.Observable;

public class RecyclerRefreshActivity extends AppCompatActivity {
    private static final String SAVE_KEY_ARRAY = "save_key_array";
    private static final String SAVE_KEY_INDEX = "save_key_index";

    @Bind(R.id.recycler_view_layout)
    DoubleRefreshRecyclerLayout recyclerViewLayout;
    @Bind(R.id.activity_recycler_refresh)
    CoordinatorLayout activityRecyclerRefresh;

    SubPageAdapter<String> adapter;
    private int loadCount = 0;
    private int refreshCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_refresh);
        ButterKnife.bind(this);

//        recyclerViewLayout.setOnDoubleRefreshViewHolder(new RefreshViewHolder(RecyclerRefreshActivity.this, recyclerViewLayout));
        List<String> list = savedInstanceState == null ? new ArrayList<>() : savedInstanceState.getStringArrayList(SAVE_KEY_ARRAY);
        if (list == null)
            list = new ArrayList<>();
        int initPageNum = savedInstanceState == null ? 0 : savedInstanceState.getInt(SAVE_KEY_INDEX, 0);

        System.out.println("init list num: " + Stream.of(list).collect(Collectors.joining(", ", "[", "]")));
        System.out.println("init page num: " + initPageNum);
        RendererAdapter<String> rendererAdapter = new RendererAdapter<>(new ArrayList<>(), this, TestRecyclerRenderer.class);
        adapter = new SubPageAdapter<>(rendererAdapter,
                list,
                initPageNum,
                pageNum -> getObservable(pageNum));

//        recyclerViewLayout.getListView().setVerticalScrollBarEnabled(true);
        recyclerViewLayout.setOnDoubleRefreshViewHolder(new LoadingStatusViewHolder(activityRecyclerRefresh, recyclerViewLayout));
        recyclerViewLayout.initData(adapter, new GridLayoutManager(this, 2));
//        recyclerViewLayout.setLoadMoreView(null);
        recyclerViewLayout.setLoadMoreView(new ViewHolder(recyclerViewLayout));
        recyclerViewLayout.setAutoLoadMore(false);
        recyclerViewLayout.setEnableLoadMore(true);
//        if(savedInstanceState == null)
//            recyclerViewLayout.refreshData();
        recyclerViewLayout.refreshData();
    }

    int count = 0;
    private Observable<List<String>> getObservable(int pageNum) {
//        return Observable.empty();
//        return Observable.just(provideTestData(pageNum));

        count++;
        if(count % 2 == 1 && pageNum == 1)
            return Observable.empty();

        if(pageNum > 2)
            return Observable.error(new NoMoreDataException());

        return Observable.just(provideTestData(pageNum));

//        if(pageNum > 2)
//            return Observable.error(new LastDataException(provideTestData(pageNum)));
//        return Observable.error(new NoMoreDataException());

//        System.out.println("load page num: " + pageNum);
//        if (pageNum > 4) {
//            return Observable.<List<String>>error(new NoMoreDataException())
//                    .delay(2000, TimeUnit.MILLISECONDS)
//                    .subscribeOn(Schedulers.io());
//        } else if (pageNum == 3) {
//            loadCount++;
//            if (loadCount < 1) {
//                return Observable.empty();
//            } else if (loadCount < 3) {
//                return Observable.error(new IOException());
//            } else {
//                loadCount = 0;
//                return Observable.just(provideTestData(pageNum))
//                        .delay(2000, TimeUnit.MILLISECONDS)
//                        .subscribeOn(Schedulers.io());
//            }
//        } else {
//            refreshCount++;
//            if (refreshCount >= 3) {
//                refreshCount = 0;
//                return Observable.just(provideTestData(pageNum))
//                        .delay(2000, TimeUnit.MILLISECONDS)
//                        .subscribeOn(Schedulers.io());
//            } else {
//                return Observable.error(new IOException());
//            }
//        }
    }

    private List<String> provideTestData(int pageNum) {
        List<String> list = new ArrayList<>();
        list.add("item " + pageNum);
        list.add("item " + pageNum);
//        list.add("item " + pageNum);

        return list;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList(SAVE_KEY_ARRAY, new ArrayList<>(adapter.getListAdapter().getContentList()));
        outState.putInt(SAVE_KEY_INDEX, adapter.getPageNum());
    }

    class RefreshViewHolder implements OnDoubleRefreshViewHolder {
        @Bind(R.id.refresh_button)
        Button refreshButton;
        @Bind(R.id.error_linealayout)
        LinearLayout errorLinealayout;

        private View view;

        public RefreshViewHolder(Context context, ViewGroup parent) {
            view = LayoutInflater.from(context).inflate(R.layout.listview_load_error_layout, parent, false);
            ButterKnife.bind(this, view);

            refreshButton.setOnClickListener(v -> recyclerViewLayout.refreshData());
        }

        @NotNull
        @Override
        public View getView() {
            return view;
        }

        @Override
        public void onLoading() {
            view.setVisibility(View.INVISIBLE);
        }

        @Override
        public boolean onNoContents() {
            view.setVisibility(View.VISIBLE);
            return true;
        }

        @Override
        public boolean onNotReachability() {
            view.setVisibility(View.VISIBLE);
            return true;
        }

        @Override
        public void onReachability() {
            view.setVisibility(View.INVISIBLE);
        }
    }

    class ViewHolder implements LoadMoreViewHolder {
        @Bind(R.id.load_more_text_view)
        TextView loadMoreTextView;
        @Bind(R.id.load_more_progress_bar)
        ProgressBar loadMoreProgressBar;

        private final View view;

        ViewHolder(ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.load_more_layout, viewGroup, false);
            ButterKnife.bind(this, view);
        }

        @NotNull
        @Override
        public View getView() {
            return view;
        }

        @Override
        public void stateChange(@NotNull LoadMoreStatus oldStatus, @NotNull LoadMoreStatus newStatus) {
            System.out.println(newStatus);

            if (newStatus == LoadMoreStatus.DISABLE) {
                loadMoreTextView.setVisibility(View.GONE);
                loadMoreProgressBar.setVisibility(View.GONE);
            } else if (newStatus == LoadMoreStatus.SHOW) {
                loadMoreTextView.setVisibility(View.VISIBLE);
                loadMoreProgressBar.setVisibility(View.GONE);
            } else if (newStatus == LoadMoreStatus.LOADING) {
                loadMoreTextView.setVisibility(View.GONE);
                loadMoreProgressBar.setVisibility(View.VISIBLE);
            } else if (newStatus == LoadMoreStatus.LOADOVER) {
                loadMoreTextView.setVisibility(View.VISIBLE);
                loadMoreProgressBar.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.load_more_text_view)
        public void onClick() {
            recyclerViewLayout.loadData();
        }
    }
}
