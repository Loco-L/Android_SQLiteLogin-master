package com.example.syydnrycx.sqlitelogin.Activity;

import java.util.*;
import android.os.Bundle;
import android.widget.*;

import com.example.syydnrycx.sqlitelogin.Class.UserInfo;
import com.example.syydnrycx.sqlitelogin.R;
import android.app.Activity;
import android.view.View;
import android.view.View.*;
import android.text.method.ScrollingMovementMethod;
import android.content.Intent;


public class MainPage extends Activity {
    private Spinner sp_2;
    private Button button;
    private String[] subject = {"", "软件管理与经济(黄杰)", "软件经济(黄杰)", "软件工程(黄杰)"};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        //获取用户信息
        UserInfo user=(UserInfo)getApplication();
        //String userName=((UserInfo)getApplicationContext()).getname();
        TextView name= findViewById(R.id.textView4);
        name.setText(user.getname());

        // 下拉框
        sp_2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<String>(this, R.layout.subject_list, subject);
        sp_2.setPrompt("Please select a subject:");
        sp_2.setAdapter(subjectAdapter);
        sp_2.setSelection(0);

        //获取按钮
        button = (Button) findViewById(R.id.button);

        //按钮进行监听
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //监听按钮，如果点击，就跳转
                Intent intent = new Intent();
                //前一个（MainActivity.this）是目前页面，后面一个是要跳转的下一个页面
                intent.setClass(MainPage.this,SubjectPage.class);
                startActivity(intent);
            }
        });
    }

}
