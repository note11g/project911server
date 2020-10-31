package com.note11.project911server;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.Toast;

import com.note11.project911server.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.btnUpdateStart.setOnClickListener(v->update(true));
        binding.btnUpdateFinish.setOnClickListener(v->update(false));

    }

    private void update(Boolean startORFinish){
        //Boolean startOrFinish : true==start, false==finish
        //using firebase realtime Database
        if(startORFinish){
            //start update service
            //service update cycle time : 5 ~ 10sec
            //위 내용이 작동하지 않다면, onLocationChanged function 사용


        }else{
            //finish update service

        }

    }

    private void getNowLocation(){
        //using GPS

    }

    private void uploadAtFirebase(){
        //made model and upload this function

    }
}