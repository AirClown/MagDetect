package com.ice.magdetect;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager manager;

    private TextView tv1,tv2;
    private Button bt1,bt2,bt3,bt4,bt5;
    private MyView myView;
    private ImageView iv;

    private boolean stop=false;

    private Handler handler;
    private Timer timer;
    private TimerTask task;

    private List<float[]> values2=new ArrayList<>();
    private List<float[]> values=new ArrayList<>();
    private int Num=100;
    private int count=0;
    private float[] Mag=new float[Num];

    private float[][] Mag_XYZ=new float[Num][3];

    private int A_num=20;
    private float[] Angle=new float[A_num];
    private int angle_count=0;
    private float cuAngle=0;

    private MyFile file,file2;

    private boolean record=false;
    private boolean ori=false;
    private boolean xyz=false;
    private String addr="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Init();
    }

    private void Init(){
        tv1=(TextView)findViewById(R.id.textView);
        tv2=(TextView)findViewById(R.id.textView2);
        iv=(ImageView)findViewById(R.id.imageView);
        bt1=(Button)findViewById(R.id.button);
        bt2=(Button)findViewById(R.id.record);
        bt3=(Button)findViewById(R.id.button2);
        bt4=(Button)findViewById(R.id.button3);
        bt5=(Button)findViewById(R.id.button5);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stop){
                    Toast.makeText(MainActivity.this, "继续记录数据", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "暂停记录数据", Toast.LENGTH_SHORT).show();
                }
                stop=!stop;
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ori){
                    Toast.makeText(MainActivity.this, "添加补偿", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "取消补偿", Toast.LENGTH_SHORT).show();
                }
                ori=!ori;
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!record){
                    record=true;
                    file=new MyFile(MainActivity.this,"Mag");
                    file.CreateFile();

                    file2=new MyFile(MainActivity.this,"Light");
                    file2.CreateFile();

                    timer.purge();
                    timer.cancel();
                    task.cancel();
                    StartTimer(400);
                    Toast.makeText(MainActivity.this, "开始记录人行数据", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "重复点击", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!record){
                    record=true;
                    file=new MyFile(MainActivity.this,"Mag");
                    file.CreateFile();

                    file2=new MyFile(MainActivity.this,"Light");
                    file2.CreateFile();

                    StartTimer(100);
                    Toast.makeText(MainActivity.this, "开始记录车载数据", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "重复点击", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(file!=null) {
                    file.Cancel();
                    file = null;

                    file2.Cancel();
                    file2=null;
                    StartTimer(200);
                    record = false;

                    Toast.makeText(MainActivity.this, "重新生成文件", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "已重新生成文件", Toast.LENGTH_SHORT).show();
                }

            }
        });

        myView=(MyView)findViewById(R.id.myview);
        myView.setNum(Num);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                float[] num=new float[3];
                for(float[] v:values){
                    num[0]+=v[0];
                    num[1]+=v[1];
                    num[2]+=v[2];
                }
                num[0]/=values.size();
                num[1]/=values.size();
                num[2]/=values.size();
                values.clear();
                refreshMag(num);
            }
        };

        StartTimer(200);
    }

    private void StartTimer(int time){
        if (timer!=null) {
            timer.purge();
            timer.cancel();
            task.cancel();
            timer = null;
            task = null;
        }

        timer=new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
                if(!stop) {
                    handler.sendEmptyMessage(0);
                }
            }
        };
        timer.schedule(task,1000,time);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD:
                if (!ori){
                    values.add(sensorEvent.values);
                }
                break;
            case Sensor.TYPE_ORIENTATION:
                cuAngle=sensorEvent.values[0];
                Angle[angle_count]=(float) Math.PI*(sensorEvent.values[0])/180;

                if(++angle_count==A_num){
                    angle_count=0;
                }
                break;
            case Sensor.TYPE_LIGHT:
                values2.clear();
                values2.add(sensorEvent.values);
                Log.e("G",(int)sensorEvent.values[0]+","+System.currentTimeMillis());
                break;
            default:
                break;
        }
    }

    private void refreshMag(float[] mag){
        Mag[count]=(float) Math.sqrt(mag[0]*mag[0]+mag[1]*mag[1]+mag[2]*mag[2]);
        Mag_XYZ[count]=mag;

        if(record&&!stop) {
            file.WriteIntoFile("" + Mag[count]);
            file2.WriteIntoFile(""+values2.get(values2.size()-1)[0]);
        }

        float angle=0;
        float[]  data=new float[Num];
        float[][] data2=new float[Num][3];
        int c=0;
        for(int i=0,j=count;i<Num;i++){
            data[i]=Mag[j];
            data2[i]=Mag_XYZ[j];

            if(--j<0){
                j+=Num;
            }

            if (i<A_num) {
                angle += Angle[i];
            }
        }

        angle=angle/A_num;
        iv.setPivotX(iv.getWidth()/2);
        iv.setPivotY(iv.getHeight()/2);//支点在图片中心
        iv.setRotation(cuAngle);

        myView.setData(data,data2);

        if(++count==Num){
            count=0;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(flag);
        }

        Sensor mag=manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        manager.registerListener(this,mag,SensorManager.SENSOR_DELAY_UI);

        Sensor d=manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        manager.registerListener(this,d,SensorManager.SENSOR_DELAY_UI);

        Sensor light=manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        manager.registerListener(this,light,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
