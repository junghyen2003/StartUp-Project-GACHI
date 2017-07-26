package com.example.junghyen.prototype1start_up;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    Button login_button;

    TextView user_id_edit;
    TextView user_password_edit;
    TextView policy;

    CheckBox login_checkBox;

    OkHttpClient client = new OkHttpClient();
    String return_value;

    boolean auto_check;
    String loginId, loginPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        auto_check = auto.getBoolean("auto_check",false);
        loginId = auto.getString("inputId",null);
        loginPwd = auto.getString("inputPwd",null);

        if(auto_check == true){
            try {
                user_id_edit.setText(loginId);
                user_password_edit.setText(loginPwd);
                load_login_request();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 로그인 할 경우 MainActivity.class로 넘어감
            if (return_value.equals("true")) {
                Handler mHandler = new Handler();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("user_id", user_id_edit.getText().toString());
                        startActivity(i);
                        finish();
                    }
                });
            }
            // 로그인 하지 못할 경우
            else if (return_value.equals("false")) {
                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        // 로그인 버튼 동작
        login_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // 로그인 버튼 클릭시 return_vlaue에 값이 저장됨
                    load_login_request();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 로그인 할 경우 MainActivity.class로 넘어감
                if (return_value.equals("true")) {
                    SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor autoLogin = auto.edit();
                    // 자동 로그인 체크
                    if(login_checkBox.isChecked()) {
                        autoLogin.putBoolean("auto_check", true);
                    }
                    autoLogin.putString("inputId",user_id_edit.getText().toString());
                    autoLogin.putString("inputPwd",user_password_edit.getText().toString());
                    autoLogin.commit();
                    Handler mHandler = new Handler();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.putExtra("user_id", user_id_edit.getText().toString());
                            startActivity(i);
                            finish();
                        }
                    });
                }
                // 로그인 하지 못할 경우
                else if (return_value.equals("false")) {
                    Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 각종 컴포넌트 초기화
    private void init(){
        // 사용자 ID, Password
        user_id_edit = (TextView)findViewById(R.id.user_id_edit);
        user_password_edit = (TextView)findViewById(R.id.user_password_edit);

        // 로그인 버튼
        login_button = (Button)findViewById(R.id.login_button);

        // 약관 텍스트뷰 스크롤 허용
        policy = (TextView)findViewById(R.id.policy);
        policy.setMovementMethod(new ScrollingMovementMethod());

        login_checkBox = (CheckBox)findViewById(R.id.login_checkBox);
    }

    // 사용자 ID와 비밀번호를 입력받아 로그인 시도
    private void load_login_request() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String address = "http://211.225.79.43:8080";
                String id_password = "/user/login?user_id=" + user_id_edit.getText() + "&user_password=" + user_password_edit.getText();
                address += id_password;

                Request request = new Request.Builder() .url(address) .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    return_value = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        t.join();
    }

}
