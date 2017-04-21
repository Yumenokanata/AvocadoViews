package indi.yume.view.avocadoviews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.dsl_adapter_simple_button)
                .setOnClickListener(v -> startActivity(new Intent(this, ListActivity.class)));
        findViewById(R.id.loading_layout_simple_button)
                .setOnClickListener(v -> startActivity(new Intent(this, LoadActivity.class)));
    }
}
