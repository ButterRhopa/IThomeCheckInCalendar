package org.butter.calendar.calendar.month;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.butter.calendar.calendar.CalendarUtils;

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

    private static final int HINT_CIRCLE_RADIUS = 6;

    private int mSelectDayTextColor = Color.WHITE;

    private MonthCalendarView mCalendarView;
    private Paint mPaint;
    private DisplayMetrics mDisplayMetrics;
    private int mColumnWidth, mRowHeight, mSelectCircleSize;
    private int[][] mDaysText;
    private int mYear, mMonth;
    private int mCurrentYear, mCurrentMonth, mCurrentDay;
    private GestureDetector mGestureDetector;

    private List<Integer> mCheckInList = new ArrayList<>();
    private List<Integer> mGiftList = new ArrayList<>();
    private List<Integer> mEventHintList;

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

        mEventHintList = new ArrayList<>();
        mEventHintList.add(1);
        mEventHintList.add(12);
        mEventHintList.add(17);
        mEventHintList.add(25);
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

    MonthView(Context context, int year, int month, MonthCalendarView calendarView) {
        this(context);

        mCalendarView = calendarView;
        mYear = year;
        mMonth = month;
        init();
    }

    private void init() {
        Calendar calendar = Calendar.getInstance();
        mCurrentYear = calendar.get(Calendar.YEAR);
        mCurrentMonth = calendar.get(Calendar.MONTH);
        mCurrentDay = calendar.get(Calendar.DATE);

        mDisplayMetrics = getResources().getDisplayMetrics();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mCalendarView.getTextSize() * mDisplayMetrics.scaledDensity);

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
        int clickYear = mYear, clickMonth = mMonth;
        if (row == 0) {
            if (mDaysText[row][column] >= 23) {
                if (mMonth == 0) {
                    clickYear = mYear - 1;
                    clickMonth = 11;
                } else {
                    clickYear = mYear;
                    clickMonth = mMonth - 1;
                }
                // 点击了上个月份的日期
                mCalendarView.onClickLastMonth(clickYear, clickMonth, mDaysText[row][column]);
            } else {
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column]);
            }
        } else {
            int monthDays = CalendarUtils.getMonthDays(mYear, mMonth);
            int weekNumber = CalendarUtils.getFirstDayWeek(mYear, mMonth);
            int nextMonthDays = 42 - monthDays - weekNumber + 1;
            if (mDaysText[row][column] <= nextMonthDays && row >= 4) {
                if (mMonth == 11) {
                    clickYear = mYear + 1;
                    clickMonth = 0;
                } else {
                    clickYear = mYear;
                    clickMonth = mMonth + 1;
                }
                // 点击了下个月份的日期
                mCalendarView.onClickNextMonth(clickYear, clickMonth, mDaysText[row][column]);
            } else {
                clickThisMonth(clickYear, clickMonth, mDaysText[row][column]);
            }
        }
    }

    /**
     * 点击本月日期
     */
    public void clickThisMonth(int year, int month, int day) {
        if (mCalendarView.isEnableDateSelect()) {
            // 重设选中日期
            mCalendarView.setSelectDate(year, month, day, this);
            // 重绘
            invalidate();
        }
        // 通知上层的MonthCalendarView
        mCalendarView.onClickThisMonth(year, month, day);
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

        // 画出上个月份的最后几天，填充开始的几个位置
        drawPreviousMonth(canvas);
        // 画出本月的所有DAY
        drawThisMonth(canvas);
        // 画出下个月份的最初几天，填充最后的位置
        drawNextMonth(canvas);
    }

    private void drawPreviousMonth(Canvas canvas) {
        if (!mCalendarView.isDrawPreviousNextMonth()) {
            return;
        }

        int lastYear, lastMonth;
        // 判断当前月份是不是一月
        if (mMonth == 0) {
            lastYear = mYear - 1;
            lastMonth = 11;
        } else {
            lastYear = mYear;
            lastMonth = mMonth - 1;
        }
        mPaint.setColor(mCalendarView.getPreviousNextMonthTextColor());
        int monthDays = CalendarUtils.getMonthDays(lastYear, lastMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mYear, mMonth);
        for (int day = 0; day < weekNumber - 1; day++) {
            mDaysText[0][day] = monthDays - weekNumber + day + 2;
            String dayString = String.valueOf(mDaysText[0][day]);
            int startX = (int) (mColumnWidth * day + (mColumnWidth - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(dayString, startX, startY, mPaint);
        }
    }

    private void drawNextMonth(Canvas canvas) {
        if (!mCalendarView.isDrawPreviousNextMonth()) {
            return;
        }

        mPaint.setColor(mCalendarView.getPreviousNextMonthTextColor());
        int monthDays = CalendarUtils.getMonthDays(mYear, mMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mYear, mMonth);
        int nextMonthDays = 42 - monthDays - weekNumber + 1;
        for (int day = 0; day < nextMonthDays; day++) {
            int column = (monthDays + weekNumber - 1 + day) % 7;
            int row = 5 - (nextMonthDays - day - 1) / 7;
            try {
                mDaysText[row][column] = day + 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
            String dayString = String.valueOf(mDaysText[row][column]);
            int startX = (int) (mColumnWidth * column + (mColumnWidth - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowHeight * row + mRowHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(dayString, startX, startY, mPaint);
        }
    }

    private void drawThisMonth(Canvas canvas) {
        int monthDays = CalendarUtils.getMonthDays(mYear, mMonth);
        int weekNumber = CalendarUtils.getFirstDayWeek(mYear, mMonth);

        for (int d = 0; d < monthDays; d++) {
            int day = d + 1;
            int column = (d + weekNumber - 1) % 7;
            int row = (d + weekNumber - 1) / 7;
            mDaysText[row][column] = day;
            int startX = (int) (mColumnWidth * column + (mColumnWidth - mPaint.measureText(String.valueOf(day))) / 2);
            int startY = (int) (mRowHeight * row + mRowHeight / 2 - (mPaint.ascent() + mPaint.descent()) / 2);

            // 画圆圈
            int startRecX = mColumnWidth * column;
            int startRecY = mRowHeight * row;
            int endRecX = startRecX + mColumnWidth;
            int endRecY = startRecY + mRowHeight;
            // 是否有选中日期
            if (mCalendarView.isEnableDateSelect() && isSelectDate(day)) {
                mPaint.setColor(mCalendarView.getColorAccent());
                canvas.drawCircle((startRecX + endRecX) / 2, (startRecY + endRecY) / 2, mSelectCircleSize, mPaint);
            } else {
                if (mYear == mCurrentYear && mMonth == mCurrentMonth && day == mCurrentDay) {
                    // 画出当日日期的背景圆圈
                    mPaint.setColor(mCalendarView.getTodayBGColor());
                    canvas.drawCircle((startRecX + endRecX) / 2, (startRecY + endRecY) / 2, mSelectCircleSize, mPaint);
                }
            }

            drawCheckInFlag(row, column, day, canvas);
            drawHintCircle(row, column, day, canvas);

            // 根据在今日前/今日后、是否选中来区分文字颜色
            int color;
            if (mCalendarView.isEnableDateSelect() && isSelectDate(day)) {
                color = mSelectDayTextColor;
            } else {
                // 判断年
                if (mYear < mCurrentYear) {
                    color = mCalendarView.getPastTextColor();
                } else if (mYear > mCurrentYear) {
                    color = mCalendarView.getComingTextColor();
                } else {
                    // 判断月
                    if (mMonth < mCurrentMonth) {
                        color = mCalendarView.getPastTextColor();
                    } else if (mMonth > mCurrentMonth) {
                        color = mCalendarView.getComingTextColor();
                    } else {
                        // 判断日
                        if (day < mCurrentDay) {
                            color = mCalendarView.getPastTextColor();
                        } else {
                            color = mCalendarView.getComingTextColor();
                        }
                    }
                }
            }
            mPaint.setColor(color);

            if (mGiftList.contains(day) && mCalendarView.getNormalGiftBitmap() != null && mCalendarView.getNormalGiftOpenBitmap() != null) {
                // 使用礼物图片
                Bitmap bm = mCheckInList.contains(day) ? mCalendarView.getNormalGiftOpenBitmap() : mCalendarView.getNormalGiftBitmap();
                startX = mColumnWidth * column + (mColumnWidth - bm.getWidth()) / 2;
                startY = mRowHeight * row + mRowHeight / 2 - bm.getHeight() / 2;
                canvas.drawBitmap(bm, startX, startY, mPaint);
            } else {
                // 如果是1号，则该位置显示本月月份(如"2月")
                if (day == 1) {
                    String s = (mMonth + 1) + "月";
                    startX = (int) (mColumnWidth * column + (mColumnWidth - mPaint.measureText(s)) / 2);
                    canvas.drawText((mMonth + 1) + "月", startX, startY, mPaint);
                } else {
                    canvas.drawText(String.valueOf(day), startX, startY, mPaint);
                }
            }
        }
    }

    private boolean isSelectDate(int day) {

        return mYear == mCalendarView.getSelectYear() && mMonth == mCalendarView.getSelectMonth() && day == mCalendarView.getSelectDay();
    }

    private void drawCheckInFlag(int row, int column, int day, Canvas canvas) {
        if (!mCheckInList.contains(day)) {
            return;
        }
        if (mGiftList.contains(day)) {
            return;
        }

//        mPaint.setColor(mCalendarView.getColorAccent());
        float circleX = (float) (mColumnWidth * column + mColumnWidth * 0.75);
        float circleY = (float) (mRowHeight * row + mRowHeight * 0.3);
//        canvas.drawCircle(circleX, circleY, CalendarUtils.dip2px(getContext(), MonthCalendarView.CHECK_IN_FLAG_SIZE), mPaint);

        int size = CalendarUtils.dip2px(getContext(), MonthCalendarView.CHECK_IN_FLAG_SIZE);
        if (mCalendarView.getCheckInFlagBitmap() != null) {
            circleX = circleX - size / 2 - 5;
            circleY = circleY - size + 5;
            canvas.drawBitmap(mCalendarView.getCheckInFlagBitmap(), circleX, circleY, mPaint);
        }
    }

    /**
     * 绘制圆点提示
     */
    private void drawHintCircle(int row, int column, int day, Canvas canvas) {
        if (mCalendarView.isShowHint() && mEventHintList != null && mEventHintList.size() > 0) {
            if (!mEventHintList.contains(day)) return;
            mPaint.setColor(mCalendarView.getColorAccent());
            float circleX = (float) (mColumnWidth * column + mColumnWidth * 0.5);
            float circleY = (float) (mRowHeight * row + mRowHeight * 0.75);
            canvas.drawCircle(circleX, circleY, HINT_CIRCLE_RADIUS, mPaint);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
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

    public int getYear() {
        return mYear;
    }

    public int getMonth() {
        return mMonth;
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

    public void setEventHintList(List<Integer> eventHintList) {
        mEventHintList = eventHintList;
        if (mCalendarView.isShowHint()) {
            invalidate();
        }
    }
}
