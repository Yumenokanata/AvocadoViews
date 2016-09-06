package indi.yume.view.avocadoviews.loading;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import indi.yume.view.avocadoviews.R;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Created by yume on 16-6-21.
 */

public class LoadingLayout extends FrameLayout {
    private SettingData settingData;

    @Getter
    private View emptyView;
    @Getter
    private View uselessView;
    @Getter
    private View errorView;
    @Getter
    private View loadingView;
    @Getter
    private View contentView;

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if(attrs != null)
            init(context.obtainStyledAttributes(attrs, R.styleable.LoadingLayout, defStyleAttr, 0));

        if(settingData == null)
            settingData = new SettingData(-1, -1, -1, -1, 0, 0);

        if (settingData.getEmptyViewLayoutResId() != -1) {
            emptyView = inflateView(settingData.getEmptyViewLayoutResId());
            addView(emptyView);
        }

        if (settingData.getUselessViewLayoutResId() != -1) {
            uselessView = inflateView(settingData.getUselessViewLayoutResId());
            addView(uselessView);
        }

        if (settingData.getErrorViewLayoutResId() != -1){
            errorView = inflateView(settingData.getErrorViewLayoutResId());
            addView(errorView);
        }

        if (settingData.getLoadingViewLayoutResId() != -1){
            loadingView = inflateView(settingData.getLoadingViewLayoutResId());
            addView(loadingView);
        }

        switchMode(settingData.getDefaultShowMode());
    }

    private View inflateView(@LayoutRes int layout) {
        return LayoutInflater.from(getContext()).inflate(layout, this, false);
    }

    private void init(TypedArray tArray) {
        try {
            int emptyViewLayoutResId = tArray.getResourceId(R.styleable.LoadingLayout_ll_emptyView, -1);
            int uselessViewLayoutResId = tArray.getResourceId(R.styleable.LoadingLayout_ll_uselessView, -1);
            int errorViewLayoutResId = tArray.getResourceId(R.styleable.LoadingLayout_ll_errorView, -1);
            int loadingViewLayoutResId = tArray.getResourceId(R.styleable.LoadingLayout_ll_loadingView, -1);
            int defaultShowMode = tArray.getInt(R.styleable.LoadingLayout_ll_defaultShowMode, 0);
            int editModeShowMode = tArray.getInt(R.styleable.LoadingLayout_ll_toolShowMode, 0);

            settingData = new SettingData(emptyViewLayoutResId,
                    uselessViewLayoutResId,
                    errorViewLayoutResId,
                    loadingViewLayoutResId,
                    defaultShowMode,
                    editModeShowMode);
        } finally {
            tArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        for(int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if(view != errorView && view != loadingView && view != emptyView && view != uselessView) {
                contentView = view;
                break;
            }
        }

        if (isInEditMode()) {
            switchMode(settingData.getEditModeShowMode());
        } else {
            switchMode(settingData.getDefaultShowMode());
        }
    }

    private void switchMode(int mode) {
        switch (mode) {
            case 0:
                showContentView();
                break;
            case 1:
                showEmptyView();
                break;
            case 2:
                showErrorView();
                break;
            case 3:
                showLoadingView();
                break;
            case 4:
                showUselessView();
                break;
        }
    }

    public void setEmptyView(View emptyView) {
        if (this.emptyView != emptyView) {
            if (this.emptyView != null) {
                removeView(this.emptyView);
            }
            this.emptyView = emptyView;
            addView(this.emptyView);
        }
    }

    public void setUselessView(View uselessView) {
        if (this.uselessView != uselessView) {
            if (this.uselessView != null) {
                removeView(this.uselessView);
            }
            this.uselessView = uselessView;
            addView(this.uselessView);
        }
    }

    public void setErrorView(View errorView) {
        if (this.errorView != null) {
            removeView(this.errorView);
        }
        if (this.errorView != errorView) {
            this.errorView = errorView;
            addView(errorView);
            this.errorView.setVisibility(GONE);
        }
    }

    public void setLoadingView(View loadingView) {
        if (this.loadingView != null) {
            removeView(this.loadingView);
        }
        if (this.loadingView != loadingView) {
            this.loadingView = loadingView;
            addView(loadingView);
            this.loadingView.setVisibility(GONE);
        }
    }

    public void setContentView(View contentView) {
        if (this.contentView != null) {
            removeView(this.contentView);
        }
        if (this.contentView != contentView) {
            this.contentView = contentView;
            addView(contentView);
        }
    }

    public void setEmptyView(@LayoutRes int emptyViewResId) {
        View view = LayoutInflater.from(getContext()).inflate(emptyViewResId, this, false);
        setEmptyView(view);
    }

    public void setUselessView(@LayoutRes int uselessViewResId) {
        View view = LayoutInflater.from(getContext()).inflate(uselessViewResId, this, false);
        setUselessView(view);
    }

    public void setErrorView(@LayoutRes int errorViewResId) {
        View view = LayoutInflater.from(getContext()).inflate(errorViewResId, this, false);
        setErrorView(view);
    }

    public void setLoadingView(@LayoutRes int loadingViewResId) {
        View view = LayoutInflater.from(getContext()).inflate(loadingViewResId, this, false);
        setLoadingView(view);
    }

    public void setContentView(@LayoutRes int contentViewResId) {
        View view = LayoutInflater.from(getContext()).inflate(contentViewResId, this, false);
        setContentView(view);
    }

    public void showEmptyView() {
        showSingleView(this.emptyView);
    }

    public void showUselessView() {
        showSingleView(this.uselessView);
    }

    public void showErrorView() {
        showSingleView(this.errorView);
    }

    public void showLoadingView() {
        showSingleView(this.loadingView);
    }

    public void showContentView() {
        showSingleView(this.contentView);
    }

    private void showSingleView(View specialView) {
        if(specialView == null)
            return;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child == specialView) {
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }

    @Data
    @AllArgsConstructor
    private static class SettingData {
        @LayoutRes
        private final int emptyViewLayoutResId;
        @LayoutRes
        private final int uselessViewLayoutResId;
        @LayoutRes
        private final int errorViewLayoutResId;
        @LayoutRes
        private final int loadingViewLayoutResId;

        private final int defaultShowMode;

        private final int editModeShowMode;
    }
}
