package indi.yume.view.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import indi.yume.view.avocadoviews.dataselect.DateSelectPicker;
import indi.yume.view.avocadoviews.subpagelayout.SubPageUtil;
import kotlin.jvm.functions.Function1;
import rx.Observable;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.date_select_picker)
    DateSelectPicker dateSelectPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SubPageUtil<String> subPageUtil = new SubPageUtil<>(pageNum -> {
            List<String> data = new ArrayList<>();
            for(int i = 0; i < 10; i++)
                data.add(String.format("%d%d%d", pageNum, pageNum, pageNum));
            return Observable.just(data).delay(2, TimeUnit.SECONDS);
        });
        subPageUtil.setDoForEveryPageData(new Function1<List<? extends String>, List<? extends String>>() {
            @Override
            public List<? extends String> invoke(List<? extends String> strings) {
                List<String> list = new ArrayList<>(strings);
                list.add("2222");
                return list;
            }
        });
        subPageUtil.refreshPageData().subscribe(list -> System.out.println(Joiner.on(", ").join(list)));
    }
}
