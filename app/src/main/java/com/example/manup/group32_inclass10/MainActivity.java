package com.example.manup.group32_inclass10;

// Priyanka Manusanipally - 801017222
// Sai Spandana Nakkireddy - 801023658

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.manup.group32_inclass10.SignupActivity.ALIST;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etemail, etpassword;
    Button btnlogin, btnsignup;
    SharedPreferences sharedPreferences;
    public static final String MYPREFNAME = "pref";
    String error;
    TokenResponse tokenResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(MYPREFNAME,MODE_PRIVATE);
        SharedPreferences s= getSharedPreferences(MYPREFNAME,MODE_PRIVATE);
        if(s.getAll().size()==0)
        {
           Log.d("there","fgfg");
            setContentView(R.layout.activity_main);
            setTitle("Chat Room");
            etemail= (EditText) findViewById(R.id.etemail);
            etpassword= (EditText) findViewById(R.id.etpassword);
            btnlogin= (Button) findViewById(R.id.btnlogin);
            btnsignup= (Button) findViewById(R.id.btnsignup);
            btnlogin.setOnClickListener(this);
            btnsignup.setOnClickListener(this);

        }
        else
        {
            Log.d("loop ","entered share");
            String em= keyToken("email");
            String pass= keyToken("pass");
            Log.d("pass",pass+"");
            userLogin(em,pass);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnsignup:
                if(isConnected()) {

                    Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

                break;


            case R.id.btnlogin:

                    if ((!etemail.getText().toString().equals("")) && (!etpassword.getText().toString().equals(""))) {
                        getToken("email",etemail.getText().toString());
                        getToken("pass",etpassword.getText().toString());
                        userLogin(etemail.getText().toString(), etpassword.getText().toString());
                    }

                    else {
                        Toast.makeText(MainActivity.this, "Please enter both the inputs", Toast.LENGTH_SHORT).show();
                    }

                break;
        }
    }

    public void userLogin(String email, String password) {
        OkHttpClient okHttpClient = new OkHttpClient();
        if (isConnected()) {
            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password", password)
                    .build();
            Log.d("demo", email);
            Request request = new Request.Builder()
                    .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/login")
                    .post(formBody)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("fail1", "entered");
                    error = e.getMessage();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("fail", "entered");
                            Toast.makeText(MainActivity.this, "Login UnSuccessfull" + error, Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    Log.d("response ", "entered");
                    String responseString = response.body().string();
                    Log.d("demo1", responseString);
                    try {
                        Gson gson = new Gson();
                        final TokenResponse tokenResponse = gson.fromJson(responseString, TokenResponse.class);
                        Log.d("tokenuser_id", tokenResponse.getUser_id() + "");
                        Log.d("tokenval", tokenResponse.getToken() + "");

                        saveToken(tokenResponse);
                        if (tokenResponse.getToken() == null) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    deleteToken(tokenResponse.getToken());
                                    Log.d("errrorr", "login");
                                    Toast.makeText(MainActivity.this, "Login was not Succesful", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("noerror", "entered not null");
                                    Toast.makeText(MainActivity.this, "User Succesfully Logged In", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(MainActivity.this, MessageActivity.class);
                                    i.putExtra("ALIST", tokenResponse.getToken());
                                    startActivity(i);
                                    finishAffinity();
                                }
                            });

                        }
                    } catch (Exception e) {

                    }
                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteToken(String tok) {
        SharedPreferences.Editor editor = getSharedPreferences(MYPREFNAME, MODE_PRIVATE).edit();
       editor.remove("token");
        editor.clear();
        editor.commit();
    }
    public void saveToken(TokenResponse tokenResponse) {
        SharedPreferences.Editor editor = getSharedPreferences(MYPREFNAME,MODE_PRIVATE).edit();
        editor.putString("token",tokenResponse.getToken());
        editor.putInt("user_id",tokenResponse.getUser_id());
        editor.putString("fullname",tokenResponse.getUser_fname()+ " "+tokenResponse.getUser_lname());
        editor.commit();

    }
    public void getToken(String k,String v) {
        SharedPreferences.Editor editor = getSharedPreferences(MYPREFNAME,MODE_PRIVATE).edit();
        editor.putString(k,v);
        editor.commit();

    }
    public String keyToken(String k) {
        SharedPreferences sharedPreferences = getSharedPreferences(MYPREFNAME,MODE_PRIVATE);
      String v = sharedPreferences.getString(k,"");
      return v;

    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }



}
