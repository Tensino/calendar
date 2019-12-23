package dhu.cst.calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.haibin.calendarview.CalendarView.*;


public class MainActivity extends AppCompatActivity implements CalendarView.OnCalendarSelectListener,View.OnClickListener {

    Calendar temporary;
    private CalendarView calendarView;
    Map<String, Calendar> map = new HashMap<>();
    private LinearLayout picker;
    private TextView tvMonth;
    public DBclass DB = new DBclass(MainActivity.this, "Schedule", null, 1);
    public Bundle bundle;
    public int t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5dfb273e");
        calendarView = findViewById(R.id.calendarView);
        picker = findViewById(R.id.picker);
        tvMonth = findViewById(R.id.tv_month);
        bundle = new Bundle();
        calendarView.setOnCalendarSelectListener(this);
        tvMonth.setText(calendarView.getCurYear() + "年" + calendarView.getCurMonth() + "月");
        //月份切换改变事件
        calendarView.setOnMonthChangeListener(new OnMonthChangeListener() {
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
        Intent in = new Intent(MainActivity.this, MyIntentService.class);
        String action = MyIntentService.ACTION_MUSIC;
        // 设置action
        in.setAction(action);
        startService(in);
    }
    @Override
    public void onClick(View v) {
        int year = calendarView.getCurYear();
        int month = calendarView.getCurMonth();
        int day = calendarView.getCurDay();
        Calendar temp = new Calendar();
        temp.setDay(day);
        temp.setMonth(month);
        temp.setYear(year);
        if(v.getId() == R.id.calendarView)
            onCalendarSelect(temp,true);
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
        Toast.makeText(this, String.format("%s : OutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }
    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        int year = calendar.getYear();
        int month = calendar.getMonth();
        int day = calendar.getDay();
        temporary = calendar;
        final int temp = calendar.getDay() + calendar.getMonth() + calendar.getYear();
        t = temp;
        final TextView textView = new TextView(this);
//        Handler handler = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                Bundle bundle = msg.getData();
//                String res = bundle.getString("res");
//                textView.setText(res);
//            }
//        };
        if (isClick) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("您想查看事件还是添加事件？");
            builder.setMessage("如果误点请点返回。查看事件界面下，长按下面的事件可以修改事件。");
            builder.setPositiveButton("添加事件", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    map.put(getSchemeCalendar(year, month, day, 0xFFe69138, "事").toString(),
                            getSchemeCalendar(year, month, day, 0xFFe69138, "事"));
                    calendarView.setSchemeDate(map);
                    bundle.putInt("day", temp);
                    Intent intent = new Intent(MainActivity.this,AddActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            registerForContextMenu(textView);
            final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
            builder.setNeutralButton("查看事件", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    SQLiteDatabase Duqu = DB.getReadableDatabase();
                    Cursor cursor = Duqu.query("Event", new String[]{"Schedule"}, "time=?", new String[]{String.valueOf(temp)}, null, null, null);
                    if (cursor.getCount() == 0) {
                        textView.setText("日程为空！");
                        builder2.setTitle("您还未添加日程").setMessage("长按下面的事件可以修改事件。").setView(textView);
                        AlertDialog dialog = builder2.create();
                        dialog.show();
                    } else {
                        cursor.moveToFirst();
                        String res = cursor.getString(cursor.getColumnIndex("Schedule"));
                        if (res.equals("")) {
                            textView.setText(res);
                            builder2.setTitle("您还未添加日程").setMessage("长按下面的事件可以修改事件。").setView(textView);
                            AlertDialog dialog = builder2.create();
                            dialog.show();
                        } else {
                            textView.setText(res);
                            builder2.setTitle("您的日程为")
                                    .setMessage("长按下面的事件可以修改事件。")
                                    .setView(textView);
                            AlertDialog dialog = builder2.create();
                            dialog.show();
                        }
                    }
                    cursor.close();
                }
            });
            builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.setHeaderTitle("您要怎样处理事件？");
        menu.add(1, 1, 1, "删除事件");
        menu.add(1, 2, 2, "更改事件");
        menu.add(1, 3, 3, "添加事件");
        MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onContextItemSelected(item);
                return true;
            }
        };
        for (int i = 0, n = menu.size(); i < n; i++)
            menu.getItem(i).setOnMenuItemClickListener(listener);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        super.onContextItemSelected(item);

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        Log.v("Test:", "context item seleted ID="+ menuInfo.id);

        SQLiteDatabase database = DB.getWritableDatabase();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        EditText editText = new EditText(this);
        switch(item.getItemId()){
            case 1:
                builder.setTitle("确认要删除吗？")
                .setMessage("删除后无法恢复！")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.delete("Event", "time = ?", new String[]{String.valueOf(t)});
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(100);
                        final int temp = temporary.getDay() + temporary.getMonth() + temporary.getYear();
                        SQLiteDatabase Duqu = DB.getReadableDatabase();
                        Cursor cursor = Duqu.query("Event", new String[]{"Schedule"}, "time=?", new String[]{String.valueOf(temp)}, null, null, null);
                        if (cursor.getCount() == 0) {
                            int year = temporary.getYear();
                            int month = temporary.getMonth();
                            int day = temporary.getDay();
                            map.put(getSchemeCalendar(year, month, day, 0xFF0000, "").toString(),
                                    getSchemeCalendar(year, month, day, 0xFF0000, ""));
                            calendarView.setSchemeDate(map);
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
            case 2:
                builder.setTitle("确认要更改吗？")
                        .setMessage("更改后无法取消！")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder1.setTitle("请输入要更改的内容").setView(editText)
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String res = editText.getText().toString();
                                        ContentValues values = new ContentValues();
                                        values.put("time", t);
                                        values.put("Schedule", res);
                                        Cursor cursor = database.query("Event", new String[]{"Schedule"}, "time=?", new String[]{String.valueOf(t)}, null, null, null);
                                        if (cursor.getCount() == 0) {
                                            database.insert("Event", null, values);
                                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            vibrator.vibrate(100);
                                        } else {
                                            database.update("Event", values, "time = ?", new String[]{
                                                    String.valueOf(t)
                                            });
                                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            vibrator.vibrate(100);
                                        }
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog dialog1 = builder1.create();
                                dialog1.show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                break;
            case 3:
                builder.setTitle("确认要添加吗？").setMessage("添加后没办法取消！")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder2.setTitle("请确认添加的事件").setView(editText).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = temporary.getYear();
                                int month = temporary.getMonth();
                                int day = temporary.getDay();
                                map.put(getSchemeCalendar(year, month, day, 0xFFe69138, "事").toString(),
                                        getSchemeCalendar(year, month, day, 0xFFe69138, "事"));
                                calendarView.setSchemeDate(map);
                                Cursor cursor = database.query("Event", new String[]{"Schedule"}, "time=?", new String[]{String.valueOf(t)}, null, null, null);
                                if (cursor.getCount() == 0) {
                                    String res = "";
                                    res += editText.getText().toString();
                                    ContentValues values = new ContentValues();
                                    values.put("time", t);
                                    values.put("Schedule", res);
                                    database.insert("Event", null, values);
                                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    vibrator.vibrate(100);
                                } else {
                                    cursor.moveToFirst();
                                    String res = cursor.getString(cursor.getColumnIndex("Schedule"));
                                    if (res.equals("")) {
                                        res += editText.getText().toString();
                                        ContentValues values = new ContentValues();
                                        values.put("time", t);
                                        values.put("Schedule", res);
                                        database.update("Event", values, "time = ?", new String[]{
                                                String.valueOf(t)
                                        });
                                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        vibrator.vibrate(100);
                                    } else {
                                        res += "\n";
                                        res += editText.getText().toString();
                                        ContentValues values = new ContentValues();
                                        values.put("time", t);
                                        values.put("Schedule", res);
                                        database.update("Event", values, "time = ?", new String[]{
                                                String.valueOf(t)
                                        });
                                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        vibrator.vibrate(100);
                                    }
                                }
                                cursor.close();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder2.create();
                        alertDialog.show();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE,1,1,"带阴历版");
        menu.add(Menu.NONE,2,2,"选中今天");

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
