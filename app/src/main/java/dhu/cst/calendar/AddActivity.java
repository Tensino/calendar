package dhu.cst.calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.airbnb.lottie.LottieAnimationView;

public class AddActivity extends AppCompatActivity {

    Button Wordadd, Voiceadd;

    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memorial);
        Wordadd = findViewById(R.id.Wordadd);
        Voiceadd = findViewById(R.id.Voiceadd);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        lottieAnimationView.playAnimation();
        final Intent intent = getIntent();

        final Bundle bundle = intent.getExtras();
        final DBclass DBBuilder = new DBclass(this, "Schedule", null, 1);
        final int time = bundle.getInt("day");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("保存成功")
        .setMessage("保存成功！").setPositiveButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AddActivity.this.finish();
            }
        });
        final EditText editText = new EditText(this);
        Wordadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("请输入您想添加的日程。")
                        .setView(editText)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String temp = editText.getText().toString();
                        SQLiteDatabase DB = DBBuilder.getWritableDatabase();
                        //创建存放数据的ContentValues对象
                        ContentValues values = new ContentValues();
                        //像ContentValues中存放数据
                        values.put("time", time);
                        values.put("Schedule", temp);
                        DB.insert("Event", null, values);
                        AlertDialog exit = builder1.create();
                        exit.show();
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(100);
                    }
                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        Voiceadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(AddActivity.this, XunfeiActivity.class);
                intent1.putExtra("time", time);
                startActivity(intent1);
            }
        });
    }
}
