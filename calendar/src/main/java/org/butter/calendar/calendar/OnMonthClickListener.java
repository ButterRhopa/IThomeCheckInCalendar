package org.butter.calendar.calendar;

/**
 *
 * Created by Butter on 2018/02/24.
 */
interface OnMonthClickListener {

    void onClickThisMonth(int year, int month, int day);
    void onClickLastMonth(int year, int month, int day);
    void onClickNextMonth(int year, int month, int day);
}
