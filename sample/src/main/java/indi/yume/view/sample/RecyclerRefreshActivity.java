package indi.yume.view.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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
import rx.Observable;
import rx.schedulers.Schedulers;

public class RecyclerRefreshActivity extends AppCompatActivity {

    @Bind(R.id.recycler_view_layout)
    DoubleRefreshRecyclerLayout recyclerViewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_refresh);
        ButterKnife.bind(this);

        recyclerViewLayout.setOnDoubleRefreshViewHolder(new RefreshViewHolder(RecyclerRefreshActivity.this, recyclerViewLayout));

        RendererAdapter<String> rendererAdapter = new RendererAdapter<>(new ArrayList<>(), this, TestRecyclerRenderer.class);
        SubPageAdapter<String> adapter = new SubPageAdapter<>(rendererAdapter,
                pageNum -> {
                    if(pageNum >= 4)
                        return Observable.<List<String>>error(new NoMoreDataException())
                                .delay(2000, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io());
                    else
                        return Observable.just(provideTestData(pageNum))
                                .delay(2000, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io());
                });

        recyclerViewLayout.initData(adapter, new LinearLayoutManager(this));
    }

    private List<String> provideTestData(int pageNum) {
        List<String> list = new ArrayList<>();
        list.add("item " + pageNum);
        list.add("item " + pageNum);
        list.add("item " + pageNum);

        return list;
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
