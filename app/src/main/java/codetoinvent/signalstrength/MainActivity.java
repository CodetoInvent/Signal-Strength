package codetoinvent.signalstrength;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager; 
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



import eo.view.signalstrength.SignalStrengthView;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class MainActivity extends AppCompatActivity {
//start initializing map things



    private static final int REQUEST_CORE_LOCATION_PERMISSION=3;

    SignalStrengthView signalStrengthView;
   static String signalStrength_;
    private static final int PERMISSION_REQUEST_CODE = 100;
   static TextView signal_strength_display_tv,latitude_tv,longitude_tv;
    TelephonyManager telephonyManager;
    myPhoneStateListener psListener;
    Button start_tracking,stop_tracking;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signalStrengthView=findViewById(R.id.signalStrengthView);//just to show the signal strength visually
        signal_strength_display_tv=findViewById(R.id.signal_strength_display_tv);
        start_tracking=findViewById(R.id.start_tracking);
        stop_tracking=findViewById(R.id.stop_tracking);
        latitude_tv=findViewById(R.id.latitide_tv);
        longitude_tv=findViewById(R.id.longitude_tv);
        psListener = new myPhoneStateListener();
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        //it will continously listen the phone signal strength and update its strength dynamically
        telephonyManager.listen(psListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);


        if (
                ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION ,READ_PHONE_STATE,ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        button_click_();//to handle all the button clicks



    }

    public void button_click_(){
        start_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_CORE_LOCATION_PERMISSION);
                    } else{
                startlocationservice();
            }

            }
        });
        stop_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            stoplocationservice();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== REQUEST_CORE_LOCATION_PERMISSION&&grantResults.length>0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startlocationservice();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

            }
        }

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                ) {

                    return;

                }

        }

    }

    public class myPhoneStateListener extends PhoneStateListener {

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                signalStrength_= signalStrength.getCellSignalStrengths()+"";
            }else{
                signalStrength_=signalStrength.toString();
            }

///          signal_strength_display_tv.setText("Signal Strength : " + signalStrength.getCellSignalStrengths());
      }

    }


    private  boolean isLocationServiceRunning(){
        ActivityManager activityManager= (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager!=null){
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(LocationService.class.getName().equals(service.service.getClassName())){
                    return  true;
                }
            }
            return  false;
        }

        return false;
    }

    private  void startlocationservice(){

           Intent intent =new Intent(getApplicationContext(),LocationService.class);
           intent.setAction(Constants.Start_Location_service);
           startService(intent);
           Toast.makeText(this,"Location Service Started",Toast.LENGTH_SHORT).show();


    }

    public  void stoplocationservice(){
        if(isLocationServiceRunning()){
            Intent intent =new Intent(getApplicationContext(),LocationService.class);
            intent.setAction(Constants.Stop_Location_service);
            startService(intent);
            Toast.makeText(this,"Location Service Stopped",Toast.LENGTH_SHORT).show();


        }else {
            Toast.makeText(this,"Location Service NOT Stopped",Toast.LENGTH_SHORT).show();

        }
    }



}

