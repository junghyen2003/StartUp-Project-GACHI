package com.example.junghyen.prototype1start_up;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by jungh on 2017-03-08.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    public static String user_id;

    public MyFirebaseInstanceIDService(){
    }

    public MyFirebaseInstanceIDService(String user_id){
        this.user_id = user_id;
    }

    @Override
    public void onTokenRefresh(){
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "Refreshed token: " + token);

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(final String token){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String address = "http://211.225.79.43:8080";
                String my_sensor = "/sensor_info/token_update?user_id="+user_id+"&token="+token;
                address += my_sensor;

                Request request = new Request.Builder() .url(address) .build();
                try {
                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
