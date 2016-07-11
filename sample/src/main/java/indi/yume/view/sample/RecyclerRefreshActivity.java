package indi.yume.view.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import indi.yume.tools.adapter_renderer.recycler.RendererAdapter;
import indi.yume.view.avocadoviews.recyclerlayout.DoubleRefreshRecyclerLayout;
import indi.yume.view.avocadoviews.recyclerlayout.NoMoreDataException;
import indi.yume.view.avocadoviews.recyclerlayout.OnDoubleRefreshViewHolder;
import indi.yume.view.avocadoviews.recyclerlayout.SubPageAdapter;
import indi.yume.view.avocadoviews.recyclerlayout.SubPageUtil;
import rx.Observable;
import rx.schedulers.Schedulers;

public class RecyclerRefreshActivity extends AppCompatActivity {
    private static final String SAVE_KEY_ARRAY = "save_key_array";
    private static final String SAVE_KEY_INDEX = "save_key_index";

    @Bind(R.id.recycler_view_layout)
    DoubleRefreshRecyclerLayout recyclerViewLayout;

    SubPageAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_refresh);
        ButterKnife.bind(this);

//        recyclerViewLayout.setOnDoubleRefreshViewHolder(new RefreshViewHolder(RecyclerRefreshActivity.this, recyclerViewLayout));
        List<String> list = savedInstanceState == null ? new ArrayList<>() : savedInstanceState.getStringArrayList(SAVE_KEY_ARRAY);
        if(list == null)
            list = new ArrayList<>();
        int initPageNum = savedInstanceState == null ? 0 : savedInstanceState.getInt(SAVE_KEY_INDEX, 0);

        System.out.println("init list num: " + Stream.of(list).collect(Collectors.joining(", ", "[", "]")));
        System.out.println("init page num: " + initPageNum);
        RendererAdapter<String> rendererAdapter = new RendererAdapter<>(new ArrayList<>(), this, TestRecyclerRenderer.class);
        adapter = new SubPageAdapter<>(rendererAdapter,
                list,
                initPageNum,
                pageNum -> {
                    System.out.println("load page num: " + pageNum);
                    if(pageNum > 4)
                        return Observable.<List<String>>error(new NoMoreDataException())
                                .delay(2000, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io());
                    else
                        return Observable.just(provideTestData(pageNum))
                                .delay(2000, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io());
                });

        recyclerViewLayout.initData(adapter, new GridLayoutManager(this, 2));
//        if(savedInstanceState == null)
//            recyclerViewLayout.refreshData();
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
        public void onNoContents() {
            view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onNotReachability() {
            view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReachability() {
            view.setVisibility(View.INVISIBLE);
        }
    }
}
