package org.butter.calendar.calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.util.Calendar;

/**
 *
 * Created by Butter on 2018/02/23.
 */
class CalendarUtils {

    /**
     * 通过年份和月份 得到当月有多少天
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        // 月份是从0开始的，先自增1位
        month++;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }

    /**
     * 返回当前月份1号位于周几
     *
     * @param year  年份
     * @param month 月份，传入系统获取的，不需要正常的
     * @return 日：1		一：2		二：3		三：4		四：5		五：6		六：7
     */
    public static int getFirstDayWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获得两个日期距离几周
     *
     * @return
     */
    public static int getWeeksAgo(int lastYear, int lastMonth, int lastDay, int year, int month, int day) {
        Calendar lastClickDay = Calendar.getInstance();
        lastClickDay.set(lastYear, lastMonth, lastDay);
        int week = lastClickDay.get(Calendar.DAY_OF_WEEK) - 1;
        Calendar clickDay = Calendar.getInstance();
        clickDay.set(year, month, day);
        if (clickDay.getTimeInMillis() > lastClickDay.getTimeInMillis()) {
            return (int) ((clickDay.getTimeInMillis() - lastClickDay.getTimeInMillis() + week * 24 * 3600 * 1000) / (7 * 24 * 3600 * 1000));
        } else {
            return (int) ((clickDay.getTimeInMillis() - lastClickDay.getTimeInMillis() + (week - 6) * 24 * 3600 * 1000) / (7 * 24 * 3600 * 1000));
        }
    }

    /**
     * 获得两个日期距离几个月
     *
     * @return
     */
    public static int getMonthsAgo(int lastYear, int lastMonth, int year, int month) {
        return (year - lastYear) * 12 + (month - lastMonth);
    }

    /**
     * 该具体日期在月视图中其前面的行数
     */
    public static int getWeekRow(int year, int month, int day) {
        int week = getFirstDayWeek(year, month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int lastWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (lastWeek == 7)
            day--;
        return (day + week - 1) / 7;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        if (width != newWidth || height != newHeight) {
            bm.recycle();
        }

        return resizedBitmap;
    }
}

