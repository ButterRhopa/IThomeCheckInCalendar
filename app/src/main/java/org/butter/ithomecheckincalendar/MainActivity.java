package org.butter.ithomecheckincalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.butter.calendar.calendar.month.MonthCalendarView;
import org.butter.calendar.calendar.OnCalendarListener;

public class MainActivity extends AppCompatActivity {

    private MonthCalendarView mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCalendar = findViewById(R.id.calendar);
        mCalendar.setDebug(true);
        mCalendar.setOnCalendarListener(new OnCalendarListener() {
            @Override
            public void onDateClick(int year, int month, int day) {
                Toast.makeText(MainActivity.this, year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMonthChange(int oldYear, int oldMonth, int newYear, int newMonth) {
                String s = "旧日期：" + oldYear + "-" + oldMonth + "\n新日期：" + newYear + "-" + newMonth;
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });

        initOptions();
    }

    private void initOptions() {
        // 是否显示前、后月份的日期
        RadioGroup rg1 = findViewById(R.id.rg_showPreviousNextMonth);
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_showPreviousNextMonth_enable) {
                    // 显示
                    mCalendar.setDrawPreviousNextMonth(true);
                } else {
                    // 不显示
                    mCalendar.setDrawPreviousNextMonth(false);
                }
            }
        });

        // 是否可以选中日期
        RadioGroup rg2 = findViewById(R.id.rg_drawSelectCircle);
        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_drawSelectCircle_enable) {
                    // 可以
                    mCalendar.setEnableDateSelect(true);
                } else {
                    // 不可以
                    mCalendar.setEnableDateSelect(false);
                }
            }
        });

        RadioGroup rg3 = findViewById(R.id.rg_showHint);
        rg3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_showHint_enable) {
                    // 可以
                    mCalendar.setShowHint(true);
                } else {
                    // 不可以
                    mCalendar.setShowHint(false);
                }
            }
        });
    }
}
