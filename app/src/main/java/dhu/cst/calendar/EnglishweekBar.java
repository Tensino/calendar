package dhu.cst.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;



import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.WeekBar;

public class EnglishweekBar extends WeekBar {

    private int mPreSelectedIndex;

    public EnglishweekBar(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.weekbar, this, true);
        setBackgroundColor(Color.WHITE);
        int padding = dipToPx(context, 10);
        setPadding(padding, 0, padding, 0);
    }

    @Override
    protected void onDateSelected(Calendar calendar, int weekStart, boolean isClick) {
        getChildAt(mPreSelectedIndex).setSelected(false);
        int viewIndex = getViewIndexByCalendar(calendar, weekStart);
        //getChildAt(viewIndex).setSelected(true);
        mPreSelectedIndex = viewIndex;
    }


    @Override
    protected void onWeekStartChange(int weekStart) {
        for (int i = 0; i < getChildCount(); i++) {
            ((TextView) getChildAt(i)).setText(getWeekString(i, weekStart));
        }
    }


    private String getWeekString(int index, int weekStart) {
        int temp = -200008;
        String[] weeks = getContext().getResources().getStringArray(temp);
        if (weekStart == 1) {
            return weeks[index];
        }
        if (weekStart == 2) {
            return weeks[index == 6 ? 0 : index + 1];
        }
        return weeks[index == 0 ? 6 : index - 1];
    }

    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
