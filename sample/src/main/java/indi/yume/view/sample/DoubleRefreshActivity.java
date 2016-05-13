package indi.yume.view.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import indi.yume.tools.adapter_renderer.RendererAdapter;
import indi.yume.view.avocadoviews.subpagelayout.DoubleRefreshLayout;
import indi.yume.view.avocadoviews.subpagelayout.NoMoreDataException;
import indi.yume.view.avocadoviews.subpagelayout.OnDoubleRefreshViewHolder;
import indi.yume.view.avocadoviews.subpagelayout.SubPageAdapter;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DoubleRefreshActivity extends AppCompatActivity {
    private int itemNum = 1;

    @Bind(R.id.double_refresh_layout)
    DoubleRefreshLayout doubleRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_refresh);
        ButterKnife.bind(this);

        doubleRefreshLayout.setOnDoubleRefreshViewHolder(new RefreshViewHolder(this, doubleRefreshLayout));

        RendererAdapter<String> rendererAdapter = new RendererAdapter<>(new ArrayList<>(), this, TestItemRenderer.class);
        SubPageAdapter<String> adapter = new SubPageAdapter<>(rendererAdapter,
                pageNum -> {
                    if(pageNum >= 4)
                        return Observable.<List<String>>error(new NoMoreDataException())
                                .subscribeOn(Schedulers.io());
                    else
                        return Observable.just(provideTestData(pageNum))
                                .subscribeOn(Schedulers.io());
                });

        doubleRefreshLayout.initData(adapter);
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

            refreshButton.setOnClickListener(v -> doubleRefreshLayout.refreshData());
        }

        @NotNull
        @Override
        public View getView() {
            return view;
        }

        @Override
        public void onLoading() {

        }

        @Override
        public void onNoContents() {

        }

        @Override
        public void onNotReachability() {

        }

        @Override
        public void onReachability() {

        }
    }
}
