package com.StrivingRookies.superalbum.ui_menu;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TimeDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "SectionDecoration";

    private DecorationCallback callback;
    private TextPaint textPaint;
    private Paint paint;
    private int topGap;
    private Paint.FontMetrics fontMetrics;
    private int spanCount;





    public TimeDecoration(Test_ui context,int spanCount, DecorationCallback decorationCallback) {
        this.spanCount = spanCount;
        ;

        this.callback = decorationCallback;

        paint = new Paint();
        paint.setColor(Color.WHITE);

        textPaint = new TextPaint();
        textPaint.setTypeface( Typeface.DEFAULT_BOLD);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(80);
        textPaint.setColor( Color.BLACK);
        textPaint.getFontMetrics(fontMetrics);
        textPaint.setTextAlign(Paint.Align.LEFT);
        fontMetrics = new Paint.FontMetrics();
        topGap = 60;


    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        //pos=pos%spanCount;
        Log.i(TAG, "getItemOffsets：" + pos);
        String groupId = callback.getGroupId(pos);
        if (groupId==null) return;
        if (pos == 0 || isFirstInGroup(pos)) {//同组的第一个才添加padding
            outRect.top = topGap;

        } else {
            outRect.top = 0;

        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(view);
            //position=position%spanCount;
            String groupId = callback.getGroupId(position);
            if (groupId ==null) return;
            String textLine = callback.getGroupFirstLine(position);
            if (position == 0 || isFirstInGroup(position)) {

                float top = view.getTop() - topGap;
                float bottom = view.getTop();
                c.drawRect(left, top, right, bottom, paint);//绘制红色矩形
                c.drawText(textLine, left, bottom, textPaint);//绘制文本
            }else{

            }
        }
    }


    private boolean isFirstInGroup(int pos) {
        if (pos == 0) {
            return true;
        } else {
            String prevGroupId = callback.getGroupId(pos - 1);
            String groupId = callback.getGroupId(pos);
            if(prevGroupId.equals ( groupId )){

                return false;
            }else{

                return true;
            }
        }
    }

    public interface DecorationCallback {

        String getGroupId(int position);

        String getGroupFirstLine(int position);
    }





}
