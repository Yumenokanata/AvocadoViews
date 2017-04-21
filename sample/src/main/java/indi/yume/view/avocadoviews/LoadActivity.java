package indi.yume.view.avocadoviews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import indi.yume.view.avocadoviews.dsladapter.RendererAdapter;
import indi.yume.view.avocadoviews.loadinglayout.LayoutInitializer;
import indi.yume.view.avocadoviews.loadinglayout.LoadMoreStatus;
import indi.yume.view.avocadoviews.loadinglayout.LoadingLayout;
import indi.yume.view.avocadoviews.loadinglayout.LoadingResult;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.Data;
import lombok.experimental.Wither;

import static indi.yume.view.avocadoviews.adapterdatabinding.DataBindingRendererBuilder.dataBindingRepositoryPresenterOf;
import static indi.yume.view.avocadoviews.dsladapter.builder.RendererBuilder.layout;
import static indi.yume.view.avocadoviews.dsladapter.builder.RendererBuilder.rendererOf;

public class LoadActivity extends AppCompatActivity {

    @Data
    class State {
        @Wither private final List<ItemModel> data;
        @Wither private final LoadMoreStatus status;
    }

    private RendererAdapter adapter;
    private State state = new State(Collections.emptyList(), LoadMoreStatus.NORMAL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        adapter = RendererAdapter.repositoryAdapter()
                .addLayout(layout(R.layout.list_header))
                .add(() -> state.data,
                        dataBindingRepositoryPresenterOf(ItemModel.class)
                                .layout(R.layout.item_layout)
                                .itemId(BR.model)
                                .stableIdForItem(ItemModel::getId)
                                .forList())
                .add(() -> state.status,
                        dataBindingRepositoryPresenterOf(LoadMoreStatus.class)
                                .layout(R.layout.list_load_more_layout)
                                .itemId(BR.status)
                                .forItem())
                .build();
        adapter.setHasStableIds(true);

        LoadingLayout loadingLayout = (LoadingLayout) findViewById(R.id.loading_layout);
        LayoutInitializer initializer = LayoutInitializer
                .<RecyclerView.ViewHolder, ItemModel>builder()
                .provider(this::dataSupplier)
                .adapter(adapter)
                .layoutManager(new LinearLayoutManager(this))
                .doForLoadMoreView(loadMoreStatus -> {
                    Log.d(LoadActivity.class.getSimpleName(), "doForLoadMoreView: " + loadMoreStatus + " data: " + state.data.size());
                    render(state.withStatus(loadMoreStatus));
                    if(loadMoreStatus == LoadMoreStatus.NORMAL)
                        loadingLayout.loadData();
                })
                .showData(data -> render(state.withData(data)))
                .build();

        loadingLayout.init(initializer);
    }

    private void render(State newState) {
        state = newState;
        adapter.update();
    }

    private Single<LoadingResult<List<ItemModel>>> dataSupplier(int pageIndex) {
        if(pageIndex > 3)
            return Single.just(LoadingResult.noMore());
        return Single.just(Models.genList(pageIndex * 4, 4))
                .delay(3000, TimeUnit.MILLISECONDS)
                .map(LoadingResult::success)
                .subscribeOn(Schedulers.io());
    }
}
