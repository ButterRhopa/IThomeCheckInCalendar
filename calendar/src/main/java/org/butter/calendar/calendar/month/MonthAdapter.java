package org.butter.calendar.calendar.month;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import org.butter.calendar.calendar.month.MonthCalendarView;
import org.butter.calendar.calendar.month.MonthView;
import org.joda.time.DateTime;

/**
 *
 * Created by Butter on 2018/02/24.
 */
class MonthAdapter extends PagerAdapter {

    /**
     * 缓存ViewPager中每个视图
     */
    private SparseArray<MonthView> mViews;
    private Context mContext;
    private MonthCalendarView mMonthCalendarView;
    /**
     * 显示的月份数，当前月份在中间
     */
    private int mMonthCount = 1000;

    MonthAdapter(Context context, MonthCalendarView monthCalendarView) {
        mContext = context;
        mMonthCalendarView = monthCalendarView;
        mViews = new SparseArray<>();
    }

    @Override
    public int getCount() {
        return mMonthCount;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mViews.get(position) == null) {
            int date[] = getYearAndMonth(position);
            MonthView monthView = new MonthView(mContext, date[0], date[1], mMonthCalendarView);
            if (mMonthCalendarView.isDebug()) {
                monthView.debugInit();
            }
            monthView.setId(position);
            monthView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            monthView.invalidate();
            mViews.put(position, monthView);
        }

        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    private int[] getYearAndMonth(int position) {
        int date[] = new int[2];
        DateTime time = new DateTime();
        time = time.plusMonths(position - mMonthCount / 2);
        date[0] = time.getYear();
        date[1] = time.getMonthOfYear() - 1;
        return date;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public SparseArray<MonthView> getViews() {
        return mViews;
    }

    public int getMonthCount() {
        return mMonthCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
