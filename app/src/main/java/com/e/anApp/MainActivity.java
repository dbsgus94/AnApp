package com.e.anApp;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView textView;
    TextView textView5;
    ImageView imageView;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;
    ImageView imageView7;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    //걸음 수 카운터
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent = false;
    private int mStepOffset;
    public ProgressBar simpleProgressBar;
    public int maxStep;
    public Intent passedIntent;
    private IMyCounterService binder;
    private boolean running =true;

    static int mStepDetector;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /*** Service가 가지고있는 binder를 전달받는다.* 즉, Service에서 구체화한 getCount() 메소드를 받았습니다.*/
            binder = IMyCounterService.Stub.asInterface(service);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.pizza);

        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView2.setImageResource(R.drawable.hamburger);

        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView3.setImageResource(R.drawable.cake);

        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView4.setImageResource(R.drawable.chicken);

        imageView5 = (ImageView) findViewById(R.id.imageView5);
        imageView5.setImageResource(R.drawable.beer);

        imageView6 = (ImageView) findViewById(R.id.imageView6);
        imageView6.setImageResource(R.drawable.donut);

        imageView7 = (ImageView) findViewById(R.id.imageView7);
        imageView7.setImageResource(R.drawable.pasta);

        passedIntent = getIntent();
        //processCommand(passedIntent);



        maxStep = 8000;

        ImageButton refreshButton = (ImageButton) findViewById(R.id.mapsRefresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getIntent());
                finish();
            }
        });

        //걸음 수 카운터
        SharedPreferences stepPreferences = getSharedPreferences("stepPreferences", MODE_PRIVATE);
        mStepDetector = stepPreferences.getInt("mStepDetector", 0);

        simpleProgressBar=(ProgressBar) findViewById(R.id.progress);
        simpleProgressBar.setMax(maxStep);
        textView5= (TextView)findViewById(R.id.textView5);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager = (SensorManager)
                this.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                != null)
        {
            mSensor =
                    mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        }
        else
        {
            isSensorPresent = false;
        }
        
        //다이어트 자극 명언 보여주기
        /*다음 링크 참고
        https://m.blog.naver.com/PostView.nhn?blogId=dagymdieting&logNo=221306225461&proxyReferer=https%3A%2F%2Fwww.google.com%2F
        https://post.naver.com/viewer/postView.nhn?volumeNo=16844224&memberNo=41829949
        https://1boon.kakao.com/tlxpass/171117
        https://www.facebook.com/diettalk/posts/912318102119245/*/
        String diet[] = {"운동은 당신의 몸을 증오하기 때문이 아니라, 사랑하기 때문에 하는 거예요.",
                "아무것도 바꾸지 않으면 변하는 것은 없다.",
                "당신이 아무리 천천히 걷고 있다고 해도 쇼파에 엉덩이를 붙이고 있는 사람들보다는 낫다!",
                "근육을 붙이려면 몸을 바삐 움직여야지.",
                "사실, 난 할 수 있어.",
                "당신의 몸에 귀를 기울여라.",
                "나약함이 동반되지 않은 강인함이란 없다.",
                "당신이 변화의 주역!",
                "시도하지 않으면, 절대 알 수 없어요.",
                "어제보다 더 멋진 나를 만드세요.",
                "건강한 음식, 더 많은 움직임, 나 스스로를 사랑하는 일.",
                "나에게 집중하세요.",
                "길은 24시간 열려있다. 나가서 뛰어라.",
                "승리는 가장 끈기있는 자에게 돌아간다.",
                "계속 노력해야 해요. 지름길은 없어요.",
                "원하는 몸을 만들기 위해 지금의 몸을 부수자.",
                "포기하지 마세요. 시작은 언제나 힘든 법입니다.",
                "목표까지 아직 멀었을지 몰라도 어제보다 가까워졌어요.",
                "포기하고 도망치거나 더 노력하거나. 내 선택은?",
                "생각이 바뀌면 몸도 변하기 시작한다.",
                "가장 큰 거짓말, 나 내일부터 다이어트 할거야.",
                "당신의 몸은 당신의 라이프스타일을 반영한다.",
                "짧게라도 걷는 것이 아예 안 걷는 것보다 낫다.",
                "바라지만 말고 실천해라! 언제까지 바라기만 할건가!",
                "힘들지 않다면 바뀌지 않는다."};

        //다이어트 자극 명언을 random하게 보여주기
        SharedPreferences sp = getSharedPreferences("diet", MODE_PRIVATE);
        int index = sp.getInt("index", 0);
        textView  = findViewById(R.id.textView2);
        textView.setText(diet[index]);
        SharedPreferences.Editor editor = sp.edit();
        if(index == 24) index = 0;
        else index ++;
        editor.putInt("index", index);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSensorPresent)
        {
            mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isSensorPresent)
        {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mStepOffset == 0) {
            mStepOffset = (int)event.values[0];
            Intent intent = new Intent(MainActivity.this, SensorService.class);
            bindService(intent, connection,BIND_AUTO_CREATE);
            running = true;
            new Thread(new GetCountThread()).start();


        }
    }
    private class GetCountThread implements Runnable {
        private Handler handler = new Handler();

        @Override
        public void run() {
            while(running) {
                if(binder == null) {
                    continue;
                }


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                                int totalStep = mStepOffset + binder.getCount();
                                simpleProgressBar.setProgress(mStepOffset+binder.getCount() );
                                textView5.setText(""+totalStep +" / " + maxStep + " 걸음");

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });

                try {
                    Thread.sleep(500);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void gotoMaps(View view){
        if(view.getId() == R.id.toMapsbutton){

            //참고한 사이트: https://stackoverflow.com/questions/4298893/android-how-do-i-create-a-function-that-will-be-executed-only-once
            SharedPreferences mapsettings = getSharedPreferences("mapsettings", 0);
            final boolean firstStart = mapsettings.getBoolean("firstStart", true);
            final SharedPreferences.Editor mapsettingsEditor = mapsettings.edit();

            if(firstStart) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));

                mapsettingsEditor.putBoolean("firstStart", false);
                mapsettingsEditor.commit();
            } else {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            }
        }
    }


    //참고한 사이트: https://webnautes.tistory.com/1315
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) { //요청 코드가 PERMISSONS_REQUEST_CODE이고, 요청한 퍼시면 개수만큼 수신되었다면
            boolean check_result = true;
            for(int result : grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if(check_result) {
            } else {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정에서 퍼미션을 허용해야 합니다.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
