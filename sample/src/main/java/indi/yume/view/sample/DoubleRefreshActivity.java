package indi.yume.view.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
import indi.yume.view.avocadoviews.subpagelayout.SubPageUtil;
import rx.Observable;
import rx.Subscriber;
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

        RendererAdapter<String> rendererAdapter = new RendererAdapter<>(new ArrayList<>(), this, SearchResultJobsItemType1Renderer.class);
        SubPageUtil<String> subPageUtil = new SubPageUtil<String>(pageNum ->
                Observable.create(
                        new Observable.OnSubscribe<List<String>>() {
                            @Override
                            public void call(Subscriber<? super List<String>> sub) {
//                                        try {
//                                            Thread.sleep(300);
//                                        } catch (InterruptedException e) {
//                                            sub.onError(e);
//                                        }

                                List<String> list = new ArrayList<>();
                                list.add("item " + pageNum);
                                list.add("item " + pageNum);
                                list.add("item " + pageNum);
                                if (pageNum >= 4) {
                                    sub.onError(new NoMoreDataException());
                                    return;
                                }

                                sub.onNext(list);
                                sub.onCompleted();
                            }
                        })
                        .subscribeOn(Schedulers.io())
        );
        SubPageAdapter<String> adapter = new SubPageAdapter<>(rendererAdapter, subPageUtil);

        doubleRefreshLayout.initData(adapter);
    }

    static class ViewHolder {
        @Bind(R.id.changecondition_button)
        Button changeconditionButton;
        @Bind(R.id.footer_linealayout)
        LinearLayout footerLinealayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class RefreshViewHolder implements OnDoubleRefreshViewHolder {
        private View view;
        private ViewHolder viewHolder;

        public RefreshViewHolder(Context context, ViewGroup parent) {
            view = LayoutInflater.from(context).inflate(R.layout.search_result_listview_footer, parent, false);
            viewHolder = new ViewHolder(view);

            viewHolder.changeconditionButton.setOnClickListener(v -> doubleRefreshLayout.refreshData());
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
