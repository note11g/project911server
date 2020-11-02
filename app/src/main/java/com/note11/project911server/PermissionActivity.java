package com.note11.project911server;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

public class PermissionActivity extends AppCompatActivity {

    private final int PERMISSION_REQUEST_CODE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);


    }


    private void showDialog(String titleText, String messageText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(titleText)
                .setMessage(messageText)
                .setPositiveButton("확인", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }).show();
    }

    private void requestPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"));
        try {
            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            showDialog("에러", "권한 설정 중 에러가 발생했습니다.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

    }
}