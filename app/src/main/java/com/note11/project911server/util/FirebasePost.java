package com.note11.project911server.util;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class FirebasePost {

    private double longitude;
    private double latitude;
    private int check;
    private long time;

    public FirebasePost(){
    }

    public FirebasePost(double longitude, double latitude, int check, long time){
        this.longitude = longitude;
        this.latitude = latitude;
        this.check = check;
        this.time = time;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        result.put("check", check);
        result.put("id", time);
        return  result;
    }

}
