package indi.yume.view.avocadoviews.dsladapter;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.ListUpdateCallback;

import java.util.ArrayList;
import java.util.List;

import static indi.yume.view.avocadoviews.dsladapter.Utils.checkArgument;
import static indi.yume.view.avocadoviews.dsladapter.Utils.checkState;

final class ListUpdateRecorder implements ListUpdateCallback {
    @Nullable
    private static ListUpdateRecorder recycledInstance;

    @NonNull
    static ListUpdateRecorder obtain(final int startingPositionOffset) {
        ListUpdateRecorder localInstance;
        synchronized (ListUpdateRecorder.class) {
            localInstance = recycledInstance;
            recycledInstance = null;
        }
        if (localInstance == null) {
            localInstance = new ListUpdateRecorder();
        }
        localInstance.positionOffset = startingPositionOffset;
        return localInstance;
    }

    @NonNull
    private final List<ListUpdateRecord> recordList = new ArrayList<>();
    /** Real record count in the {@link #recordList}. Allows reusing ListUpdateRecord objects. */
    private int recordCount;
    /** Offset to add to a relative position to calculate the absolute position; -1 if not ready. */
    private int positionOffset;
    /** Current item count, for verifying ranges; changes as items are inserted and removed. */
    private int itemCount;

    void nextPart(final int oldItemCount) {
        itemCount = oldItemCount;
    }

    private ListUpdateRecord nextRecord() {
        checkState(positionOffset != -1, "Illegal call to ListUpdateCallback");
        final int index = recordCount++;
        if (recordList.size() > index) {
            return recordList.get(index);
        }
        final ListUpdateRecord record = new ListUpdateRecord();
        recordList.add(record);
        return record;
    }

    @Override
    public void onInserted(final int position, final int count) {
        checkArgument(0 <= position && position <= itemCount, // position==itemCount to insert at end.
                "onInserted: invalid position");
        checkArgument(count >= 0, "onInserted: invalid count");
        final ListUpdateRecord record = nextRecord();
        record.action = ListUpdateAction.INSERT;
        record.position = positionOffset + position;
        record.secondParam = count;
        record.payload = null;
        itemCount += count;
    }

    @Override
    public void onRemoved(final int position, final int count) {
        checkArgument(0 <= position && position < itemCount, "onRemoved: invalid position");
        checkArgument(count >= 0 && position + count <= itemCount, "onRemoved: invalid count");
        final ListUpdateRecord record = nextRecord();
        record.action = ListUpdateAction.REMOVE;
        record.position = positionOffset + position;
        record.secondParam = count;
        record.payload = null;
        itemCount -= count;
    }

    @Override
    public void onMoved(final int fromPosition, final int toPosition) {
        checkArgument(0 <= fromPosition && fromPosition < itemCount, "onMoved: invalid fromPosition");
        checkArgument(0 <= toPosition && toPosition < itemCount, "onMoved: invalid toPosition");
        final ListUpdateRecord record = nextRecord();
        record.action = ListUpdateAction.MOVE;
        record.position = positionOffset + fromPosition;
        record.secondParam = positionOffset + toPosition;
        record.payload = null;
    }

    @Override
    public void onChanged(final int position, final int count, final Object payload) {
        checkArgument(0 <= position && position < itemCount, "onChanged: invalid position");
        checkArgument(count >= 0 && position + count <= itemCount, "onChanged: invalid count");
        final ListUpdateRecord record = nextRecord();
        record.action = ListUpdateAction.CHANGE;
        record.position = positionOffset + position;
        record.secondParam = count;
        record.payload = payload;
    }

    int commitCurrentEndPosition() {
        final int lastEndPosition = positionOffset + itemCount;
        positionOffset = lastEndPosition;
        return lastEndPosition;
    }

    void recycle() {
        recordCount = 0;
        positionOffset = -1;
        itemCount = 0;
        synchronized (ListUpdateRecorder.class) {
            recycledInstance = this;
        }
    }

    void replayAndRecycle(@NonNull final RendererAdapter adapter) {
        for (int i = 0; i < recordCount; i++) {
            final ListUpdateRecord record = recordList.get(i);
            switch (record.action) {
                case ListUpdateAction.INSERT:
                    adapter.notifyItemRangeInserted(record.position, record.secondParam);
                    break;
                case ListUpdateAction.REMOVE:
                    adapter.notifyItemRangeRemoved(record.position, record.secondParam);
                    break;
                case ListUpdateAction.MOVE:
                    adapter.notifyItemMoved(record.position, record.secondParam);
                    break;
                case ListUpdateAction.CHANGE:
                    adapter.notifyItemRangeChanged(record.position, record.secondParam, record.payload);
                    break;
            }
        }
        recycle();
    }

    @IntDef({
            ListUpdateAction.INSERT,
            ListUpdateAction.REMOVE,
            ListUpdateAction.MOVE,
            ListUpdateAction.CHANGE,
    })
    @interface ListUpdateAction {
        int INSERT = 1;
        int REMOVE = 2;
        int MOVE = 3;
        int CHANGE = 4;
    }

    static final class ListUpdateRecord {
        @ListUpdateAction int action;
        int position;
        int secondParam;
        Object payload;
    }
}
