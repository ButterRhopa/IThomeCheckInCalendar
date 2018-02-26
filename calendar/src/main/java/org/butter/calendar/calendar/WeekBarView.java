package org.butter.calendar.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import org.butter.calendar.R;


/**
 * 星期标头  日→六
 *
 * Created by Butter on 2018/02/23.
 */
public class WeekBarView extends View {

    /**
     * 字体颜色
     */
    private int mWeekTextColor;
    private int mWeekendTextColor;
    /**
     * 字体大小
     */
    private int mWeekSize;
    private Paint mPaint;
    private DisplayMetrics mDisplayMetrics;
    private String[] mWeekString;

    public WeekBarView(Context context) {
        this(context, null);
    }

    public WeekBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        // 从xml中获取字体颜色和大小
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WeekBarView);
        mWeekTextColor = array.getColor(R.styleable.WeekBarView_week_text_color, Color.parseColor("#80787892"));
        mWeekendTextColor = array.getColor(R.styleable.WeekBarView_week_text_color_weekend, Color.parseColor("#80e99900"));
        mWeekSize = array.getInteger(R.styleable.WeekBarView_week_text_size, 14);
        mWeekString = context.getResources().getStringArray(R.array.calendar_week);

        array.recycle();
    }

    private void initPaint() {
        mDisplayMetrics = getResources().getDisplayMetrics();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mWeekSize * mDisplayMetrics.scaledDensity);
    }

    public void setTextColor(int weekTextColor, int weekendTextColor) {
        mWeekTextColor = weekTextColor;
        mWeekendTextColor = weekendTextColor;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mDisplayMetrics.densityDpi * 30;
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = mDisplayMetrics.densityDpi * 300;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        // 一共需要画七个文字，等间距分布
        int columnWidth = width / 7;
        for (int i = 0; i < mWeekString.length; i++) {
            String text = mWeekString[i];
            int fontWidth = (int) mPaint.measureText(text);
            // 字体需要在每个区域小块里面居中
            int startX = columnWidth * i + (columnWidth - fontWidth) / 2;
            int startY = (int) (height / 2 - (mPaint.ascent() + mPaint.descent()) / 2);

            // 字体颜色
            if (text.equals("日") || text.equals("六")) {
                mPaint.setColor(mWeekendTextColor);
            } else {
                mPaint.setColor(mWeekTextColor);
            }

            canvas.drawText(text, startX, startY, mPaint);
        }
    }
}
