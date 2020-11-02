package com.note11.project911server;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.note11.project911server.databinding.ActivityMainBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int GPS_ENABLE_REQUEST_CODE = 5000;
    private static final int PERMISSIONS_REQUEST_CODE = 4000;
    private LocationManager mLM;
    private DatabaseReference mPostReference;
    private long getStartTime;
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            float accuracy = location.getAccuracy();    //신뢰도

            uploadAtFirebase(longitude, latitude, accuracy);
            updateUI(longitude, latitude, accuracy, true);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (!locationStatus()) dialogWhenOffLocationService();
        else {
            if (!checkPermission()) {
                startActivity(new Intent(this, getPermissionActivity.class));
                finish();
            }
        }
        binding.btnUpdateStart.setOnClickListener(v -> {
            getStartTime = new Date().getTime();
            uploadRTDB(true,  0, 0, 1, getStartTime);
            start(true);
        });
        binding.btnUpdateFinish.setOnClickListener(v -> start(false));
    }

    private void start(Boolean startORFinish) {
        //Boolean startOrFinish : true==start, false==finish
        //using firebase realtime Database
        if (startORFinish) {
            //start update service
            //service update cycle time : 2sec / 2meter
            //위 내용이 작동하지 않다면, onLocationChanged function 사용
            startGPSService();
            Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
        } else {
            //finish update service
            mLM.removeUpdates(mLocationListener);//자원해제
            deleteRTDB();
            updateUI(0, 0, 0, false);
            Toast.makeText(this, "finished", Toast.LENGTH_SHORT).show();
        }

    }

    private void startGPSService() {
        mLM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            mLM.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000, 2, mLocationListener);
        } catch (Exception e) {
            Toast.makeText(this, "위치 권한이 허용되지 않았습니다.", Toast.LENGTH_SHORT).show();
            mLM.removeUpdates(mLocationListener);//자원해제
        }
    }

    private void uploadAtFirebase(double longitude, double latitude, float accuracy) {
        if (accuracy>=1.0f){
            //upload
            //TODO upload at RealTime DataBase
            uploadRTDB(true, longitude, latitude, 1, getStartTime);
        } else
            Toast.makeText(this, "위치 얻기를 재시도 중입니다", Toast.LENGTH_SHORT).show();
    }

    private void updateUI(double longitude, double latitude, float accuracy, boolean updateStatus) {
        binding.setAccuracy("신뢰도 : " + accuracy + "%");
        binding.setNowLoc(longitude + "," + latitude);
        binding.setNowUpdate(updateStatus ? "실행 중" : "비활성화");
    }

    private void dialogWhenOffLocationService() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("위치를 켜주세요.")
                .setCancelable(true)
                .setMessage("위치 서비스가 꺼져있습니다. 위치 서비스를 아래 버튼을 눌러 활성화해주세요.")
                .setPositiveButton("설정하러가기", (dialog, id) -> {
                    Intent callGPSSettingIntent
                            = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                }).setNegativeButton("취소", (dialog, id) -> dialog.cancel())
                .create()
                .show();
    }

    public boolean locationStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean checkPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                REQUIRED_PERMISSIONS[0]);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    REQUIRED_PERMISSIONS[1]);

        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED)
            return false;
        else
            return true;
    }

    public void uploadRTDB(boolean add, double longitude, double latitude, int check, long time){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            FirebasePost post = new FirebasePost(longitude, latitude, check, time);
            postValues = post.toMap();
        }
        childUpdates.put("/list/" + time, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    public void deleteRTDB(){
        mPostReference = FirebaseDatabase.getInstance().getReference("/list/"+getStartTime);
        mPostReference.removeValue();
    }
}