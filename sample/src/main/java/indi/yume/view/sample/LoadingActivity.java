package indi.yume.view.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import indi.yume.view.avocadoviews.loading.LoadingLayout;

public class LoadingActivity extends AppCompatActivity {

    @Bind(R.id.loading_layout)
    LoadingLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.content_buuton, R.id.empty_buuton, R.id.loading_buuton, R.id.error_buuton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.content_buuton:
                loadingLayout.showContentView();
                break;
            case R.id.empty_buuton:
                loadingLayout.showEmptyView();
                break;
            case R.id.loading_buuton:
                loadingLayout.showLoadingView();
                break;
            case R.id.error_buuton:
                loadingLayout.showErrorView();
                break;
        }
    }
}
