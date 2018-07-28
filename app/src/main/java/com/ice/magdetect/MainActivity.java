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
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager manager;

    private TextView tv1,tv2;
    private Button bt1;
    private MyView myView;

    private boolean stop=false;

    private Handler handler;
    private Timer timer;
    private TimerTask task;

    private List<float[]> values=new ArrayList<>();
    private int Num=100;
    private int count=0;
    private float[] Mag=new float[Num];

    private File file;

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
        bt1=(Button)findViewById(R.id.button);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   stop=!stop;
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
        timer=new Timer();
        task=new TimerTask() {
            @Override
            public void run() {
                if(!stop) {
                    handler.sendEmptyMessage(0);
                }
            }
        };
        timer.schedule(task,1000,400);

        file=new File(this.getExternalFilesDir(null),"/"+System.currentTimeMillis()+"Mag.txt");
        Log.e("FileName",this.getExternalFilesDir(null)+"");
        if(file.exists()){
            file.delete();
        }

        try {
            file.createNewFile();
            // 获取文件的输出流对象
            FileOutputStream outStream = new FileOutputStream(file);
            // 获取字符串对象的byte数组并写入文件流
            outStream.write("Acc:".getBytes());
            // 最后关闭文件输出流
            outStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_MAGNETIC_FIELD:
                values.add(sensorEvent.values);
                break;
            default:
                break;
        }
    }

    private void refreshMag(float[] mag){
        Mag[count]=(float) Math.sqrt(mag[0]*mag[0]+mag[1]*mag[1]+mag[2]*mag[2]);

        FileWriter writer;
        try{
            writer=new FileWriter(file,true);
            writer.write(Mag[count]+",");
            writer.close();
        }catch (Exception e){
            Log.e("Error","117");
            e.printStackTrace();
        }

        float[]  data=new float[Num];
        for(int i=0,j=count;i<Num;i++){
            data[i]=Mag[j];

            if(--j<0){
                j+=Num;
            }
        }

        myView.setData(data);

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
