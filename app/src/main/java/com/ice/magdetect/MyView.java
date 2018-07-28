package com.ice.magdetect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;


public class MyView extends View{

    private int NUM;
    private float[] data1;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setNum(int num){
        NUM=num;
        data1=new float[NUM];
    }

    public void setData(float[] num1){
        data1=num1;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(NUM>0) {

            int Width = canvas.getWidth();
            int Height = canvas.getHeight();

            Paint paint1 = new Paint();
            paint1.setColor(Color.rgb(118, 238, 198));
            paint1.setStrokeWidth(10);
            paint1.setStyle(Paint.Style.STROKE);

            Paint paint2 = new Paint();
            paint2.setColor(Color.rgb(0, 0, 0));
            paint2.setStrokeWidth(5);
            paint2.setStyle(Paint.Style.STROKE);

            Paint paint3 = new Paint();
            paint3.setTextSize(15);
            paint3.setColor(Color.rgb(0, 0, 0));

            canvas.drawLine(Width/2,0,Width/2,Height,paint2);

            float max=data1[0];
            float min=data1[0];
            float ave=0f;

            for(int i=0;i<NUM;i++){
                ave+=data1[i];

                if(data1[i]>max){
                    max=data1[i];
                    continue;
                }

                if(data1[i]<min){
                    min=data1[i];
                }
            }
            ave/=NUM;

            float index1=Height/NUM;
            float index2=(Width-100)/(2*20);

            Path path1 = new Path();
            path1.moveTo(0, 0);
            for (int i = 0; i < NUM; i++) {
                if(data1[i]!=0f) {
                    path1.lineTo((data1[i]-ave) * index2+Width/2, index1*i);
                }
            }

            canvas.drawPath(path1, paint1);
        }
    }
}
