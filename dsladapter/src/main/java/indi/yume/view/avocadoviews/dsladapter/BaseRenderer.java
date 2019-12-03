package indi.yume.view.avocadoviews.dsladapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by yume on 17-4-20.
 */

public abstract class BaseRenderer<T> implements Renderer<T> {
    @Override
    public abstract int getItemCount(@NonNull T data);

    @Override
    public long getItemId(@NonNull T data, int index) {
        return RecyclerView.NO_ID;
    }

    @LayoutRes
    public abstract int getLayoutResId(@NonNull T data, int index);

    @Override
    public abstract void bind(@NonNull T data, int index, RecyclerView.ViewHolder holder);

    @Override
    public void recycle(RecyclerView.ViewHolder holder) {}

    @Override
    public int getItemViewType(@NonNull T data, int index) {
        return getLayoutResId(data, index);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int layoutResourceId) {
        return new RecyclerView.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false)) {};
    }

    @Override
    public boolean getUpdates(@NonNull T oldData, @NonNull T newData, @NonNull ListUpdateCallback listUpdateCallback) {
        return false;
    }
}
