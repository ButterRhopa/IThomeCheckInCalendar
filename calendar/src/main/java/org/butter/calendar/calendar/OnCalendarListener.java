package org.butter.calendar.calendar;

/**
 *
 * Created by Butter on 2018/02/24.
 */
public interface OnCalendarListener {

    /**
     * 点击日期回调
     *
     * @param year 年
     * @param month 月，注意是真实月份，无需再加1
     * @param day 日
     * @param checked 点击日期是否已签到
     * @param gift 点击日期是否为特殊图片(礼物盒)日期
     */
    void onDateClick(int year, int month, int day, boolean checked, boolean gift);

    /**
     *
     *
     * @param oldYear 旧年份
     * @param oldMonth 旧月份，注意无需加1
     * @param newYear 新年份
     * @param newMonth 新月份，注意无需加1
     */
    void onMonthChange(int oldYear, int oldMonth, int newYear, int newMonth);
}
