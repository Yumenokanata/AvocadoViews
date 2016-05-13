package indi.yume.view.avocadoviews.dataselect;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.StyleableRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

import indi.yume.tools.avocado.model.DayDate;
import indi.yume.tools.avocado.util.DisplayUtil;
import indi.yume.view.avocadoviews.R;
import lombok.Getter;
import rx.functions.Action1;

/**
 * Created by yume on 16-3-25.
 */
public class DateSelectPicker extends FrameLayout implements View.OnClickListener {
    private static final int YEAR = 1;
    private static final int MONTH = 2;
    private static final int DAY = 4;

    public static final int YMD = YEAR | MONTH | DAY;
    public static final int YM = YEAR | MONTH;

    @IntDef({YMD, YM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShowMode {
    }

    @Getter
    private
    @ShowMode
    int showMode = YMD;

    LinearLayout spinnerMainLayout;
    TextView mainSpinner;
    ImageView arrowImgView;
    ImageView underlineView;
    TextView errorTextview;

    @Nullable
    private DayDate selectDay;
    @Nullable
    private DayDate endDate;
    @Nullable
    private DayDate startDate;

    private OnSelectedListener mOnSelectedListener;

    public DateSelectPicker(Context context) {
        this(context, null);
    }

    public DateSelectPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.custom_spinner_layout, this);

        spinnerMainLayout = (LinearLayout) findViewById(R.id.spinner_main_layout);
        mainSpinner = (TextView) findViewById(R.id.main_spinner);
        arrowImgView = (ImageView) findViewById(R.id.arrow_img_view);
        underlineView = (ImageView) findViewById(R.id.underline_view);
        errorTextview = (TextView) findViewById(R.id.error_textview);

        if (attrs != null)
            init(context, attrs);

        clearDate();
        setOnClickListener(this);

        mainSpinner.setText("");
    }

//    private void init(TypedArray tArray) {
//        int count = tArray.getIndexCount();
//        for (int i = 0; i < count; i++)
//            setAttr(tArray.getIndex(i), tArray);
//        tArray.recycle();
//    }
//
//    private void setAttr(@StyleableRes int attrIndex, TypedArray tArray) {
//        if(attrIndex == R.styleable.DateSelectPicker_dsp_showMode) {
//            showMode = tArray.getIndex(attrIndex, YMD);
//        }
//    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.DateSelectPicker);

        arrowImgView.setVisibility(
                tArray.getBoolean(R.styleable.DateSelectPicker_dsp_hideArrow, false) ? GONE : VISIBLE);
        arrowImgView.setImageResource(
                tArray.getResourceId(R.styleable.DateSelectPicker_dsp_arrowImg, R.mipmap.ic_arrow_drop_down));
        MarginLayoutParams layoutParams = (MarginLayoutParams) arrowImgView.getLayoutParams();
        getAndSetSize(tArray,
                R.styleable.DateSelectPicker_dsp_arrowWidth,
                size -> layoutParams.width = size);
        getAndSetSize(tArray,
                R.styleable.DateSelectPicker_dsp_arrowHeight,
                size -> layoutParams.height = size);
        getAndSetSize(tArray,
                R.styleable.DateSelectPicker_dsp_arrowMarginStart,
                layoutParams::setMarginStart);
        getAndSetSize(tArray,
                R.styleable.DateSelectPicker_dsp_arrowMarginEnd,
                layoutParams::setMarginEnd);
        arrowImgView.setLayoutParams(layoutParams);

        getAndSetSize(tArray,
                R.styleable.DateSelectPicker_dsp_textSize,
                size -> mainSpinner.setTextSize(DisplayUtil.px2sp(context, size)));
        mainSpinner.setHintTextColor(
                tArray.getColor(
                        R.styleable.DateSelectPicker_dsp_hintTextColor,
                        ContextCompat.getColor(context, R.color.color_gr5)));
        mainSpinner.setHint(tArray.getString(R.styleable.DateSelectPicker_dsp_hintText));

        mainSpinner.setTextColor(
                tArray.getColor(
                        R.styleable.DateSelectPicker_dsp_textColor,
                        ContextCompat.getColor(context, R.color.color_gr1)));

        underlineView.setVisibility(
                tArray.getBoolean(R.styleable.DateSelectPicker_dsp_hideUnderline, false) ? GONE : VISIBLE);
        underlineView.setBackground(
                new ColorDrawable(
                        tArray.getColor(
                                R.styleable.DateSelectPicker_dsp_underlineColor,
                                ContextCompat.getColor(context, R.color.color_gr6)
                        )
                )
        );
        MarginLayoutParams underlineLayoutParams = (MarginLayoutParams) underlineView.getLayoutParams();
        getAndSetSize(tArray,
                R.styleable.DateSelectPicker_dsp_underlineHeight,
                size -> underlineLayoutParams.height = size);
        getAndSetSize(tArray,
                R.styleable.DateSelectPicker_dsp_underlineMarginTop,
                size -> underlineLayoutParams.topMargin = size);
        underlineView.setLayoutParams(underlineLayoutParams);

        ViewGroup.LayoutParams mainTextViewLayoutParams = mainSpinner.getLayoutParams();
        getAndSetSize(tArray,
                R.styleable.DateSelectPicker_dsp_textViewHeight,
                size -> mainTextViewLayoutParams.height = size);
        getAndSetSize(tArray,
                R.styleable.DateSelectPicker_dsp_textViewWidth,
                size -> mainTextViewLayoutParams.width = size);
        mainSpinner.setLayoutParams(mainTextViewLayoutParams);

        ViewGroup.LayoutParams mainLayoutParam = spinnerMainLayout.getLayoutParams();
        getAndSetSize(tArray,
                R.styleable.DateSelectPicker_dsp_viewWidth,
                size -> mainLayoutParam.width = size);
        spinnerMainLayout.setLayoutParams(mainLayoutParam);

        switch (tArray.getInt(R.styleable.DateSelectPicker_dsp_showMode, 1)) {
            case 2:
                setMode(YM);
                break;
            case 1:
            default:
                setMode(YMD);
                break;
        }

        tArray.recycle();
    }

    private static void getAndSetSize(TypedArray tArray, @StyleableRes int index, @NonNull Action1<Integer> setSize) {
        int size = tArray.getDimensionPixelSize(index, -5);
        if (size != -5) setSize.call(size);
    }

    @Override
    public void onClick(View v) {
        new CustomDatePickerDialog(getContext(), selectDay, startDate, endDate)
                .setMode(showMode2Mode(showMode))
                .setCancelButton(R.string.dialog_button_cancel, null)
                .setOkButton(
                        R.string.dialog_button_ok,
                        (dialog, date) -> {
                            setDate(date[0], date[1], date[2]);
                            if (mOnSelectedListener != null)
                                mOnSelectedListener.onSelect(date);
                            clearError();
                        })
                .showDialog();
    }

    @CustomDatePickerDialog.MODE
    private static int showMode2Mode(@ShowMode int showMode) {
        switch (showMode) {
            case YM:
                return CustomDatePickerDialog.MODE_YM;
            case YMD:
            default:
                return CustomDatePickerDialog.MODE_YMD;
        }
    }

    private void renderText() {
        if (selectDay == null) {
            mainSpinner.setText("");
            return;
        }

        switch (showMode) {
            case YM:
                mainSpinner.setText(String.format(Locale.getDefault(),
                        "%d年%d月",
                        selectDay.getYear(),
                        selectDay.getMonth() + 1));
                break;
            case YMD:
            default:
                mainSpinner.setText(String.format(Locale.getDefault(),
                        "%d年%d月%d日",
                        selectDay.getYear(),
                        selectDay.getMonth() + 1,
                        selectDay.getDay()));
                break;
        }
    }

    public void setMode(@ShowMode int showMode) {
        this.showMode = showMode;
        switch (showMode) {
            case YMD:
                mainSpinner.setHint("年月日");
                break;
            case YM:
                mainSpinner.setHint("年月");
                break;
        }
        renderText();
    }

    public void resetData() {
        setDate(new DayDate());
        renderText();
    }

    public void clearDate() {
        selectDay = null;
        renderText();
    }

    public void setStartDate(@Nullable DayDate startDate) {
        this.startDate = startDate;

        if(startDate != null && selectDay != null && startDate.getTime() > selectDay.getTime())
            selectDay.setTime(startDate.getTime());
        renderText();
    }

    public void setEndDate(@Nullable DayDate endDate) {
        this.endDate = endDate;

        if(endDate != null && selectDay != null && endDate.getTime() < selectDay.getTime())
            selectDay.setTime(endDate.getTime());
        renderText();
    }

    public void setDate(int year,
                        @IntRange(from = 1, to = 12) int month) {
        setDate(year, month, 1);
    }

    public void setDate(@Nullable DayDate selectDay) {
        if(selectDay == null) {
            selectDay = null;
            renderText();
            return;
        }

        setDate(selectDay.getYear(),
                selectDay.getMonth() + 1,
                selectDay.getDay());
    }

    public void setDate(int year,
                        @IntRange(from = 1, to = 12) int month,
                        int day) {
        if (selectDay == null)
            selectDay = new DayDate();

        selectDay.setYear(year);
        selectDay.setMonth(month - 1);
        selectDay.setDay(day);

        if(endDate != null && endDate.getTime() < selectDay.getTime())
            selectDay.setTime(endDate.getTime());
        if(startDate != null && startDate.getTime() > selectDay.getTime())
            selectDay.setTime(startDate.getTime());

        renderText();
    }

    @Size(3)
    @Nullable
    public int[] getDate() {
        if (selectDay == null)
            return null;

        return new int[]{
                selectDay.getYear(),
                selectDay.getMonth() + 1,
                selectDay.getDay()};
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        mOnSelectedListener = onSelectedListener;
    }

    public interface OnSelectedListener {
        /*
         * @param size [year, month, day], month is 1 to 12.
         */
        void onSelect(@Size(3) int[] date);
    }

    public void setErrorMsg(String errorMsg) {
        if (TextUtils.isEmpty(errorMsg))
            return;
        errorTextview.setText(errorMsg);
        errorTextview.setVisibility(VISIBLE);
        underlineView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_re1));
    }

    public void clearError() {
        errorTextview.setText("");
        errorTextview.setVisibility(GONE);
        underlineView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_gr6));

    }
}
