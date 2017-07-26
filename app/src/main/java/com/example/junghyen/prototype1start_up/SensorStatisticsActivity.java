package com.example.junghyen.prototype1start_up;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SensorStatisticsActivity extends ActionBarActivity {
    OkHttpClient client = new OkHttpClient();
    String return_value;

    String date_f;
    String[] year_list = {"2016","2017","2018","2019","2020"};
    String[] month_list = {"1","2","3","4","5","6","7","8","9","10","11","12"};
    String[] day_list = {"1","2","3","4","5","6","7","8","9","10",
            "11","12","13","14","15","16","17","18","19","20",
            "21","22","23","24","25","26","27","28","29","30","31"};

    String serial_num;
    String nickname;

    TextView nickname_textView;
    TextView date_textView;

    TextView sum_incount;
    TextView sum_outcount;
    int sum_in = 0, sum_out = 0;

    // 라디오 그룹
    RadioGroup rg;
    int id;
    // 라디오 버튼
    RadioButton rb;

    // 스피너 년/월/일
    Spinner year;
    Spinner month;
    Spinner day;

    // 조회 버튼
    Button search;

    ListView sensor_statistics_listView;
    ArrayList<SensorStatisticsListItemActivity> sensor_statistics_list_item_activityArrayList;
    SensorStatisticsListAdapter sensor_statistics_ListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_statistics);

        Intent i = getIntent();
        serial_num = i.getStringExtra("serial_num");
        nickname = i.getStringExtra("nickname");

        init();
        sum_incount.setText("0");
        sum_outcount.setText("0");

        sensor_statistics_list_item_activityArrayList = new ArrayList<SensorStatisticsListItemActivity>();
        sensor_statistics_ListAdapter = new SensorStatisticsListAdapter(SensorStatisticsActivity.this, sensor_statistics_list_item_activityArrayList);
        sensor_statistics_listView.setAdapter(sensor_statistics_ListAdapter);

        // 뒤로가기 버튼 사용
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nickname_textView.setText(nickname);

        // 현재 선택된 라디오 버튼 값 : 기본은 일 버튼
        id = rg.getCheckedRadioButtonId();
        rb = (RadioButton)findViewById(id);
        date_textView.setText("시간");

        // 년 월 일 라디오 버튼 선택 시
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                rb = (RadioButton)findViewById(i);
                switch (i) {
                    case R.id.radio0 :
                        date_textView.setText("월");
                        year.setEnabled(true);
                        month.setEnabled(false);
                        day.setEnabled(false);
                        break;
                    case R.id.radio1 :
                        date_textView.setText("일");
                        year.setEnabled(true);
                        month.setEnabled(true);
                        day.setEnabled(false);
                        break;
                    case R.id.radio2 :
                        date_textView.setText("시간");
                        year.setEnabled(true);
                        month.setEnabled(true);
                        day.setEnabled(true);
                        break;
                }
            }
        });

        // 스피너 오늘 날짜로 초기화 2017 02 24
        init_date_spinner();

        // 조회 버튼에 일 월 년 검색 이벤트 등록
        search.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                id = rg.getCheckedRadioButtonId();
                try {
                    switch (id) {
                        // 년 검색 이벤트
                        case R.id.radio0 :
                            year_search();
                            break;

                        // 월 검색 이벤트
                        case R.id.radio1 :
                            month_search();
                            break;

                        // 일 검색 이벤트
                        case R.id.radio2 :
                            day_search();
                            break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void init(){
        nickname_textView = (TextView)findViewById(R.id.sensor_statistics_nickname_textView);
        date_textView = (TextView)findViewById(R.id.date_textView);
        rg = (RadioGroup)findViewById(R.id.statistics_radioGroup);
        year = (Spinner)findViewById(R.id.statistics_year_spinner);
        month = (Spinner)findViewById(R.id.statistics_month_spinner);
        day = (Spinner)findViewById(R.id.statistics_day_spinner);
        search = (Button)findViewById(R.id.statistics_search);
        sensor_statistics_listView = (ListView)findViewById(R.id.sensor_statistics_listView);
        sum_incount = (TextView)findViewById(R.id.sum_incount);
        sum_outcount = (TextView)findViewById(R.id.sum_outcount);

        // 스피너 팝업창 높이 조절
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(year);
            popupWindow.setHeight(500);
            popupWindow = (android.widget.ListPopupWindow) popup.get(month);
            popupWindow.setHeight(500);
            popupWindow = (android.widget.ListPopupWindow) popup.get(day);
            popupWindow.setHeight(500);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
        }
    }

    public void init_date_spinner(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat CurYearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat CurMonthFormat = new SimpleDateFormat("M");
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("d");

        // 현재 년도, 월, 일 구함
        String strCurYear = CurYearFormat.format(date); // 년도
        String strCurMonth = CurMonthFormat.format(date); // 월
        String strCurDay = CurDayFormat.format(date); // 일

        int yearPosition = 0;
        int monthPosition = 0;
        int dayPosition = 0;

        for(int i = 0; i < year_list.length; i++){
            if(year_list[i].contains(strCurYear)){
                yearPosition = i;
                break;
            }
        }
        for(int i = 0; i < month_list.length; i++){
            if(month_list[i].contains(strCurMonth)){
                monthPosition = i;
                break;
            }
        }
        for(int i = 0; i < day_list.length; i++){
            if(day_list[i].contains(strCurDay)){
                dayPosition = i;
                break;
            }
        }

        year.setSelection(yearPosition);
        month.setSelection(monthPosition);
        day.setSelection(dayPosition);
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

    public void day_search() throws InterruptedException{
        // 새로 고침시 리스트를 비움
        sensor_statistics_list_item_activityArrayList.clear();
        String year_f = year.getSelectedItem().toString();
        String month_f = month.getSelectedItem().toString();
        String day_f = day.getSelectedItem().toString();

        sum_in = 0;
        sum_out = 0;

        if(month_f.length() == 1){
            month_f = "0" + month_f;
        }
        if(day_f.length() == 1){
            day_f = "0" + day_f;
        }

        date_f = year_f + "-" + month_f + "-" + day_f;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String address = "http://211.225.79.43:8080";
                String my_sensor = "/sensor_info/search_day?serial_num=" + serial_num + "&inDay=" + date_f;
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
                            String date = test[0];
                            String incount = test[1];
                            String outcount = test[2];

                            sum_in += Integer.valueOf(incount);
                            sum_out += Integer.valueOf(outcount);

                            sensor_statistics_list_item_activityArrayList.add(new SensorStatisticsListItemActivity(date,incount,outcount));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        t.join();

        sum_incount.setText(String.valueOf(sum_in));
        sum_outcount.setText(String.valueOf(sum_out));

        sensor_statistics_ListAdapter.notifyDataSetChanged();
    }

    public void month_search() throws InterruptedException{
        // 새로 고침시 리스트를 비움
        sensor_statistics_list_item_activityArrayList.clear();
        String year_f = year.getSelectedItem().toString();
        String month_f = month.getSelectedItem().toString();

        if(month_f.length() == 1) {
            month_f = "0" + month_f;
        }

        date_f = year_f + "-" + month_f;

        sum_in = 0;
        sum_out = 0;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String address = "http://211.225.79.43:8080";
                String my_sensor = "/sensor_info/search_month?serial_num=" + serial_num + "&inMonth=" + date_f;
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
                            String date = test[0];
                            String incount = test[1];
                            String outcount = test[2];

                            sum_in += Integer.valueOf(incount);
                            sum_out += Integer.valueOf(outcount);

                            sensor_statistics_list_item_activityArrayList.add(new SensorStatisticsListItemActivity(date,incount,outcount));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        t.join();

        sum_incount.setText(String.valueOf(sum_in));
        sum_outcount.setText(String.valueOf(sum_out));
        sensor_statistics_ListAdapter.notifyDataSetChanged();
    }

    public void year_search() throws InterruptedException{
        // 새로 고침시 리스트를 비움
        sensor_statistics_list_item_activityArrayList.clear();
        String year_f = year.getSelectedItem().toString();

        date_f = year_f;

        sum_in = 0;
        sum_out = 0;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String address = "http://211.225.79.43:8080";
                String my_sensor = "/sensor_info/search_year?serial_num=" + serial_num + "&inYear=" + date_f;
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
                            String date = test[0];
                            String incount = test[1];
                            String outcount = test[2];

                            sum_in += Integer.valueOf(incount);
                            sum_out += Integer.valueOf(outcount);

                            sensor_statistics_list_item_activityArrayList.add(new SensorStatisticsListItemActivity(date,incount,outcount));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        t.join();

        sum_incount.setText(String.valueOf(sum_in));
        sum_outcount.setText(String.valueOf(sum_out));
        sensor_statistics_ListAdapter.notifyDataSetChanged();
    }
}
