package com.note11.project911server;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.note11.project911server.databinding.ActivityGetPermissionBinding;

public class getPermissionActivity extends AppCompatActivity {


    private static final int PERMISSIONS_REQUEST_CODE = 4000;
    private final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };
    private ActivityGetPermissionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_permission);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_get_permission);

        binding.btnGoSetting.setOnClickListener(v -> requestPermission());
    }


    private void requestPermission() {
       ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
    }

    private void onSucceedAndGo() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (!check_result) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(this, "권한이 거부되었습니다. 허용해야 어플 사용이 가능합니다.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "권한이 거부되었습니다.\n설정 화면에서 권한-위치를 항상 사용으로 설정해주세요.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.fromParts("package", getPackageName(), null)));
                    finish();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                        Toast.makeText(this, "백그라운드 위치 권한이 거부되었습니다. 허용해야 어플 사용이 가능합니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }
            } else {
                onSucceedAndGo();
            }

        }
    }
}