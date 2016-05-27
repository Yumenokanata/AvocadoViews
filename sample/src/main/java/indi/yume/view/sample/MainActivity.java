package indi.yume.view.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import indi.yume.view.avocadoviews.dataselect.DateSelectPicker;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.date_select_picker)
    DateSelectPicker dateSelectPicker;
    @Bind(R.id.jump_to_refresh_button)
    Button jumpToRefreshButton;
    @Bind(R.id.jump_to_recycler_button)
    Button jumpToRecyclerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        jumpToRefreshButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DoubleRefreshActivity.class)));
        jumpToRecyclerButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RecyclerRefreshActivity.class)));
    }
}
