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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class XunfeiActivity extends AppCompatActivity implements InitListener{

    Button start, save;
    TextView show, suoming;
    LottieAnimationView lottieAnimationView;
    String res = "您要保存的内容是：";
    int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xunfei);
        start = findViewById(R.id.startbtn);
        save = findViewById(R.id.savebtn);
        show = findViewById(R.id.textView);
        suoming = findViewById(R.id.suo);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        lottieAnimationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoice();
            }
        });
        lottieAnimationView.playAnimation();
        final DBclass DBBuilder = new DBclass(this, "Schedule", null, 1);
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("保存成功")
                .setMessage("保存成功！").setPositiveButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent1 = new Intent(XunfeiActivity.this, MainActivity.class);
                startActivity(intent1);
            }
        });
        Intent intent = getIntent();
        time = intent.getIntExtra("time", 0);
        suoming.setText("请点击开始识别按钮，然后在5秒内说出你想保存的内容，直到上面显示了你说的话为止，可以多次分批说话。您的话语会每次叠加。点击保存识别按钮确认想要保存的内容。");
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoice();
            }
        });
        TextView temp = new TextView(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (res != null) {
                    temp.setText(res);
                    builder.setTitle("请您确认要保存的内容")
                            .setMessage("点击确认确认添加，点击取消返回上级页面。")
                            .setView(temp)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SQLiteDatabase DB = DBBuilder.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("time", time);
                            String resrel = res.substring(9);
                            values.put("Schedule", resrel);
                            DB.insert("Event", null, values);
                            AlertDialog exit = builder1.create();
                            exit.show();
                            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            res = "您要保存的内容是：";
                            XunfeiActivity.this.finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }



    public void startVoice(){
        //1.创建RecognizerDialog对象
        RecognizerDialog recognizerDialog = new RecognizerDialog(XunfeiActivity.this, this);
        //2.设置accent、language等参数
        recognizerDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");//语种，这里可以有zh_cn和en_us
        recognizerDialog.setParameter(SpeechConstant.ACCENT, "mandarin");//设置口音，这里设置的是汉语普通话 具体支持口音请查看讯飞文档，
        recognizerDialog.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");//设置编码类型
        recognizerDialog.setParameter(SpeechConstant.VAD_BOS, "5000");
        recognizerDialog.setParameter(SpeechConstant.VAD_EOS, "2000");
        //其他设置请参考文档http://www.xfyun.cn/doccenter/awd
        //3.设置讯飞识别语音后的回调监听
        recognizerDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {//返回结果
                if (!b) {
                    Log.i("test_xunfei", recognizerResult.getResultString());
                    show.setText(result(recognizerResult.getResultString()));
                    res += show.getText().toString();
                }
            }

            @Override
            public void onError(SpeechError speechError) {//返回错误
                Log.e("test_xunfei", speechError.getErrorCode() + "");
            }

        });
        //显示讯飞语音识别视图
        recognizerDialog.show();
    }


    @Override
    public void onInit(int code) {
        if (code != ErrorCode.SUCCESS) {
            Toast.makeText(XunfeiActivity.this,"初始化失败，错误码：" + code,Toast.LENGTH_LONG).show();
        }
    }

    public String result(String resultString){
        JSONObject jsonObject = JSON.parseObject(resultString);
        JSONArray jsonArray = jsonObject.getJSONArray("ws");
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            JSONArray jsonArray1 = jsonObject1.getJSONArray("cw");
            JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
            String w = jsonObject2.getString("w");
            stringBuffer.append(w);
        }
        String result = stringBuffer.toString();
        Log.i("test_xunfei", "识别结果为：" + result);
        return result;
    }
}
