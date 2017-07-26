package com.example.junghyen.prototype1start_up;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PushSettingActivity extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    String return_value;

    String serial_num;
    String nickname;

    TextView push_update_nickname_textView;
    Switch push_update_real_check_switch;
    Switch push_update_not_check_switch;
    Switch push_update_many_check_switch;
    Switch push_update_not_out_check_switch;
    Button push_update_button;

    int real_check = 0;
    int not_check = 0;
    int many_check = 0;
    int not_out_check = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_setting);

        // 인텐트로 전달 받은 serial_num, nickname 셋팅
        Intent i = getIntent();
        serial_num = i.getStringExtra("serial_num");
        nickname = i.getStringExtra("nickname");

        // 뒤로가기 버튼 사용
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        push_update_nickname_textView.setText(nickname);

        try {
            load_push_status();
            switch_update();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        push_update_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    update_push_status();
                    Toast.makeText(getApplicationContext(),"설정이 업데이트 되었습니다",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void init(){
        push_update_nickname_textView = (TextView)findViewById(R.id.push_update_nickname_textView);
        push_update_real_check_switch = (Switch)findViewById(R.id.push_update_real_check_switch);
        push_update_not_check_switch = (Switch)findViewById(R.id.push_update_not_check_switch);
        push_update_many_check_switch = (Switch)findViewById(R.id.push_update_many_check_switch);
        push_update_not_out_check_switch = (Switch)findViewById(R.id.push_update_not_out_check_switch);
        push_update_button = (Button)findViewById(R.id.push_update_button);
    }

    public void switch_update(){
        if(real_check == 1) {
            push_update_real_check_switch.setChecked(true);
        }
        if(not_check == 1) {
            push_update_not_check_switch.setChecked(true);
        }
        if(many_check == 1) {
            push_update_many_check_switch.setChecked(true);
        }
        if(not_out_check == 1){
            push_update_not_out_check_switch.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // 뒤로가기 버튼이 눌렸을 때
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    public void load_push_status() throws InterruptedException{
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                    String address = "http://211.225.79.43:8080";
                    String my_sensor = "/sensor_info/load_push_status?serial_num=" + serial_num;
                    address += my_sensor;

                    Request request = new Request.Builder() .url(address) .build();
                    Response response = null;
                    try {
                        response = client.newCall(request).execute();
                        return_value = response.body().string();
                        if(!return_value.equals("null")) {
                            String[] str = new String(return_value).split(",");
                            real_check = Integer.parseInt(str[0]);
                            not_check = Integer.parseInt(str[1]);
                            many_check = Integer.parseInt(str[2]);
                            not_out_check = Integer.parseInt(str[3]);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        t.start();
        t.join();
    }

    public void update_push_status() throws InterruptedException{
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if(push_update_real_check_switch.isChecked()) {
                    real_check = 1;
                }else{
                    real_check = 0;
                }
                if(push_update_not_check_switch.isChecked()){
                    not_check = 1;
                }else{
                    not_check = 0;
                }
                if(push_update_many_check_switch.isChecked()){
                    many_check = 1;
                }
                else{
                    many_check = 0;
                }
                if(push_update_not_out_check_switch.isChecked()){
                    not_out_check = 1;
                }
                else{
                    not_out_check = 0;
                }
                String address = "http://211.225.79.43:8080";
                String my_sensor = "/sensor_info/update_push_status?"
                        + "serial_num=" + serial_num
                        + "&real_check=" + real_check
                        + "&not_check=" + not_check
                        + "&many_check=" + many_check
                        + "&not_out_check" + not_out_check;
                address += my_sensor;

                Request request = new Request.Builder() .url(address) .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        t.join();
    }

}
