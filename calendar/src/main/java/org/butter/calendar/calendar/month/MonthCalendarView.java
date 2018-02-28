package org.butter.calendar.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import org.butter.calendar.R;
import org.butter.calendar.calendar.CalendarUtils;
import org.butter.calendar.calendar.OnCalendarListener;

import java.util.Calendar;
import java.util.List;

/**
 *
 * Created by Butter on 2018/2/24.
 */

public class MonthCalendarView extends ViewPager {

    private int test;

    static final int CHECK_IN_FLAG_SIZE = 11;
    static final int GIFT_BITMAP_SIZE = 30;

    private MonthAdapter mMonthAdapter;
    private OnCalendarListener mOnCalendarListener;
    private int mSelectYear, mSelectMonth, mSelectDay;
    private MonthView mSelectDateMonthView;

    private int mPastTextColor;
    private int mComingTextColor;
    private int mPreviousNextMonthTextColor;
    private int mColorAccent;
    private int mTextSize;
    private int mTodayBGColor;
    private Bitmap mCheckInFlagBitmap;
    private Bitmap mNormalGiftBitmap;
    private Bitmap mNormalGiftOpenBitmap;
    private boolean mDrawPreviousNextMonth;
    private boolean mEnableDateSelect;
    private boolean mShowHint;

    private boolean mDebug;

    public MonthCalendarView(Context context) {
        this(context, null);
    }

    public MonthCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Calendar calendar = Calendar.getInstance();
        mSelectYear = calendar.get(Calendar.YEAR);
        mSelectMonth = calendar.get(Calendar.MONTH);
        mSelectDay = calendar.get(Calendar.DAY_OF_MONTH);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MonthCalendarView);
        mPastTextColor = array.getColor(R.styleable.MonthCalendarView_month_past_text_color, Color.parseColor("#9c9cb8"));
        mComingTextColor = array.getColor(R.styleable.MonthCalendarView_month_coming_text_color, Color.parseColor("#333333"));
        mPreviousNextMonthTextColor = array.getColor(R.styleable.MonthCalendarView_month_previous_next_month_text_color, Color.parseColor("#ACA9BC"));
        mColorAccent = array.getColor(R.styleable.MonthCalendarView_month_color_accent, Color.parseColor("#d22222"));
        mTextSize = array.getInteger(R.styleable.MonthCalendarView_month_text_size, 14);
        mTodayBGColor = array.getColor(R.styleable.MonthCalendarView_month_today_bg_color, Color.parseColor("#e8e8e8"));
        mDrawPreviousNextMonth = array.getBoolean(R.styleable.MonthCalendarView_month_draw_previous_next_month, false);
        mEnableDateSelect = array.getBoolean(R.styleable.MonthCalendarView_month_date_select, false);
        array.recycle();

        int size = CalendarUtils.dip2px(getContext(), GIFT_BITMAP_SIZE);
        Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.check_in_gift_normal);
        mNormalGiftBitmap = CalendarUtils.getResizedBitmap(bm, size, size);
        bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.check_in_gift_open_normal);
        mNormalGiftOpenBitmap = CalendarUtils.getResizedBitmap(bm, size, size);
        size = CalendarUtils.dip2px(getContext(), CHECK_IN_FLAG_SIZE);
        bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.check_in_flag);
        mCheckInFlagBitmap = CalendarUtils.getResizedBitmap(bm, size, size);

        mMonthAdapter = new MonthAdapter(context, this);
        setAdapter(mMonthAdapter);
        setCurrentItem(mMonthAdapter.getMonthCount() / 2, false);
        mSelectDateMonthView = getCurrentMonthView();
        OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 通知回调
                if (mOnCalendarListener != null) {
                    MonthView monthView = getCurrentMonthView();
                    if (monthView != null) {
                        int year = monthView.getYear();
                        int month = monthView.getMonth();

                        mOnCalendarListener.onMonthChange(mSelectYear, mSelectMonth + 1, year, month + 1);
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
        addOnPageChangeListener(onPageChangeListener);
    }

    private MonthView getCurrentMonthView() {
        return mMonthAdapter.getViews().get(getCurrentItem());
    }

    /**
     * 设置点击日期监听
     */
    public void setOnCalendarListener(OnCalendarListener onCalendarListener) {
        mOnCalendarListener = onCalendarListener;
    }

    void onClickThisMonth(int year, int month, int day) {
        // 通知回调
        if (mOnCalendarListener != null) {
            mOnCalendarListener.onDateClick(year, month + 1, day);
        }
    }

    void onClickLastMonth(int year, int month, int day) {
        if (!mEnableDateSelect) {
            return;
        }

        try {
            setCurrentItem(getCurrentItem() - 1);
            MonthView monthView = getCurrentMonthView();
            monthView.clickThisMonth(year, month, day);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void onClickNextMonth(int year, int month, int day) {
        if (!mEnableDateSelect) {
            return;
        }

        try {
            setCurrentItem(getCurrentItem() + 1);
            MonthView monthView = getCurrentMonthView();
            monthView.clickThisMonth(year, month, day);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSelectYear() {
        return mSelectYear;
    }

    public int getSelectMonth() {
        return mSelectMonth;
    }

    public int getSelectDay() {
        return mSelectDay;
    }

    public int getCurrentShowYear() {
        MonthView monthView = getCurrentMonthView();
        if (monthView != null) {
            return monthView.getYear();
        }

        return -1;
    }

    public int getCurrentShowMonth() {
        MonthView monthView = getCurrentMonthView();
        if (monthView != null) {
            return monthView.getMonth();
        }

        return -1;
    }

    void setSelectDate(int year, int month, int day, MonthView monthView) {
        mSelectYear = year;
        mSelectMonth = month;
        mSelectDay = day;

        if (mSelectDateMonthView != null) {
            mSelectDateMonthView.invalidate();
        }
        mSelectDateMonthView = monthView;
    }

    int getPastTextColor() {
        return mPastTextColor;
    }

    int getComingTextColor() {
        return mComingTextColor;
    }

    int getPreviousNextMonthTextColor() {
        return mPreviousNextMonthTextColor;
    }

    int getColorAccent() {
        return mColorAccent;
    }

    int getTextSize() {
        return mTextSize;
    }

    int getTodayBGColor() {
        return mTodayBGColor;
    }

    Bitmap getCheckInFlagBitmap() {
        return mCheckInFlagBitmap;
    }

    Bitmap getNormalGiftBitmap() {
        return mNormalGiftBitmap;
    }

    Bitmap getNormalGiftOpenBitmap() {
        return mNormalGiftOpenBitmap;
    }

    boolean isDrawPreviousNextMonth() {
        return mDrawPreviousNextMonth;
    }

    boolean isEnableDateSelect() {
        return mEnableDateSelect;
    }

    boolean isShowHint() {
        return mShowHint;
    }

    public void setCheckInData(int data) {
        MonthView monthView = getCurrentMonthView();
        if (monthView != null) {
            monthView.setCheckInData(data);
        }
    }

    public void setGiftData(int data) {
        MonthView monthView = getCurrentMonthView();
        if (monthView != null) {
            monthView.setGiftData(data);
        }
    }

    public void setHintData(List<Integer> eventHintList, boolean show) {
        mShowHint = show;
        MonthView monthView = getCurrentMonthView();
        if (monthView != null) {
            monthView.setEventHintList(eventHintList);
        }
    }

    public void setShowHint(boolean show) {
        mShowHint = show;
        for (int i = 0; i < getChildCount(); i++) {
            MonthView monthView = (MonthView) getChildAt(i);
            if (monthView != null) {
                monthView.invalidate();
            }
        }
    }

    public void setPastTextColor(int pastTextColor) {
        mPastTextColor = pastTextColor;

        for (int i = 0; i < getChildCount(); i++) {
            MonthView monthView = (MonthView) getChildAt(i);
            if (monthView != null) {
                monthView.invalidate();
            }
        }
    }

    public void setComingTextColor(int comingTextColor) {
        mComingTextColor = comingTextColor;

        for (int i = 0; i < getChildCount(); i++) {
            MonthView monthView = (MonthView) getChildAt(i);
            if (monthView != null) {
                monthView.invalidate();
            }
        }
    }

    public void setColorAccent(int colorAccent) {
        mColorAccent = colorAccent;

        for (int i = 0; i < getChildCount(); i++) {
            MonthView monthView = (MonthView) getChildAt(i);
            if (monthView != null) {
                monthView.invalidate();
            }
        }
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;

        for (int i = 0; i < getChildCount(); i++) {
            MonthView monthView = (MonthView) getChildAt(i);
            if (monthView != null) {
                monthView.invalidate();
            }
        }
    }

    public void setTodayBGColor(int todayBGColor) {
        mTodayBGColor = todayBGColor;

        for (int i = 0; i < getChildCount(); i++) {
            MonthView monthView = (MonthView) getChildAt(i);
            if (monthView != null) {
                monthView.invalidate();
            }
        }
    }

    public void setNormalGiftBitmap(Bitmap normalGiftBitmap) {
        mNormalGiftBitmap = normalGiftBitmap;

        for (int i = 0; i < getChildCount(); i++) {
            MonthView monthView = (MonthView) getChildAt(i);
            if (monthView != null) {
                monthView.invalidate();
            }
        }
    }

    public void setNormalGiftOpenBitmap(Bitmap normalGiftOpenBitmap) {
        mNormalGiftOpenBitmap = normalGiftOpenBitmap;

        for (int i = 0; i < getChildCount(); i++) {
            MonthView monthView = (MonthView) getChildAt(i);
            if (monthView != null) {
                monthView.invalidate();
            }
        }
    }

    public void setCheckInFlagBitmap(Bitmap checkInFlagBitmap) {
        mCheckInFlagBitmap = checkInFlagBitmap;

        for (int i = 0; i < getChildCount(); i++) {
            MonthView monthView = (MonthView) getChildAt(i);
            if (monthView != null) {
                monthView.invalidate();
            }
        }
    }

    public void setDrawPreviousNextMonth(boolean drawPreviousNextMonth) {
        mDrawPreviousNextMonth = drawPreviousNextMonth;

        for (int i = 0; i < getChildCount(); i++) {
            MonthView monthView = (MonthView) getChildAt(i);
            if (monthView != null) {
                monthView.invalidate();
            }
        }
    }

    public void setPreviousNextMonthTextColor(int previousNextMonthTextColor) {
        mPreviousNextMonthTextColor = previousNextMonthTextColor;

        for (int i = 0; i < getChildCount(); i++) {
            MonthView monthView = (MonthView) getChildAt(i);
            if (monthView != null) {
                monthView.invalidate();
            }
        }
    }

    public void setEnableDateSelect(boolean enableDateSelect) {
        mEnableDateSelect = enableDateSelect;

        for (int i = 0; i < getChildCount(); i++) {
            MonthView monthView = (MonthView) getChildAt(i);
            if (monthView != null) {
                monthView.invalidate();
            }
        }
    }

    public void setDebug(boolean debug) {
        mDebug = debug;
    }

    public boolean isDebug() {
        return mDebug;
    }
}
