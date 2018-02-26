package org.butter.ithomecheckincalendar;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.butter.calendar.calendar.MonthCalendarView;
import org.butter.calendar.calendar.OnCalendarListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MonthCalendarView calendar = findViewById(R.id.calendar);
        calendar.setOnCalendarListener(new OnCalendarListener() {
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

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                calendar.setCheckInData(3072);
//            }
//        }, 500);
    }
}
