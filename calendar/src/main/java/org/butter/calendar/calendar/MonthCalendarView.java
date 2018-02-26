package org.butter.calendar.calendar;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import java.util.Calendar;

/**
 *
 * Created by Butter on 2018/2/24.
 */

public class MonthCalendarView extends ViewPager implements OnMonthClickListener {

    private MonthAdapter mMonthAdapter;
    private OnCalendarListener mOnCalendarListener;
    private int mCurrentYear, mCurrentMonth;

    public MonthCalendarView(Context context) {
        this(context, null);
    }

    public MonthCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Calendar calendar = Calendar.getInstance();
        mCurrentYear = calendar.get(Calendar.YEAR);
        mCurrentMonth = calendar.get(Calendar.MONTH);

        mMonthAdapter = new MonthAdapter(context, this, attrs);
        setAdapter(mMonthAdapter);
        setCurrentItem(mMonthAdapter.getMonthCount() / 2, false);
        OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // 通知回调
                if (mOnCalendarListener != null) {
                    MonthView monthView = getCurrentMonthView();
                    if (monthView != null) {
                        int year = monthView.getSelectYear();
                        int month = monthView.getSelectMonth();

                        mOnCalendarListener.onMonthChange(mCurrentYear, mCurrentMonth + 1, year, month + 1);
                        mCurrentYear = year;
                        mCurrentMonth = month;
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

    @Override
    public void onClickThisMonth(int year, int month, int day) {
        // 通知回调
        if (mOnCalendarListener != null) {
            mOnCalendarListener.onDateClick(year, month + 1, day);
        }
    }

    @Override
    public void onClickLastMonth(int year, int month, int day) {
    }

    @Override
    public void onClickNextMonth(int year, int month, int day) {
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
}
