package indi.yume.view.avocadoviews.dsladapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * Created by yume on 17-4-20.
 */

public abstract class BaseRenderer<T> {
    public abstract int getItemCount(@NonNull T data);

    public long getItemId(@NonNull T data, int index) {
        return RecyclerView.NO_ID;
    }

    @LayoutRes
    public abstract int getLayoutResId(@NonNull T data, int index);

    public abstract void bind(@NonNull T data, int index, RecyclerView.ViewHolder holder);

    public void recycle(RecyclerView.ViewHolder holder) {}
}
