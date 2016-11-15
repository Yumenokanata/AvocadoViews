package indi.yume.view.sample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import butterknife.Bind;
import butterknife.ButterKnife;
import indi.yume.view.avocadoviews.loading.LoadingLayout;
import indi.yume.view.avocadoviews.recyclerlayout.DoubleRefreshRecyclerLayout;
import indi.yume.view.avocadoviews.recyclerlayout.OnDoubleRefreshViewHolder;

/**
 * Created by yume on 16-8-12.
 */

public class LoadingStatusViewHolder implements OnDoubleRefreshViewHolder {
    @Bind(R.id.home_loading_layout)
    LoadingLayout homeLoadingLayout;

    private final View view;

    public LoadingStatusViewHolder(ViewGroup viewGroup, DoubleRefreshRecyclerLayout refreshRecyclerLayout) {
        this.view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_page_loading_layout, viewGroup, false);
        ButterKnife.bind(this, view);

        homeLoadingLayout.getErrorView().setOnClickListener(v -> refreshRecyclerLayout.loadData());
    }

    @NotNull
    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onLoading() {
        homeLoadingLayout.showLoadingView();
        System.out.println("LoadingStatusViewHolder: onLoading");
    }

    @Override
    public boolean onNoContents() {
        homeLoadingLayout.showErrorView();
        System.out.println("LoadingStatusViewHolder: onNoContents");
        return false;
    }

    @Override
    public boolean onNotReachability() {
        homeLoadingLayout.showErrorView();
        System.out.println("LoadingStatusViewHolder: onNotReachability");
        return false;
    }

    @Override
    public void onReachability() {
        System.out.println("LoadingStatusViewHolder: onReachability");
    }
}
