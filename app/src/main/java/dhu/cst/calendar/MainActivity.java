package dhu.cst.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Map<String, Calendar> map = new HashMap<>();
    private CalendarView calendarView;
    private LinearLayout picker;
    private TextView tvMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        calendarView = findViewById(R.id.calendarView);
        picker = findViewById(R.id.picker);
        tvMonth = findViewById(R.id.tv_month);

        initData();
        //初始化当前年月
        tvMonth.setText(calendarView.getCurYear() + "年" + calendarView.getCurMonth() + "月");
        //月份切换改变事件
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                tvMonth.setText(year + "年" + month + "月");
            }
        });
        final boolean[] type = {true, true, false, false, false, false};
        //时间选择器选择年月，对应的日历切换到指定日期
        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerView pvTime = new TimePickerBuilder(MainActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        java.util.Calendar c = java.util.Calendar.getInstance();
                        c.setTime(date);
                        int year = c.get(java.util.Calendar.YEAR);
                        int month = c.get(java.util.Calendar.MONTH);
                        //滚动到指定日期
                        calendarView.scrollToCalendar(year, month + 1, 1);
                    }
                }).setType(type).build();
                pvTime.show();
            }
        });

    }

    private void initData() {
        Calendar calendar1 = getSchemeCalendar(2018, 8, 11, "1");
        Calendar calendar2 = getSchemeCalendar(2018, 8, 12, "2");
        Calendar calendar3 = getSchemeCalendar(2018, 8, 13, "3");
        Calendar calendar4 = getSchemeCalendar(2018, 8, 6, "4");
        map.put(calendar1.toString(), calendar1);
        map.put(calendar2.toString(), calendar2);
        map.put(calendar3.toString(), calendar3);
        map.put(calendar4.toString(), calendar4);
        calendarView.setSchemeDate(map);
    }

    private Calendar getSchemeCalendar(int year, int month, int day, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setScheme(text);
        return calendar;
    }
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE,1,1,"lunar_mode");
        menu.add(Menu.NONE,2,2,"click_to_current");

        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case 1:
                Intent intent = new Intent(MainActivity.this,SimpleActivity.class);
                startActivity(intent);
                break;
            case 2:
                final boolean[] type = {true, true, false, false, false, false};

                        java.util.Calendar c = java.util.Calendar.getInstance();
                        int year = c.get(java.util.Calendar.YEAR);
                        int month = c.get(java.util.Calendar.MONTH);
                        int day = c.get(java.util.Calendar.DAY_OF_MONTH);
                        //滚动到指定日期
                        calendarView.scrollToCalendar(year, month + 1, day);
        }


        return true;
    }





}
