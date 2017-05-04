package indi.yume.view.avocadoviews;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import indi.yume.view.avocadoviews.adapterdatabinding.DataBindingRendererBuilder;
import indi.yume.view.avocadoviews.dsladapter.RendererAdapter;
import indi.yume.view.avocadoviews.BR;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static indi.yume.view.avocadoviews.adapterdatabinding.DataBindingRendererBuilder.dataBindingRepositoryPresenterOf;
import static indi.yume.view.avocadoviews.dsladapter.builder.RendererBuilder.rendererOf;
import static indi.yume.view.avocadoviews.dsladapter.builder.RendererBuilder.layout;

public class ListActivity extends AppCompatActivity {

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RendererAdapter adapter = RendererAdapter.repositoryAdapter()
                .addLayout(layout(R.layout.list_header))
                .add(() -> provideData(index),
                        rendererOf(ItemModel.class)
                                .layout(R.layout.simple_item)
                                .stableIdForItem(ItemModel::getId)
                                .bindWith((m, v) -> ((TextView)v.findViewById(R.id.simple_text_view)).setText(m.getTitle()))
                                .recycleWith(v -> ((TextView)v.findViewById(R.id.simple_text_view)).setText(""))
                                .forList())
                .add(() -> provideData(index),
                        dataBindingRepositoryPresenterOf(ItemModel.class)
                                .layout(R.layout.item_layout)
                                .itemId(BR.model)
                                .itemId(BR.content, m -> m.getContent() + "xxxx")
                                .stableIdForItem(ItemModel::getId)
                                .forList())
                .addItem(DateFormat.getInstance().format(new Date()),
                        dataBindingRepositoryPresenterOf(String.class)
                                .layout(R.layout.list_footer)
                                .itemId(BR.text)
                                .forItem())
                .build();
        adapter.setHasStableIds(true);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<ItemModel> provideData(int pageIndex) {
        return dataSupplier(pageIndex)
                .blockingGet();
    }

    private Single<List<ItemModel>> dataSupplier(int pageIndex) {
        return Single.just(Models.genList(pageIndex * 10, 10))
                .delay(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io());
    }
}
