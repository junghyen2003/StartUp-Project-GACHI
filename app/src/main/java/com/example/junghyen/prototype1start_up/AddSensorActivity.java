package com.example.junghyen.prototype1start_up;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddSensorActivity extends ActionBarActivity {
    final Context context = this;

    String user_id;
    String return_value;

    TextView serial_num_edit_text;
    TextView nickname_edit_text;

    Button add_sensor_button;

    OkHttpClient client = new OkHttpClient();

    Gallery gallery;

    // 왼쪽 오른쪽 선택 이미지
    ImageView arrow_left;
    ImageView arrow_right;

    Switch real_check, not_check, many_check, not_out_check;

    int select_icon_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_sensor);

        // 컴포넌트 초기화
        init();

        gallery = (Gallery)findViewById(R.id.gallery1);
        ImgsAdapter adapter = new ImgsAdapter(this);
        gallery.setAdapter(adapter);
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                select_icon_num = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        arrow_left.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select_icon_num!=0)
                    gallery.setSelection(select_icon_num-1);
            }
        });
        arrow_right.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select_icon_num!=gallery.getLastVisiblePosition())
                    gallery.setSelection(select_icon_num+1);
            }
        });

        // 장치 추가 버튼이 눌렸을 경우
        add_sensor_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    add_sensor();
                    // 장치의 시리얼 번호가 등록 되어 있지 않은 경우
                    if (return_value.equals("false")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("오류");
                        alertDialogBuilder
                                .setMessage("등록되어있지 않은 장치입니다.")
                                .setCancelable(false)
                                .setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                // 다이얼로그를 취소한다
                                                dialog.cancel();
                                            }
                                        });
                        // 다이얼로그 생성
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // 다이얼로그 보여주기
                        alertDialog.show();
                    }

                    // 장치의 사용자가 이미 존재하는 경우
                    else if (return_value.equals("overlap")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("오류");
                        alertDialogBuilder
                                .setMessage("이미 사용자가 존재하는 장치입니다.")
                                .setCancelable(false)
                                .setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                // 다이얼로그를 취소한다
                                                dialog.cancel();
                                            }
                                        });
                        // 다이얼로그 생성
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // 다이얼로그 보여주기
                        alertDialog.show();
                    }

                    // 장치 사용자 등록 완료
                    else if (return_value.equals("true")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setTitle("장치 추가 완료");
                        alertDialogBuilder
                                .setMessage("장치가 정상적으로 등록되었습니다.")
                                .setCancelable(false)
                                .setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                // 다이얼로그를 취소한다
                                                dialog.cancel();
                                                onBackPressed();
                                            }
                                        });
                        // 다이얼로그 생성
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // 다이얼로그 보여주기
                        alertDialog.show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // 뒤로가기 버튼 사용
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // 각종 컴포넌트 초기화
    public void init() {
        // 인텐트로 전달받은 user_id 초기화
        Intent i = getIntent();
        user_id = i.getStringExtra("user_id");

        // 버튼 초기화
        add_sensor_button = (Button) findViewById(R.id.add_sensor_button);

        // 텍스트 초기화
        serial_num_edit_text = (TextView) findViewById(R.id.serial_num_edit_text);
        nickname_edit_text = (TextView) findViewById(R.id.nickname_edit_text);

        arrow_left = (ImageView)findViewById(R.id.arrow_left_imageView);
        arrow_right = (ImageView)findViewById(R.id.arrow_right_imageView);

        // 스위치 초기화
        real_check = (Switch)findViewById(R.id.real_check_switch);
        not_check = (Switch)findViewById(R.id.not_check_switch);
        many_check = (Switch)findViewById(R.id.many_check_switch);
        not_out_check = (Switch)findViewById(R.id.not_out_check_switch);
    }

    private void add_sensor() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int real_check_status = 0;
                int not_check_status = 0;
                int many_check_status = 0;
                int not_out_check_stauts = 0;

                if(real_check.isChecked())
                {
                    real_check_status = 1;
                }
                if(not_check.isChecked())
                {
                    not_check_status = 1;
                }
                if(many_check.isChecked())
                {
                    many_check_status = 1;
                }
                if(not_out_check.isChecked())
                {
                    not_out_check_stauts = 1;
                }

                String address = "http://211.225.79.43:8080";
                String sensor_update_user = "/sensor_info/sensor_update_user?serial_num=" + serial_num_edit_text.getText()
                        + "&user_id=" + user_id
                        + "&nickname=" + nickname_edit_text.getText()
                        + "&icon_num=" + select_icon_num
                        + "&real_check=" + real_check_status
                        + "&not_check=" + not_check_status
                        + "&many_check=" + many_check_status
                        + "&not_out_check=" + not_check_status;
                address += sensor_update_user;

                Request request = new Request.Builder().url(address).build();
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
}
class ImgsAdapter extends BaseAdapter {
    Context context;

    //이미지를 가져옴, res<drawable 에 이미지를 넣어줌
    int[] imgs = {
            R.drawable.icon_big_bathroom,
            R.drawable.icon_big_entrance,
            R.drawable.icon_big_kitchen,
            R.drawable.icon_big_library,
            R.drawable.icon_big_livingroom,
            R.drawable.icon_big_woman
    };

    //외부에서 받지 않기 때문에 내부에서 만들어야함.
    //mainactivity에 생기는 view
    public ImgsAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return imgs.length;
    }

    //전체 아이템 리턴
    @Override
    public Object getItem(int position) {
        return imgs[position];
    }

    //id값을 보내줌
    @Override
    public long getItemId(int position) {
        return imgs[position];
    }

    //생성자가 실행될 때 자원의 갯수만큼 만들어짐
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageview;
        //생성되고 사라지는 view를 if문으로 재사용 한다!
        if(convertView == null){
            imageview = new ImageView(context);
        }else{
            imageview = (ImageView)convertView;
        }
        imageview.setPadding(80,0,80,0);
        imageview.setImageResource(imgs[position]);
        //리턴 할때 마다 새로운 이미지로 보임
        return imageview;
    }
}
