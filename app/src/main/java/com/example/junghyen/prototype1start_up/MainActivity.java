package com.example.junghyen.prototype1start_up;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TimerTask adTast;
    Timer update_timer = new Timer();

    RelativeLayout content_main; // content_main 레이아웃

    String user_id;
    String[] mysensor; // 사용자가 등록한 센서의 시리얼 넘버

    OkHttpClient client = new OkHttpClient();
    String return_value;

    ListView listView;
    ArrayList<ListItemActivity> list_item_activityArrayList;
    MyListAdapter myListAdapter;
    int[] image_num = {R.drawable.icon_big_bathroom,
            R.drawable.icon_big_entrance,
            R.drawable.icon_big_kitchen,
            R.drawable.icon_big_library,
            R.drawable.icon_big_livingroom,
            R.drawable.icon_big_woman};
    SwipeRefreshLayout mSwipeRefreshLayout;

    String nickname;

    TextView nav_user_id_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 장비 추가 페이지로 넘어감
                Intent i = new Intent(MainActivity.this, AddSensorActivity.class);
                // 사용자 id 정보 파라미터로 넘어감
                i.putExtra("user_id",user_id);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 인텐트로 전달 받은 user_id 정보 초기화
        Intent i = getIntent();
        user_id = i.getStringExtra("user_id");

        // 푸쉬 관련
        FirebaseMessaging.getInstance();
        try {
            token_update(user_id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // drawer textView 변경 = user_id
        View header = navigationView.getHeaderView(0);
        nav_user_id_textView = (TextView)header.findViewById(R.id.nav_user_id_textView);
        nav_user_id_textView.setText(user_id);

        try {
            // mysensor[1]부터 사용자가 가지고 있는 장비의 시리얼 넘버가 담김
            load_my_sensor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 레이아웃 연결
        content_main = (RelativeLayout)findViewById(R.id.content_main);

        listView = (ListView)findViewById(R.id.listView);
        list_item_activityArrayList = new ArrayList<ListItemActivity>();
        myListAdapter = new MyListAdapter(MainActivity.this, list_item_activityArrayList);
        listView.setAdapter(myListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nickname = list_item_activityArrayList.get(position).getNickname();

                Intent intent = new Intent(getApplicationContext(),SensorInfoActivity.class);
                intent.putExtra("serial_num",mysensor[position+1]);
                intent.putExtra("nickname",nickname);
                startActivity(intent);
            }
        });

        try {
            // 서버로부터 정보를 가져와 리스트 뷰에 뿌려줌
            refresh();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 아래로 당겨서 새로고침 SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                try {
                    load_my_sensor();
                    refresh();
                    mSwipeRefreshLayout.setRefreshing(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRestart(){
        super.onRestart();
        try {
            load_my_sensor();
            refresh();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        adTast = new TimerTask(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            load_my_sensor();
                            refresh();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.main_user_update){
            item.setChecked(true);
            update_timer.cancel();
            update_timer = new Timer();
            return true;
        }
        else if(id == R.id.main_five_sec_update){
            item.setChecked(true);
            update_timer.cancel();
            update_timer = new Timer();
            update_timer.schedule(adTast,0,5000);
            return true;
        }
        else if(id == R.id.main_fifteen_sec_update){
            item.setChecked(true);
            update_timer.cancel();
            update_timer = new Timer();
            update_timer.schedule(adTast,0,15000);
            return true;
        }
        else if(id == R.id.main_thirty_sec_update){
            item.setChecked(true);
            update_timer.cancel();
            update_timer = new Timer();
            update_timer.schedule(adTast,0,30000);
            return true;
        }
        else if(id == R.id.main_sixty_sec_update){
            item.setChecked(true);
            update_timer.cancel();
            update_timer = new Timer();
            update_timer.schedule(adTast,0,60000);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            // 로그아웃 액션
            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = auto.edit();
            //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
            editor.clear();
            editor.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // 사용자의 ID를 가지고 사용자가 등록되어있는 장치들의 시리얼 번호를 불러옴
    private void load_my_sensor() throws InterruptedException {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String address = "http://211.225.79.43:8080";
                String my_sensor = "/sensor_info/my_sensor?user_id=" + user_id;
                address += my_sensor;

                Request request = new Request.Builder() .url(address) .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    return_value = response.body().string();
                    mysensor = return_value.split(",");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        t.join();
    }

    // 사용자가 가지고 있는 장치의 시리얼 넘버를 가지고 아이콘넘버, 별명, 현재인원, 최근 입출시간을 가져옴
    // 리스트뷰에 세팅
    public void refresh() throws InterruptedException{
        // 새로 고침시 리스트를 비움
        list_item_activityArrayList.clear();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < mysensor.length - 1; i++){
                    String address = "http://211.225.79.43:8080";
                    String my_sensor = "/sensor_info/my_serial_sensor?serial_num=" + mysensor[i+1];
                    address += my_sensor;

                    Request request = new Request.Builder() .url(address) .build();
                    Response response = null;
                    try {
                        response = client.newCall(request).execute();
                        return_value = response.body().string();
                        if(!return_value.equals("null")) {
                            String[] str = new String(return_value).split(",");
                            int icon_num = Integer.parseInt(str[0]);
                            String nickname = str[1];
                            String left_person = str[2];
                            String recent_time = str[3];

                            list_item_activityArrayList.add(new ListItemActivity(image_num[icon_num],nickname,left_person,recent_time));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
        t.join();

        myListAdapter.notifyDataSetChanged();
    }

    // 토큰 업데이트
    public void token_update(final String user_id) throws InterruptedException{
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                MyFirebaseInstanceIDService t = new MyFirebaseInstanceIDService(user_id);
                t.onTokenRefresh();
            }
        });
        t.start();
        t.join();
    }
}
