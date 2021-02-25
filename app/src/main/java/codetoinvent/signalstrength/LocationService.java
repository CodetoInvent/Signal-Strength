package codetoinvent.signalstrength;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service {
    double latitude,longitude;
    NotificationCompat.Builder builder;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                 latitude = locationResult.getLastLocation().getLatitude();
                 longitude = locationResult.getLastLocation().getLongitude();
                MainActivity.latitude_tv.setText("Latitude: "+latitude);
                MainActivity.longitude_tv.setText("Longitude: "+longitude);
                MainActivity.signal_strength_display_tv.setText("Signal Strength : " + MainActivity.signalStrength_);
                Log.e("Location Update", "Latitude: " + latitude + " , Longitude: " + longitude+"Signal Strength: "+MainActivity.signalStrength_);
                builder.setContentText("Lat: " + latitude + "\n Long: " + longitude+"\nSig Str : " + MainActivity.signalStrength_);
              startForeground(Constants.Location_service_id,builder.build());

            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private void startlocationservice() {
        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent();

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
         builder = new NotificationCompat.Builder(getApplicationContext(),
                channelId);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Signal Strength");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Signal Strength : " + MainActivity.signalStrength_);
        builder.setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Signal Strength",
                        NotificationManager.IMPORTANCE_HIGH

                );

                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }

        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
       // startForeground(Constants.Location_service_id,builder.build());

    }

    private  void  stoplocationservice(){
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            String action =intent.getAction();
            if(action!=null){
                if(action.equals(Constants.Start_Location_service)){
                    startlocationservice();
                }else{
                    stoplocationservice();
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }
}

