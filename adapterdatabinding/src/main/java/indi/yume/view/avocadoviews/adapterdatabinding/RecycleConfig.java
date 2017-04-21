package indi.yume.view.avocadoviews.adapterdatabinding;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import indi.yume.view.avocadoviews.dsladapter.functions.Supplier;

@Retention(RetentionPolicy.SOURCE)
@IntDef(flag = true, value = {
        RecycleConfig.DO_NOTHING,
        RecycleConfig.CLEAR_ITEM,
        RecycleConfig.CLEAR_COLLECTION,
        RecycleConfig.CLEAR_HANDLERS,
        RecycleConfig.CLEAR_ALL,
})
public @interface RecycleConfig {
    /**
     * When the {@link RecyclerView} recycles a view, do nothing. This is the default behavior.
     */
    int DO_NOTHING = 0;

    /**
     * When the {@link RecyclerView} recycles a view, reset the item from the {@link Supplier}
     * to {@code null}.
     */
    int CLEAR_ITEM = 1;

    /**
     * When the {@link RecyclerView} recycles a view, reset and all handlers to {@code null}.
     */
    int CLEAR_HANDLERS = 1 << 1;

    /**
     * When the {@link RecyclerView} recycles a view, reset the collection from the
     * {@link Supplier} to {@code null}.
     */
    int CLEAR_COLLECTION = 1 << 2;

    /**
     * When the {@link RecyclerView} recycles a view, rebind all variables to {@code null}.
     */
    int CLEAR_ALL = CLEAR_ITEM | CLEAR_COLLECTION | CLEAR_HANDLERS;
}
