package indi.yume.view.avocadoviews.dataselect;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import indi.yume.tools.avocado.model.DayDate;
import indi.yume.view.avocadoviews.R;
import rx.functions.Action1;

/**
 * Created by yume on 16-3-25.
 */
public class CustomDatePickerDialog {
    public static final int MODE_YMD = 0x1;
    public static final int MODE_YM  = 0x10;

    @IntDef(value = {MODE_YMD, MODE_YM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MODE{}

    private Context context;

    private AlertDialog.Builder builder;
    private DialogViewHolder viewHolder;

    private OnClickOkListener onOkBtnClick;
    private Action1<Dialog> onCancelBtnClick;

    public CustomDatePickerDialog(Context context) {
        this(context, null);
    }

    public CustomDatePickerDialog(Context context, DayDate selectDay) {
        this(context, selectDay, null, null);
    }

    public CustomDatePickerDialog(Context context, DayDate selectDay, DayDate startDate, DayDate endDate) {
        this.context = context;
        builder = new AlertDialog.Builder(context);

        View contentView = LayoutInflater.from(context).inflate(R.layout.custom_date_picker_dialog_layout, null);
        viewHolder = new DialogViewHolder(
                contentView,
                selectDay != null ? new DayDate(selectDay) : new DayDate(),
                startDate,
                endDate);
        builder.setView(contentView);
    }

    public CustomDatePickerDialog setMode(@MODE int mode) {
        viewHolder.setMode(mode);
        return this;
    }

    public CustomDatePickerDialog setSeparatorColor(@ColorInt int color) {
        viewHolder.setSeparatorColor(color);
        return this;
    }

    public CustomDatePickerDialog setSeparatorColorRes(@ColorRes int color) {
        viewHolder.setSeparatorColor(ContextCompat.getColor(context, color));
        return this;
    }

    public CustomDatePickerDialog setCancelButton(@StringRes int textResId, Action1<Dialog> onCancelBtnClick) {
        return setCancelButton(context.getString(textResId), onCancelBtnClick);
    }

    public CustomDatePickerDialog setCancelButton(String text, Action1<Dialog> onCancelBtnClick) {
        this.onCancelBtnClick = onCancelBtnClick;

        viewHolder.dialogCancelBtn.setVisibility(View.VISIBLE);
        if(TextUtils.isEmpty(text))
            viewHolder.dialogCancelBtn.setVisibility(View.GONE);
        else
            viewHolder.dialogCancelBtn.setText(text);

        return this;
    }

    public CustomDatePickerDialog setOkButton(@StringRes int textResId, OnClickOkListener onOkBtnClick) {
        return setOkButton(context.getString(textResId), onOkBtnClick);
    }

    public CustomDatePickerDialog setOkButton(String text, OnClickOkListener onOkBtnClick) {
        this.onOkBtnClick = onOkBtnClick;

        viewHolder.dialogOkBtn.setVisibility(View.VISIBLE);
        if(TextUtils.isEmpty(text))
            viewHolder.dialogOkBtn.setVisibility(View.GONE);
        else
            viewHolder.dialogOkBtn.setText(text);

        return this;
    }

    public void showDialog() {
        Dialog dialog = builder.create();

        viewHolder.dialogCancelBtn.setOnClickListener(v -> {
            if (onCancelBtnClick != null)
                onCancelBtnClick.call(dialog);
            dialog.dismiss();
        });

        viewHolder.dialogOkBtn.setOnClickListener(v -> {
            if (onOkBtnClick != null)
                onOkBtnClick.onClick(dialog, viewHolder.getDate());
            dialog.dismiss();
        });

        dialog.show();

        context = null;
        builder = null;
    }

    /* @return year, month and day‘s date int[], month range is 1 to 12. */
    public interface OnClickOkListener {
        void onClick(Dialog dialog, @Size(3) int[] date);
    }

    static class DialogViewHolder {
        private static final DayDate today = new DayDate();

        MaterialNumberPicker yearNumPicker;
        MaterialNumberPicker monthNumPicker;
        MaterialNumberPicker dayNumPicker;
        TextView dialogCancelBtn;
        TextView dialogOkBtn;

        String yearString;
        String monthString;
        String dayString;

        @NonNull
        private DayDate selectDay;
        @Nullable
        private DayDate endDate;
        @Nullable
        private DayDate startDate;

        DialogViewHolder(View view) {
            this(view, new DayDate());
        }

        DialogViewHolder(View view,
                         @NonNull DayDate selectDay) {
            this(view, selectDay, null, new DayDate());
        }

        DialogViewHolder(View view,
                         @NonNull DayDate selectDay,
                         @Nullable DayDate startDate,
                         @Nullable DayDate endDate) {
            yearNumPicker = (MaterialNumberPicker) view.findViewById(R.id.year_num_picker);
            monthNumPicker = (MaterialNumberPicker) view.findViewById(R.id.month_num_picker);
            dayNumPicker = (MaterialNumberPicker) view.findViewById(R.id.day_num_picker);
            dialogCancelBtn = (TextView) view.findViewById(R.id.dialog_cancel_btn);
            dialogOkBtn = (TextView) view.findViewById(R.id.dialog_ok_btn);

            yearString = view.getContext().getString(R.string.year_unit);
            monthString = view.getContext().getString(R.string.month_unit);
            dayString = view.getContext().getString(R.string.day_unit);

            if(endDate != null && selectDay.getTime() > endDate.getTime()) {
//                throw new RuntimeException("Choice of a day can not be more than the end of the day");
                selectDay.setTime(endDate.getTime());
            }
            if(startDate != null) {
                if (endDate != null && startDate.getTime() > endDate.getTime()) {
                    if(startDate.equals(endDate))
                        startDate.setTime(endDate.getTime());
                    else
                        throw new RuntimeException("Start of a day can not be more than the end of the day");
                } else if (startDate.getTime() > selectDay.getTime()) {
//                    throw new RuntimeException("Start of a day can not be more than the choice of a day");
                    selectDay.setTime(startDate.getTime());
                }
            }

            this.selectDay = selectDay;
            this.startDate = startDate;
            this.endDate = endDate;
            init(view.getContext());
        }

        void setSeparatorColor(@ColorInt int color) {
            yearNumPicker.setSeparatorColor(color);
            monthNumPicker.setSeparatorColor(color);
            dayNumPicker.setSeparatorColor(color);
        }

        private void init(Context context) {
            yearNumPicker.setBackgroundColor(ContextCompat.getColor(context, R.color.color_gr7));
            monthNumPicker.setBackgroundColor(ContextCompat.getColor(context, R.color.color_gr7));
            dayNumPicker.setBackgroundColor(ContextCompat.getColor(context, R.color.color_gr7));

            initYearPicker();
            initMonthPicker();
            initDayPicker();
        }

        private void initYearPicker() {
            yearNumPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            yearNumPicker.setFormatter(value -> String.valueOf(value) + yearString);
            yearNumPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                selectDay.setYear(newVal);
                initMonthData();
                initDayData();
            });
            yearNumPicker.setWrapSelectorWheel(false);

            initYearData();
        }

        private void initMonthPicker() {
            monthNumPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            monthNumPicker.setFormatter(value -> String.valueOf(value) + monthString);
            monthNumPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                selectDay.setMonth(newVal - 1);
                initDayData();
            });
            monthNumPicker.setWrapSelectorWheel(true);

            initMonthData();
        }

        private void initDayPicker() {
            dayNumPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            dayNumPicker.setFormatter(value -> String.valueOf(value) + dayString);
            dayNumPicker.setOnValueChangedListener((picker, oldVal, newVal) -> selectDay.setDay(newVal));
            dayNumPicker.setWrapSelectorWheel(true);

            initDayData();
        }

        private void initYearData() {
            /* 日付選択範囲は1900~2100年です */
            int minYear = 1900;
            if(startDate != null)
                minYear = startDate.getYear();
            yearNumPicker.setMinValue(minYear);

            int maxYear = 2100;
            if(endDate != null)
                maxYear = endDate.getYear();
            yearNumPicker.setMaxValue(maxYear);

            int selectYear = Math.max(selectDay.getYear(), minYear);
            selectYear = Math.min(selectYear, maxYear);
            yearNumPicker.setValue(selectYear);

            yearNumPicker.setWrapSelectorWheel(false);
        }

        private void initMonthData() {
            int minMonth = 1;
            if(startDate != null && startDate.getYear() == selectDay.getYear())
                minMonth = startDate.getMonth() + 1;
            monthNumPicker.setMinValue(minMonth);

            int maxMonth = 12;
            if(endDate != null && selectDay.getYear() == endDate.getYear())
                maxMonth = endDate.getMonth() + 1;
            monthNumPicker.setMaxValue(maxMonth);

            int selectMonth = Math.max(selectDay.getMonth() + 1, minMonth);
            selectMonth = Math.min(selectMonth, maxMonth);
            monthNumPicker.setValue(selectMonth);
            selectDay.setMonth(selectMonth - 1);

            monthNumPicker.setWrapSelectorWheel(true);
        }

        private void initDayData() {
            int minDay = 1;
            if(startDate != null
                    && selectDay.getYear() == startDate.getYear()
                    && selectDay.getMonth() == startDate.getMonth())
                minDay = startDate.getDay();
            dayNumPicker.setMinValue(minDay);

            int maxOfMonth = selectDay.getMaxDay();
            if(endDate != null
                    && selectDay.getYear() == endDate.getYear()
                    && selectDay.getMonth() == endDate.getMonth())
                maxOfMonth = endDate.getDay();

            dayNumPicker.setMaxValue(maxOfMonth);

            int select = Math.min(selectDay.getDay(), maxOfMonth);
            select = Math.max(select, minDay);
            dayNumPicker.setValue(select);
            selectDay.setDay(select);

            dayNumPicker.setWrapSelectorWheel(true);
        }

        public void setMode(@MODE int mode) {
            switch (mode) {
                case MODE_YM:
                    dayNumPicker.setVisibility(View.GONE);
                    break;
                case MODE_YMD:
                    dayNumPicker.setVisibility(View.VISIBLE);
                    break;
            }
        }

        /*
        * @return year, month and day, month range is 1 to 12.
        */
        @Size(3)
        public int[] getDate() {
            return new int[]{
                    selectDay.getYear(),
                    selectDay.getMonth() + 1,
                    selectDay.getDay()};
        }
    }
}
