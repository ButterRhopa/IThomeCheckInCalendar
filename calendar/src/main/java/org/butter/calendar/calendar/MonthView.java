package org.butter.calendar.calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.butter.calendar.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 单月日历视图
 *
 * Created by Butter on 2018/2/23.
 */

class MonthView extends View {

    /**
     * 即一周有七天
     */
    private static final int NUM_COLUMNS = 7;
    /**
     * 6行在各种情况下都可以完整的显示所有DAY，空缺位置填入相邻月份的DAY
     */
    private static final int NUM_ROWS = 6;
    private static final int CHECK_IN_FLAG_SIZE = 11;
    private static final int GIFT_BITMAP_SIZE = 30;

    private int mPastTextColor;
    private int mComingTextColor;
    private int mColorAccent;
    private int mTextSize;
    private int mTodayBGColor;
    private Bitmap mCheckInFlagBitmap;
    private Bitmap mNormalGiftBitmap;
    private Bitmap mNormalGiftOpenBitmap;

    private Paint mPaint;
    private DisplayMetrics mDisplayMetrics;
    private int mColumnWidth, mRowHeight, mSelectCircleSize;
    private int[][] mDaysText;
    private int mSelYear, mSelMonth;
    private GestureDetector mGestureDetector;
    private OnMonthClickListener mDateClickListener;

    private List<Integer> mCheckInList = new ArrayList<>();
    private List<Integer> mGiftList = new ArrayList<>();

    // TODO: 2018/2/26 奶油测试
    {
        mCheckInList.add(1);
        mCheckInList.add(3);
        mCheckInList.add(7);
        mCheckInList.add(10);
        mCheckInList.add(17);
        mCheckInList.add(22);
        mCheckInList.add(30);
        mCheckInList.add(11);
        mCheckInList.add(12);
        mCheckInList.add(13);
        mCheckInList.add(14);
        mCheckInList.add(15);
        mCheckInList.add(16);
        mCheckInList.add(24);

        mGiftList.add(5);
        mGiftList.add(9);
        mGiftList.add(17);
        mGiftList.add(19);
        mGiftList.add(22);
    }

    public MonthView(Context context) {
        super(context);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    MonthView(Context context, AttributeSet attrs, int year, int month) {
        this(context);

        @SuppressLint("CustomViewStyleable")
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MonthCalendarView);
        mPastTextColor = array.getColor(R.styleable.MonthCalendarView_month_past_text_color, Color.parseColor("#9c9cb8"));
        mComingTextColor = array.getColor(R.styleable.MonthCalendarView_month_coming_text_color, Color.parseColor("#333333"));
        mColorAccent = array.getColor(R.styleable.MonthCalendarView_month_color_accent, Color.parseColor("#d22222"));
        mTextSize = array.getInteger(R.styleable.MonthCalendarView_month_text_size, 14);
        mTodayBGColor = array.getColor(R.styleable.MonthCalendarView_month_today_bg_color, Color.parseColor("#e8e8e8"));
        array.recycle();

        int size = CalendarUtils.dip2px(getContext(), GIFT_BITMAP_SIZE);
        Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.check_in_gift_normal);
        mNormalGiftBitmap = CalendarUtils.getResizedBitmap(bm, size, size);
        bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.check_in_gift_open_normal);
        mNormalGiftOpenBitmap = CalendarUtils.getResizedBitmap(bm, size, size);
        size = CalendarUtils.dip2px(getContext(), CHECK_IN_FLAG_SIZE);
        bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.check_in_flag);
        mCheckInFlagBitmap = CalendarUtils.getResizedBitmap(bm, size, size);

        mSelYear = year;
        mSelMonth = month;
        init();
    }

    private void init() {
        mDisplayMetrics = getResources().getDisplayMetrics();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize * mDisplayMetrics.scaledDensity);

        initGestureDetector();
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                doClickAction((int) e.getX(), (int) e.getY());
                return true;
            }
        });
    }

    private void doClickAction(int x, int y) {
        if (y > getHeight())
            return;
        int row = y / mRowHeight;
        int column = x / mColumnWidth;
        column = Math.min(column, 6);
        int clickYear = mSelYear, clickMonth = mSelMonth;
        if (row == 0) {
            if (mDaysText[row][column] >= 23) {
//                if (mSelMonth == 0) {
//                    clickYear = mSelYear - 1;
//                    clickMonth = 11;
//                } else {
//                    clickYear = mSelYear;
//                    clickMonth = mSelMonth - 1;
//                }
//                if (mDateClickListener != null) {
//                    // 点击了上个月份的日期
//                    mDateClickListener.onClickLastMonth(clickYear, clickMonth, mDaysText[row][column]);
//                }
            } else {
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column]);
            }
        } else {
            int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
            int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
            int nextMonthDays = 42 - monthDays - weekNumber + 1;
            if (mDaysText[row][column] <= nextMonthDays && row >= 4) {
//                if (mSelMonth == 11) {
//                    clickYear = mSelYear + 1;
//                    clickMonth = 0;
//                } else {
//                    clickYear = mSelYear;
//                    clickMonth = mSelMonth + 1;
//                }
//                if (mDateClickListener != null) {
//                    // 点击了下个月份的日期
//                    mDateClickListener.onClickNextMonth(clickYear, clickMonth, mDaysText[row][column]);
//                }
            } else {
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column]);
            }
        }
    }

    /**
     * 点击本月日期
     */
    private void clickThisMonth(int year, int month, int day) {
        // 通知上层的MonthCalendarView
        if (mDateClickListener != null) {
            mDateClickListener.onClickThisMonth(year, month, day);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 200;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initSize();
        clearData();
        drawThisMonth(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private void drawThisMonth(Canvas canvas) {
        int monthDays = CalendarUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mSelYear, mSelMonth);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String dayString;
        for (int d = 0; d < monthDays; d++) {
            dayString = String.valueOf(d + 1);
            int column = (d + weekNumber - 1) % 7;
            int row = (d + weekNumber - 1) / 7;
            mDaysText[row][column] = d + 1;
            int startX = (int) (mColumnWidth * column + (mColumnWidth - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowHeight * row + mRowHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2);

            // 画出当日日期的背景圆圈
            int startRecX = mColumnWidth * column;
            int startRecY = mRowHeight * row;
            int endRecX = startRecX + mColumnWidth;
            int endRecY = startRecY + mRowHeight;
            if (mSelYear == year && mSelMonth == month && d + 1 == day) {
                mPaint.setColor(mTodayBGColor);
                canvas.drawCircle((startRecX + endRecX) / 2, (startRecY + endRecY) / 2, mSelectCircleSize, mPaint);
            }

            drawCheckInFlag(row, column, d + 1, canvas);

            // 根据在今日前/今日后来区分文字颜色，当日使用强调色
            int color;
            // 判断年
            if (mSelYear < year) {
                color = mPastTextColor;
            } else if (mSelYear > year) {
                color = mComingTextColor;
            } else {
                // 判断月
                if (mSelMonth < month) {
                    color = mPastTextColor;
                } else if (mSelMonth > month) {
                    color = mComingTextColor;
                } else {
                    // 判断日
                    if (d + 1 < day) {
                        color = mPastTextColor;
                    } else {
                        color = mComingTextColor;
                    }
                }
            }
            mPaint.setColor(color);

            if (mGiftList.contains(d + 1) && mNormalGiftBitmap != null && mNormalGiftOpenBitmap != null) {
                // 使用礼物图片
                Bitmap bm = mCheckInList.contains(d + 1) ? mNormalGiftOpenBitmap : mNormalGiftBitmap;
                startX = mColumnWidth * column + (mColumnWidth - bm.getWidth()) / 2;
                startY = mRowHeight * row + mRowHeight / 2 - bm.getHeight() / 2;
                canvas.drawBitmap(bm, startX, startY, mPaint);
            } else {
                // 如果是1号，则该位置显示本月月份(如"2月")
                if (dayString.equals("1")) {
                    String s = (mSelMonth + 1) + "月";
                    startX = (int) (mColumnWidth * column + (mColumnWidth - mPaint.measureText(s)) / 2);
                    canvas.drawText((mSelMonth + 1) + "月", startX, startY, mPaint);
                } else {
                    canvas.drawText(dayString, startX, startY, mPaint);
                }
            }
        }
    }

    private void drawCheckInFlag(int row, int column, int day, Canvas canvas) {
        if (!mCheckInList.contains(day)) {
            return;
        }
        if (mGiftList.contains(day)) {
            return;
        }

//        mPaint.setColor(mColorAccent);
        float circleX = (float) (mColumnWidth * column + mColumnWidth * 0.75);
        float circleY = (float) (mRowHeight * row + mRowHeight * 0.3);
//        canvas.drawCircle(circleX, circleY, CalendarUtils.dip2px(getContext(), CHECK_IN_FLAG_SIZE), mPaint);

        int size = CalendarUtils.dip2px(getContext(), CHECK_IN_FLAG_SIZE);
        if (mCheckInFlagBitmap != null) {
            circleX = circleX - size / 2 - 5;
            circleY = circleY - size + 5;
            canvas.drawBitmap(mCheckInFlagBitmap, circleX, circleY, mPaint);
        }
    }

    private void initSize() {
        // 每列的宽度
        mColumnWidth = getWidth() / NUM_COLUMNS;
        // 每行的高度
        mRowHeight = getHeight() / NUM_ROWS;
        mSelectCircleSize = (int) (mColumnWidth / 3.2);
        // 选中圆圈的半径不能超过宽度的一半
        while (mSelectCircleSize > mRowHeight / 2) {
            mSelectCircleSize = (int) (mSelectCircleSize / 1.3);
        }
    }

    private void clearData() {
        mDaysText = new int[6][7];
    }

    public void setPastTextColor(int pastTextColor) {
        mPastTextColor = pastTextColor;
        invalidate();
    }

    public void setComingTextColor(int comingTextColor) {
        mComingTextColor = comingTextColor;
        invalidate();
    }

    public void setColorAccent(int colorAccent) {
        mColorAccent = colorAccent;
        invalidate();
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
        invalidate();
    }

    public void setNormalGiftBitmap(Bitmap normalGiftBitmap) {
        int size = CalendarUtils.dip2px(getContext(), GIFT_BITMAP_SIZE);
        mNormalGiftBitmap = CalendarUtils.getResizedBitmap(normalGiftBitmap, size, size);
        invalidate();
    }

    public void setNormalGiftOpenBitmap(Bitmap normalGiftOpenBitmap) {
        int size = CalendarUtils.dip2px(getContext(), GIFT_BITMAP_SIZE);
        mNormalGiftOpenBitmap = CalendarUtils.getResizedBitmap(normalGiftOpenBitmap, size, size);
        invalidate();
    }

    public void setOnDateClickListener(OnMonthClickListener dateClickListener) {
        this.mDateClickListener = dateClickListener;
    }

    public int getSelectYear() {
        return mSelYear;
    }

    public int getSelectMonth() {
        return mSelMonth;
    }

    public void setCheckInData(int data) {
        for (int i = 1; i <= 31; i++) {
            boolean checked = (data & (2 ^ (i - 1))) > 0;
            if (checked) {
                mCheckInList.add(i);
            }
        }

        invalidate();
    }

    public void setGiftData(int data) {
        for (int i = 1; i <= 31; i++) {
            boolean checked = (data & (2 ^ (i - 1))) > 0;
            if (checked) {
                mGiftList.add(i);
            }
        }

        invalidate();
    }
}
