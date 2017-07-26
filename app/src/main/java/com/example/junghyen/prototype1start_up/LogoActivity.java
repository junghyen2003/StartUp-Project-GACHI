package com.example.junghyen.prototype1start_up;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count",0);
        intent.putExtra("badge_count_package_name", getApplicationContext().getPackageName());
        intent.putExtra("badge_count_class_name",LogoActivity.class.getName());
        sendBroadcast(intent);

        SharedPreferences badge = getSharedPreferences("badge", Activity.MODE_PRIVATE);
        SharedPreferences.Editor badge_editor = badge.edit();
        badge_editor.putInt("badge_count",0);
        badge_editor.commit();

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(LogoActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        }, 1000); // 1000ms
    }
}
