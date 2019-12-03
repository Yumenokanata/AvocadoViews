package indi.yume.view.avocadoviews.dsladapter;

import android.support.annotation.NonNull;
import android.support.v7.util.ListUpdateCallback;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by yume on 18-3-22.
 */

public interface Renderer<T> {
    int getItemCount(@NonNull T data);

    /**
     * Empty:
     * return RecyclerView.NO_ID
     */
    long getItemId(@NonNull T data, int index);

    int getItemViewType(@NonNull T data, int position);

    RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    void bind(@NonNull T data, int index, RecyclerView.ViewHolder holder);

    void recycle(RecyclerView.ViewHolder holder);

    boolean getUpdates(@NonNull final T oldData, @NonNull final T newData,
                       @NonNull final ListUpdateCallback listUpdateCallback);
}
