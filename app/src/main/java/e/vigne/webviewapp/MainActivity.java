package e.vigne.webviewapp;
// all imports
import android.content.Context;

import android.support.annotation.InterpolatorRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.webkit.WebView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;




public class MainActivity extends AppCompatActivity  {

        SensorManager senman;
        Sensor sen;
        double theta ;
        Integer h;
        Integer endgamec = 0;
 // initializing Firebase and setting variables
     FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mydata = database.getReference("angle/angle2");
    DatabaseReference healths = database.getReference("condition/condition2");
    DatabaseReference health2 = database.getReference("condition/condition1");



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// Streaming URLs
        String url1 ="http://192.168.2.17:8080/stream";
        WebView view1=(WebView) this.findViewById(R.id.webView1);
        view1.getSettings().setJavaScriptEnabled(true);
        view1.loadUrl(url1);
        String url2 ="http://192.168.2.15:8080/stream";
        WebView view2=(WebView) this.findViewById(R.id.webView2);
        view2.getSettings().setJavaScriptEnabled(true);
        view2.loadUrl(url2);
// starting the gyroscope to send data
        senman = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sen = senman.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
// initializing variable for text
     /*   textthetha = (TextView) findViewById(R.id.textView6);
       textcon = (TextView) findViewById(R.id.textView);
        textcon2 = (TextView) findViewById(R.id.textView3);

       */
    }

// function to establish delay between sensor readings
    public void onResume() {
        super.onResume();
        senman.registerListener(gyroListener,sen,SensorManager.SENSOR_DELAY_NORMAL);
// Function to end gyro reading
    }public void onStop(){
        super.onStop();
        senman.unregisterListener(gyroListener);
    }
// Function to detect sensor changes
    public SensorEventListener gyroListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
// Rough tuning based on given cardoard
            double dtheta = (x*60)/(2*3.14);
// calculating the final angle and accounting for 360 reading
            theta = theta +dtheta;
            if(theta > 360){
                theta = 0;

            }else if ( theta < 0){
                theta = 360;
            }
// updating theta on app and online database
//            textthetha.setText("angle" +(int)theta);

            if(endgamec == 1){
            mydata.setValue((int)500);
            }else{
            mydata.setValue((int)theta);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
// function to start editing the online database
    public void onStart(){
        super.onStart();
        final ProgressBar health_left =(ProgressBar)findViewById(R.id.progressBar);

        final ProgressBar health_right =(ProgressBar)findViewById(R.id.progressBar2);
        health_left.setVisibility(View.VISIBLE);
        health_right.setVisibility(View.VISIBLE);
       // Integer e;
        health2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer opphealth = dataSnapshot.getValue(Integer.class);
                if (opphealth == 0) {
                   // e = 1;
                    endgamec = 1;
                } else {
                   //  e = 0;
                    endgamec = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        healths.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Updating the local variable based on database
                Integer health = dataSnapshot.getValue(Integer.class);
       //         textcon.setText("health "+health);
         //       textcon2.setText("health "+health);
                h = health;
                health_left.setProgress(h);
                health_right.setProgress(h);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                 String health = "no data";
           //     textcon.setText("health "+health);


            }
        });


    }




    }




