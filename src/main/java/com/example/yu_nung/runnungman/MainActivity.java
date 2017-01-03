package com.example.yu_nung.runnungman;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {
    Toolbar toolBar;
    TextView txtTimer;
    Button btnStart, btnStop, btnRestart;
    ShowMap mapFrag;

    private int startTime = 0;
    private Handler handler = new Handler();
    double distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTimer = (TextView) findViewById(R.id.txtTimer);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnRestart = (Button) findViewById(R.id.btnRestart);
        toolBar = (Toolbar) findViewById(R.id.tbToolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setLogo(R.mipmap.rm);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));



        mapFrag = new  ShowMap();
        getFragmentManager().beginTransaction().replace(R.id.flMap,mapFrag).commit();

        btnStart.setOnClickListener(listener);
        btnStop.setOnClickListener(listener);
        btnRestart.setOnClickListener(listener);
        txtTimer.setText("0:0");
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Dialog menuItemDialog = new Dialog(this);
        menuItemDialog.setContentView(R.layout.about);
        Toolbar tbDialog = (Toolbar) menuItemDialog.findViewById(R.id.tbDialog);
        tbDialog.setLogo(R.mipmap.rm);
        tbDialog.setTitle("關於"+getResources().getString(R.string.app_name));
        menuItemDialog.show();

        return super.onOptionsItemSelected(item);
    }

    public void checkDistance(LatLng myLatLng,LatLng distLatLng){
        String str = "";
        double[] point1 = new double[]{myLatLng.latitude,myLatLng.longitude};
        double[] point2 = new double[]{distLatLng.latitude,distLatLng.longitude};
        distance = distHaversine(point1, point2);
        str += "距離 : " + String.format("%.2f", distance) + "公里";
        Toast.makeText(getApplicationContext(), str,Toast.LENGTH_LONG).show();
        if(distance <= 0.01){
            btnRestart.setVisibility(Button.VISIBLE);
            handler.removeCallbacks(updateTimer);

        }
    }

    double distHaversine(double[] p1, double[] p2){
        double radius = (6356.752 + 6378.137)/2;
        if(p1[0]>-23.5 && p1[0]<23.5 && p2[0]>-23.5 && p2[0]<23.5)
            radius = 6378.137;
        else if((p1[0]<-66.5 && p2[0]<-66.5) || (p1[0]>66.5 && p2[0]>66.5))
            radius = 6356.752;
        double distLat = rad2deg(p2[0] - p1[0]);
        double distLon = rad2deg(p2[1] - p1[1]);
        double a = Math.sin(distLat / 2) * Math.sin(distLat / 2) +
                Math.cos(rad2deg(p1[0])) * Math.cos(rad2deg(p2[0]))
                        * Math.sin(distLon / 2) * Math.sin(distLon /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = radius * c;
        return dist;
    }

    double rad2deg(double ran){
        return ran * Math.PI /180;
    }

    public Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            startTime += 1;
            //計算目前已過分鐘數
            int minius = (startTime ) / 60;
            //計算目前已過秒數
            int seconds = (startTime) % 60;
            txtTimer.setText(minius + ":" + seconds);
            handler.postDelayed(this, 1000);
        }
    };

    public View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnStart:
                    handler.postDelayed(updateTimer, 0);
                    btnStart.setVisibility(Button.INVISIBLE);
                    btnRestart.setVisibility(Button.INVISIBLE);
                    break;

                case R.id.btnStop:
                    handler.removeCallbacks(updateTimer);
                    btnRestart.setVisibility(Button.VISIBLE);
                    break;

                case R.id.btnRestart:
                    startTime = 0;
                    btnStart.setVisibility(Button.VISIBLE);
                    txtTimer.setText("0:0");
                    break;
            }

        }
    };
}
