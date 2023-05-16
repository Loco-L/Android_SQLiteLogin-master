package com.example.syydnrycx.sqlitelogin.Activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.syydnrycx.sqlitelogin.Class.UserInfo;
import com.example.syydnrycx.sqlitelogin.Class.UserService;
import com.example.syydnrycx.sqlitelogin.R;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class LoginActivity extends Activity implements View.OnClickListener{

    private ArrayList<String> usernamelList;

    private Button bt_login,bt_register,bt_getphonecore,bt_corelogin;
    private ImageButton image_btn;
    private EditText edit_username;
    private EditText edit_password;
    private EditText edit_phone;
    private EditText edit_cord;

    private UserService uService = null;
    private ListPopupWindow listPopupWindow;
    private String phone_number;
    private String cord_number;
    EventHandler eventHandler;
    private boolean coreflag=true;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logic_layout);

        initViews();
        sms_verification();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    private void initViews() {

        bt_login=(Button) findViewById(R.id.bt_login);
        bt_register=(Button) findViewById(R.id.bt_register);
        bt_getphonecore=(Button) findViewById(R.id.bt_getphonecore);
        bt_corelogin=(Button) findViewById(R.id.bt_corelogin);
        image_btn=(ImageButton)findViewById(R.id.user_btn_img);

        edit_username=(EditText) findViewById(R.id.user_name);
        edit_password=(EditText) findViewById(R.id.user_pass);
        edit_phone=(EditText)findViewById(R.id.ed_phone); //你的手机号
        edit_cord=(EditText)findViewById(R.id.ed_code);//你的验证码

        bt_login.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        bt_getphonecore.setOnClickListener(this);
        bt_corelogin.setOnClickListener(this);
        image_btn.setOnClickListener(this);

        uService = new UserService(LoginActivity.this);

        usernamelList = uService.getAll();

    }

    protected void onDestroy() {//销毁
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);

    }

    protected void onResume() {
        super.onResume();
        usernamelList.clear();      //从注册返回时清除usernamelList
        usernamelList = uService.getAll(); //更新注册的内容
    }


    private void showListPopulWindow() {

        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setAdapter(new ArrayAdapter<String>(this,R.layout.list_item, usernamelList));//用android内置布局，或设计自己的样式
        listPopupWindow.setAnchorView(edit_username);//以哪个控件为基准，在该处以mEditText为基准
        listPopupWindow.setModal(true);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置项点击监听
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                edit_username.setText(usernamelList.get(i));//把选择的选项内容展示在EditText上
                listPopupWindow.dismiss();//如果已经选择了，隐藏起来
            }

        });

        listPopupWindow.show();//把ListPopWindow展示出来
    }






    //按钮点击事件
    @Override
    public void onClick(View v) {
/*
        String phone_number=edit_phone.getText().toString();//1
        String cord_number=bt_getcord.getText().toString().trim();//1
*/

        switch (v.getId()){
            case R.id.bt_login://登录监听
                String name=edit_username.getText().toString();
                String pass=edit_password.getText().toString();
                boolean flag=uService.login(name, pass);
                if(flag) {

                    //前后端交互，给后端传递用户登录信息进行验证
                    //
                    //包装需要上传的Json数据
                    JSONObject params = new JSONObject();
                    try {
                        params.put("s_id", "1951606");
                        params.put("password", "12345");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    //Log.i("TAG",params.toString());

                    //给出路径
                    URL url = null;
                    try {
                        url = new URL("http://120.27.130.178:8080/student/login");
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                    //Log.i("TAG",url.toString());
                    //Log.i("TAG","开始连接");
                    //Log.i("TAG",params.toString());
                    String encode="utf-8";

                    try {//把请求的主体写入正文！！
                        //传输所需配置，勿动，直接Copy
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setConnectTimeout(3000);
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
                        connection.setDoInput(true);//表示从服务器获取数据
                        connection.setDoOutput(true);//表示向服务器写数据
                        connection.setUseCaches(false);//是否使用缓存
                        connection.getOutputStream().write(String.valueOf(params).getBytes());

                        //获得服务器响应的结果和状态码
                        String result="";
                        int responseCode = connection.getResponseCode();
                        if(responseCode == HttpURLConnection.HTTP_OK){
                            //获取服务器传来的数据（字符串），再转换为Json格式
                            result = changeInputeStream(connection.getInputStream(),encode);
                            Log.i("TAG",result);
                            JSONObject res = new JSONObject(result);
                            //调用UserInfo类
                            UserInfo user=(UserInfo)getApplication();
                            //读取Json中的对应数据
                            String username = res.getJSONObject("data").getString("name");
                            String userphone = res.getJSONObject("data").getString("phone");
                            int usertotal = res.getJSONObject("data").getInt("total");
                            //调用UserInfo中的赋值方法
                            user.setname(username);
                            user.setphone(userphone);
                            user.settotal(usertotal);

                            Log.i("TAG","连接成功");
                        }
                        Log.i("TAG","连接完毕");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        Log.i("TAG","连接失败1");
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Log.i("TAG","连接失败2");
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.i("TAG","连接失败3");
                        throw new RuntimeException(e);
                    }
                    //

                    Log.i("TAG","登录成功");
                    Intent intent=new Intent(LoginActivity.this,MainPage.class);
                    startActivity(intent);
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                }else{
                    Log.i("TAG","登录失败");
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.bt_register://注册监听
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_getphonecore://获取验证码的ID
                if(judPhone()){//去掉左右空格获取字符串，是正确的手机号
                    SMSSDK.getVerificationCode("86",phone_number);//获取你的手机号的验证码
                    edit_cord.requestFocus();//判断是否获得焦点
                }
                break;
            //  获取后要提交你的验证码以判断是否正确，并登陆成功
            case R.id.bt_corelogin://登陆页面的ID
                if(judCord()) {//判断验证码
                    SMSSDK.submitVerificationCode("86", phone_number, cord_number);//提交手机号和验证码
                    startActivity(new Intent(this,MainPage.class));
                }
                coreflag=false;
                break;
            case R.id.user_btn_img://编辑框下拉监听
                showListPopulWindow(); //调用显示PopuWindow 函数
                break;
        }
    }
    //前后端交互使用方法，用于转换输入数据类型为字符串
    private static String changeInputeStream(InputStream inputStream, String encode) {
        //通常叫做内存流，写在内存中的
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if(inputStream != null){
            try {
                while((len = inputStream.read(data))!=-1){
                    data.toString();

                    outputStream.write(data, 0, len);
                }
                //result是在服务器端设置的doPost函数中的
                result = new String(outputStream.toByteArray(),encode);
                outputStream.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }
    private boolean judPhone() {//判断手机号是否正确
        //不正确的情况
        if(TextUtils.isEmpty(edit_phone.getText().toString().trim()))//对于字符串处理Android为我们提供了一个简单实用的TextUtils类，如果处理比较简单的内容不用去思考正则表达式不妨试试这个在android.text.TextUtils的类，主要的功能如下:
        //是否为空字符 boolean android.text.TextUtils.isEmpty(CharSequence str)
        {
            Toast.makeText(LoginActivity.this,"请输入您的电话号码",Toast.LENGTH_LONG).show();
            edit_phone.requestFocus();//设置是否获得焦点。若有requestFocus()被调用时，后者优先处理。注意在表单中想设置某一个如EditText获取焦点，光设置这个是不行的，需要将这个EditText前面的focusable都设置为false才行。
            return false;
        }
        else if(edit_phone.getText().toString().trim().length()!=11){
            Toast.makeText(LoginActivity.this,"您的电话号码位数不正确",Toast.LENGTH_LONG).show();
            edit_phone.requestFocus();
            return false;
        }

        //正确的情况
        else{
            phone_number=edit_phone.getText().toString().trim();
            String num="[1][3578]\\d{9}";
            if(phone_number.matches(num)) {
                return true;
            }
            else{
                Toast.makeText(LoginActivity.this,"请输入正确的手机号码",Toast.LENGTH_LONG).show();
                return false;
            }
        }
    }


    private boolean judCord() {//判断验证码是否正确
        judPhone();//先执行验证手机号码正确与否
        if(TextUtils.isEmpty(edit_cord.getText().toString().trim())) {//验证码
            Toast.makeText(LoginActivity.this, "请输入您的验证码", Toast.LENGTH_LONG).show();
            edit_cord.requestFocus();//聚集焦点
            return false;
        }
        else if(edit_cord.getText().toString().trim().length()!=4){
            Toast.makeText(LoginActivity.this,"您的验证码位数不正确",Toast.LENGTH_LONG).show();
            edit_cord.requestFocus();
            return false;
        }
        else{
            cord_number=edit_cord.getText().toString().trim();
            return true;
        }
    }

    public void sms_verification(){
        //MobSDK.init(context, "28bc12fa236e4","44cb357655f252a8a75eac378b8283ad");
        eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg=new Message();//创建了一个对象
                msg.arg1=event;
                msg.arg2=result;
                msg.obj=data;
                handler.sendMessage(msg);
            }
        };

        SMSSDK.registerEventHandler(eventHandler);//注册短信回调（记得销毁，避免泄露内存）*/
    }

    /**
     * 使用Handler来分发Message对象到主线程中，处理事件
     */
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event=msg.arg1;
            int result=msg.arg2;
            Object data=msg.obj;
            if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//获取验证码成功
                if(result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    boolean smart = (Boolean)data;
                    if(smart) {
                        Toast.makeText(getApplicationContext(),"该手机号已经注册过，请重新输入",Toast.LENGTH_LONG).show();
                        edit_phone.requestFocus();//焦点
                        return;
                    }
                }
            }
            //回调完成
            if (result==SMSSDK.RESULT_COMPLETE){
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                    Toast.makeText(getApplicationContext(), "验证码输入正确",Toast.LENGTH_LONG).show();
                }
            }else {//其他出错情况
                if(coreflag){
                    bt_getphonecore.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"验证码获取失败请重新获取", Toast.LENGTH_LONG).show();
                    edit_phone.requestFocus();
                }
                else{
                    Toast.makeText(getApplicationContext(),"验证码输入错误", Toast.LENGTH_LONG).show();
                }

            }
        }

    };

}