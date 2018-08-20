package com.ice.magdetect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class MyView extends View{

    private int NUM;
    private float[] data1;
    private float[][] data2;

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
        data2=new float[NUM][3];
    }

    public void setData(float[] num1,float num2[][]){
        data1=num1;
        data2=num2;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(NUM>0) {
            int Width = canvas.getWidth();
            int Height = canvas.getHeight();

            Paint x = new Paint();
            x.setColor(Color.RED);
            x.setStrokeWidth(10);
            x.setTextSize(50);
            x.setStyle(Paint.Style.STROKE);

            Paint y = new Paint();
            y.setColor(Color.BLUE);
            y.setStrokeWidth(10);
            y.setTextSize(50);
            y.setStyle(Paint.Style.STROKE);

            Paint z = new Paint();
            z.setColor(Color.GREEN);
            z.setStrokeWidth(10);
            z.setTextSize(50);
            z.setStyle(Paint.Style.STROKE);

            Paint paint2 = new Paint();
            paint2.setColor(Color.rgb(0, 0, 0));
            paint2.setStrokeWidth(10);
            paint2.setStyle(Paint.Style.STROKE);

            Paint paint3 = new Paint();
            paint3.setStrokeWidth(4);
            paint3.setTextSize(50);
            paint3.setColor(Color.RED);
            paint3.setStyle(Paint.Style.STROKE);
            paint3.setAlpha(150);

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

            float range=200;
            float index1=Height/NUM;
            float index2=(Width)/range;

            Path path1 = new Path();
            path1.moveTo(0, 0);
            for (int i = 0; i < NUM; i++) {
                if(data1[i]!=0f) {
                    path1.lineTo((data1[i]+range/2) * index2, index1*i);
                }
            }

            paint2.setColor(Color.GRAY);
            canvas.drawPath(path1, paint2);

            Path pathx = new Path();
            Path pathy = new Path();
            Path pathz = new Path();
            pathx.moveTo(0, 0);
            pathy.moveTo(0, 0);
            pathz.moveTo(0, 0);

            for (int i = 0; i < NUM; i++) {
                pathx.lineTo((data2[i][0]+range/2) * index2, index1*i);
                pathy.lineTo((data2[i][1]+range/2) * index2, index1*i);
                pathz.lineTo((data2[i][2]+range/2) * index2, index1*i);
            }
            canvas.drawPath(pathx, x);
            canvas.drawPath(pathy, y);
            canvas.drawPath(pathz, z);

            canvas.drawText("最大值="+max,Width-400,40,paint3);
            canvas.drawText("均值="+ave,Width-400,100,paint3);
            canvas.drawText("最新值="+data1[0],Width-400,160,paint3);
            canvas.drawText("X",Width-250,220,x);
            canvas.drawText("Y",Width-150,220,y);
            canvas.drawText("Z",Width-50,220,z);
        }
    }
}
