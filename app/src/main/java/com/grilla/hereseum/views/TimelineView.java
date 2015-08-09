package com.grilla.hereseum.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grilla.hereseum.R;

import java.util.Calendar;

/**
 * Created by bill on 8/8/15.
 */
public class TimelineView extends LinearLayout {

    private static final int[] YEARS = {
            R.id.year10,
            R.id.year11,
            R.id.year12,
            R.id.year13,
            R.id.year14,
            R.id.year15
    };

    private static final int[] MONTHS = {
            R.id.month1,
            R.id.month2,
            R.id.month3,
            R.id.month4,
            R.id.month5,
            R.id.month6,
            R.id.month7,
            R.id.month8,
            R.id.month9,
            R.id.month10,
            R.id.month11,
            R.id.month12,
    };

    private TextView[] mYears;
    private TextView[] mMonths;

    private int mTextColor;
    private int mTextColorFaded;
    private int mTextColorDisabled;

    private OnDateSelectedListener mListener;

    private int mYear;
    private int mMonth;

    public TimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_timeline, this);

        mTextColor = context.getResources().getColor(R.color.text);
        mTextColorFaded = context.getResources().getColor(R.color.text_faded);
        mTextColorDisabled = context.getResources().getColor(R.color.text_disabled);

        mYears = new TextView[YEARS.length];
        for (int i = 0, l = YEARS.length; i < l; i++) {
            TextView tv = (TextView)findViewById(YEARS[i]);
            tv.setOnClickListener(mYearListener);
            mYears[i] = tv;
        }

        mMonths = new TextView[MONTHS.length];
        for (int i = 0, l = MONTHS.length; i < l; i++) {
            TextView tv = (TextView)findViewById(MONTHS[i]);
            tv.setOnClickListener(mMonthListener);
            mMonths[i] = tv;
        }

        mYear = 2015;
        mMonth = 7;
        updateMonthsText();
    }

    private int getYear(int id) {
        for (int i = 0, l = YEARS.length; i < l; i++) {
            if (YEARS[i] == id) {
                return i+2010;
            }
        }

        return -1;
    }

    private int getMonth(int id) {
        for (int i = 0, l = MONTHS.length; i < l; i++) {
            if (MONTHS[i] == id) {
                return i;
            }
        }

        return -1;
    }

    private OnClickListener mYearListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            int year = getYear(id);
            if (afterCurrentDate(year, mMonth)) {
                // If switching to the current year, with a month
                // from the future that you were at, move the month
                // to the most recent
                mMonth = Calendar.getInstance().get(Calendar.MONTH);
            }

            mYear = year;
            for (int i = 0, l = YEARS.length; i < l; i++) {
                mYears[i].setTextColor(id == YEARS[i] ? mTextColor : mTextColorFaded);
            }
            updateMonthsText();

            if (mListener != null) {
                mListener.onDateSelected(mYear, mMonth);
            }
        }
    };

    private OnClickListener mMonthListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            int month = getMonth(id);
            if (afterCurrentDate(mYear, month)) {
                return;
            }

            mMonth = month;
            updateMonthsText();

            if (mListener != null) {
                mListener.onDateSelected(mYear, mMonth);
            }
        }
    };

    private boolean afterCurrentDate(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        return (cal.getTimeInMillis() > Calendar.getInstance().getTimeInMillis());
    }

    private void updateMonthsText() {
        for (int i = 0, l = MONTHS.length; i < l; i++) {
            mMonths[i].setTextColor(mMonth == i ? mTextColor :
                    afterCurrentDate(mYear, i) ? mTextColorDisabled : mTextColorFaded);
        }
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        mListener = listener;
    }

    public interface OnDateSelectedListener {
        void onDateSelected(int year, int month);
    }
}
