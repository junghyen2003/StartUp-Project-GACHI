package com.example.junghyen.prototype1start_up;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SensorInfoActivity extends ActionBarActivity {
    OkHttpClient client = new OkHttpClient();
    String return_value;

    String serial_num, nickname, left_person, inTime, outTime, inPerson, outPerson;
    String rx, tx;
    ImageView sensor_info_imageView;
    TextView sensor_info_nickname_textView;
    TextView sensor_info_left_person_textView;
    TextView sensor_info_recent_inTime_textView;
    TextView sensor_info_recent_outTime_textView;
    TextView sensor_info_inPerson_textView;
    TextView sensor_info_outPerson_textView;

    TextView rx_battery_textView;
    TextView tx_battery_textView;

    ImageView rx_battery_imageView;
    ImageView tx_battery_imageView;

    ListView listView;
    ArrayList<SensorInfoListItemActivity> sensor_info_list_item_activityArrayList;
    SensorInfoListAdapter sensor_info_ListAdapter;
    SwipeRefreshLayout sensor_info_mSwipeRefreshLayout;

    Button sensor_info_button;

    TimerTask adTast;
    Timer update_timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_info);

        // 인텐트로 전달 받은 serial_num, nickname 셋팅
        Intent i = getIntent();
        serial_num = i.getStringExtra("serial_num");
        nickname = i.getStringExtra("nickname");

        // 뒤로가기 버튼 사용
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 컴포넌트 초기화
        init();

        // 별명 셋팅
        sensor_info_nickname_textView.setText(nickname);

        // 버튼 셋팅
        sensor_info_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SensorInfoActivity.this, SensorStatisticsActivity.class);
                // 사용자 id 정보 파라미터로 넘어감
                i.putExtra("serial_num",serial_num);
                i.putExtra("nickname",nickname);
                startActivity(i);
            }
        });

        listView = (ListView)findViewById(R.id.sensor_info_listView);
        sensor_info_list_item_activityArrayList = new ArrayList<SensorInfoListItemActivity>();
        sensor_info_ListAdapter = new SensorInfoListAdapter(SensorInfoActivity.this, sensor_info_list_item_activityArrayList);
        listView.setAdapter(sensor_info_ListAdapter);

        try {
            // 시리얼 넘버를 파라미터로 서버에 남은인원, 최신 in/out 시간, in/out 총 인원 을 불러옴(첫번째 실행)
            info_refresh();
            sensor_info_left_person_textView.setText(left_person);
            sensor_info_recent_inTime_textView.setText(inTime);
            sensor_info_recent_outTime_textView.setText(outTime);
            sensor_info_inPerson_textView.setText(inPerson);
            sensor_info_outPerson_textView.setText(outPerson);

            // 시리얼 넘버와 최근 입실 날짜를 파라미터로 서버에서 입/퇴실, 시간, 입실누적, 퇴실누적을 불러옴
            more_info_refresh();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 남은인원 텍스트 클릭 시 새로고침 및 이미지 뷰 회전
        sensor_info_left_person_textView.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // 시리얼 넘버를 파라미터로 서버에 남은인원, 최신 in/out 시간, in/out 총 인원 을 불러옴(새로고침 시)
                    info_refresh();
                    if(rx.equals("0")){
                        // 배터리 이상 없음
                        rx_battery_textView.setVisibility(View.GONE);
                        rx_battery_imageView.setVisibility(View.GONE);
                    }else if(rx.equals("1")){
                        rx_battery_textView.setVisibility(View.VISIBLE);
                        rx_battery_imageView.setVisibility(View.VISIBLE);
                    }
                    // tx 배터리 상태
                    if(tx.equals("0")) {
                        // 배터리 이상 없음
                        tx_battery_textView.setVisibility(View.GONE);
                        tx_battery_imageView.setVisibility(View.GONE);
                    }else if(tx.equals("1")){
                        tx_battery_textView.setVisibility(View.VISIBLE);
                        tx_battery_imageView.setVisibility(View.VISIBLE);
                    }
                    // 시리얼 넘버와 최근 입실 날짜를 파라미터로 서버에서 입/퇴실, 시간, 입실누적, 퇴실누적을 불러옴(새로고침 시)
                    more_info_refresh();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sensor_info_left_person_textView.setText(left_person);
                sensor_info_recent_inTime_textView.setText(inTime);
                sensor_info_recent_outTime_textView.setText(outTime);
                sensor_info_inPerson_textView.setText(inPerson);
                sensor_info_outPerson_textView.setText(outPerson);

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anim);
                sensor_info_imageView.startAnimation(animation);
            }
        });

        // 아래로 당겨서 새로고침 SwipeRefreshLayout
        sensor_info_mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.sensor_info_mSwipeRefreshLayout);
        sensor_info_mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                try {
                    // 시리얼 넘버를 파라미터로 서버에 남은인원, 최신 in/out 시간, in/out 총 인원 을 불러옴(새로고침 시)
                    info_refresh();
                    if(rx.equals("0")){
                        // 배터리 이상 없음
                        rx_battery_textView.setVisibility(View.GONE);
                        rx_battery_imageView.setVisibility(View.GONE);
                    }else if(rx.equals("1")){
                        rx_battery_textView.setVisibility(View.VISIBLE);
                        rx_battery_imageView.setVisibility(View.VISIBLE);
                    }
                    // tx 배터리 상태
                    if(tx.equals("0")) {
                        // 배터리 이상 없음
                        tx_battery_textView.setVisibility(View.GONE);
                        tx_battery_imageView.setVisibility(View.GONE);
                    }else if(tx.equals("1")){
                        tx_battery_textView.setVisibility(View.VISIBLE);
                        tx_battery_imageView.setVisibility(View.VISIBLE);
                    }
                    // 시리얼 넘버와 최근 입실 날짜를 파라미터로 서버에서 입/퇴실, 시간, 입실누적, 퇴실누적을 불러옴(새로고침 시)
                    more_info_refresh();
                    sensor_info_left_person_textView.setText(left_person);
                    sensor_info_recent_inTime_textView.setText(inTime);
                    sensor_info_recent_outTime_textView.setText(outTime);
                    sensor_info_inPerson_textView.setText(inPerson);
                    sensor_info_outPerson_textView.setText(outPerson);

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anim);
                    sensor_info_imageView.startAnimation(animation);

                    sensor_info_mSwipeRefreshLayout.setRefreshing(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // 배터리 교체 후 사용자 클릭 이벤트
        rx_battery_imageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                rx_battery_textView.setVisibility(View.GONE);
                rx_battery_imageView.setVisibility(View.GONE);
                try {
                    rx_battery_update();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        tx_battery_imageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                tx_battery_textView.setVisibility(View.GONE);
                tx_battery_imageView.setVisibility(View.GONE);
                try {
                    tx_battery_update();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 컴포넌트 초기화
    public void init(){
        sensor_info_imageView = (ImageView)findViewById(R.id.sensor_info_imageView);
        sensor_info_nickname_textView = (TextView)findViewById(R.id.sensor_info_nickname_textView);
        sensor_info_left_person_textView = (TextView)findViewById(R.id.sensor_info_left_person_textView);
        sensor_info_recent_inTime_textView = (TextView)findViewById(R.id.sensor_info_recent_inTime_textView);
        sensor_info_recent_outTime_textView = (TextView)findViewById(R.id.sensor_info_recent_outTime_textView);
        sensor_info_inPerson_textView = (TextView)findViewById(R.id.sensor_info_inPerson_textView);
        sensor_info_outPerson_textView = (TextView)findViewById(R.id.sensor_info_outPerson_textView);
        sensor_info_mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.sensor_info_mSwipeRefreshLayout);
        sensor_info_button = (Button)findViewById(R.id.sensor_info_button);
        rx_battery_imageView = (ImageView)findViewById(R.id.rx_battery_imageView);
        tx_battery_imageView = (ImageView)findViewById(R.id.tx_battery_imageView);
        rx_battery_textView = (TextView)findViewById(R.id.rx_battery_textView);
        tx_battery_textView = (TextView)findViewById(R.id.tx_battery_textView);

        // 텍스트뷰 감춤
        rx_battery_textView.setVisibility(View.GONE);
        tx_battery_textView.setVisibility(View.GONE);

        // 이미지뷰 감춤
        rx_battery_imageView.setVisibility(View.GONE);
        tx_battery_imageView.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        adTast = new TimerTask(){
            @Override
            public void run() {
                try {
                    info_refresh();
                    if(rx.equals("0")){
                        // 배터리 이상 없음
                        rx_battery_textView.setVisibility(View.GONE);
                        rx_battery_imageView.setVisibility(View.GONE);
                    }else if(rx.equals("1")){
                        rx_battery_textView.setVisibility(View.VISIBLE);
                        rx_battery_imageView.setVisibility(View.VISIBLE);
                    }
                    // tx 배터리 상태
                    if(tx.equals("0")) {
                        // 배터리 이상 없음
                        tx_battery_textView.setVisibility(View.GONE);
                        tx_battery_imageView.setVisibility(View.GONE);
                    }else if(tx.equals("1")){
                        tx_battery_textView.setVisibility(View.VISIBLE);
                        tx_battery_imageView.setVisibility(View.VISIBLE);
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                more_info_refresh();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            sensor_info_left_person_textView.setText(left_person);
                            sensor_info_recent_inTime_textView.setText(inTime);
                            sensor_info_recent_outTime_textView.setText(outTime);
                            sensor_info_inPerson_textView.setText(inPerson);
                            sensor_info_outPerson_textView.setText(outPerson);

                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anim);
                            sensor_info_imageView.startAnimation(animation);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        int id = item.getItemId();
        // 뒤로가기 버튼이 눌렸을 때
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        else if (id == R.id.action_trash) {
            // 장비삭제 이벤트
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("장치 삭제");
                alertDialogBuilder
                        .setMessage("장치를 삭제하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        try {
                                            sensor_delete();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if(return_value.equals("true")){
                                            Toast.makeText(getApplicationContext(), "장치가 정상적으로 삭제되었습니다",Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                            onBackPressed();
                                        }
                                        else if (return_value.equals("false")){
                                            Toast.makeText(getApplicationContext(), "장치가 삭제되지 않았습니다.",Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                        }
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener(){
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // 다이얼로그 생성
                AlertDialog alertDialog = alertDialogBuilder.create();
                // 다이얼로그 보여주기
                alertDialog.show();
            //
            return true;
        }
        else if(id == R.id.user_update){
            item.setChecked(true);
            update_timer.cancel();
            update_timer = new Timer();
            return true;
        }
        else if(id == R.id.five_sec_update){
            item.setChecked(true);
            update_timer.cancel();
            update_timer = new Timer();
            update_timer.schedule(adTast,0,5000);
            return true;
        }
        else if(id == R.id.fifteen_sec_update){
            item.setChecked(true);
            update_timer.cancel();
            update_timer = new Timer();
            update_timer.schedule(adTast,0,15000);
            return true;
        }
        else if(id == R.id.thirty_sec_update){
            item.setChecked(true);
            update_timer.cancel();
            update_timer = new Timer();
            update_timer.schedule(adTast,0,30000);
            return true;
        }
        else if(id == R.id.sixty_sec_update){
            item.setChecked(true);
            update_timer.cancel();
            update_timer = new Timer();
            update_timer.schedule(adTast,0,60000);
            return true;
        }
        else if(id == R.id.push_settings){
            Intent i = new Intent(SensorInfoActivity.this, PushSettingActivity.class);
            // 사용자 id 정보 파라미터로 넘어감
            i.putExtra("serial_num",serial_num);
            i.putExtra("nickname",nickname);
            startActivity(i);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return true;
    }

    // 시리얼 넘버를 파라미터로 서버에 남은인원, 최신 in/out 시간, in/out 총 인원 을 불러옴
    public void info_refresh() throws InterruptedException{
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String address = "http://211.225.79.43:8080";
                String my_sensor = "/sensor_info/get_sensor_info?serial_num=" + serial_num;
                address += my_sensor;

                Request request = new Request.Builder() .url(address) .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    return_value = response.body().string();
                    if(!return_value.equals("null")) {
                        String[] str = new String(return_value).split(",");
                        left_person = str[0];
                        // rx 배터리 상태
                        rx = str[1];
                        tx = str[2];
                        inTime = str[3];
                        outTime = str[4];
                        inPerson = str[5];
                        outPerson = str[6];
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        t.join();
    }

    // 시리얼 넘버와 최근 입실 날짜를 파라미터로 서버에서 입/퇴실, 시간, 입실누적, 퇴실누적을 불러옴
    public void more_info_refresh() throws InterruptedException{
        // 새로 고침시 리스트를 비움
        sensor_info_list_item_activityArrayList.clear();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String incount_f = "0";
                String outcount_f = "0";
                String incount = "0";
                String outcount = "0";

                String[] inDay = inTime.split("\\s");
                String address = "http://211.225.79.43:8080";
                String my_sensor = "/sensor_info/get_sensor_more_info?serial_num=" + serial_num + "&inDay=" + inDay[0];
                address += my_sensor;

                Request request = new Request.Builder() .url(address) .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    return_value = response.body().string();
                    if(!return_value.equals("null")) {
                        String[] str = new String(return_value).split("/");
                        for(int i = 1; i < str.length; i++){
                            String [] test = str[i].split(",");
                            String check = test[0];
                            String time = test[1];
                            if(test[2].equals("-")){
                                incount = test[2];
                            }
                            else{
                                incount_f = test[2];
                                incount = test[2];
                            }
                            if(test[3].equals("-")) {
                                outcount = test[3];
                            }
                            else{
                                outcount_f = test[3];
                                outcount = test[3];
                            }
                            String left_person = String.valueOf(Integer.valueOf(incount_f) - Integer.valueOf(outcount_f));
                            sensor_info_list_item_activityArrayList.add(0, new SensorInfoListItemActivity(check,time,incount,outcount,left_person));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        t.join();
        sensor_info_ListAdapter.notifyDataSetChanged();
    }

    public void sensor_delete() throws InterruptedException{
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String address = "http://211.225.79.43:8080";
                String my_sensor = "/sensor_info/refresh_sensor?serial_num=" + serial_num;
                address += my_sensor;

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

    public void  rx_battery_update() throws InterruptedException{
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String address = "http://211.225.79.43:8080";
                String my_sensor = "/sensor_info/rx_battery_status?serial_num=" + serial_num + "rx_status=0";
                address += my_sensor;

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

    public void  tx_battery_update() throws InterruptedException{
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String address = "http://211.225.79.43:8080";
                String my_sensor = "/sensor_info/tx_battery_status?serial_num=" + serial_num + "tx_status=0";
                address += my_sensor;

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
