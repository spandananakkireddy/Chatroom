package com.example.manup.group32_inclass10;
import android.Manifest;
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

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    EditText fname, lname, email, pwd, rpwd;
    Button btncancel, btnsignup;
    public static final String MYPREFNAME = "pref";
    public static final String ALIST = "tok";
    String err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setTitle("Sign up");
        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        email = (EditText) findViewById(R.id.email);
        pwd = (EditText) findViewById(R.id.pwd);
        rpwd = (EditText) findViewById(R.id.rpwd);
        btncancel = (Button) findViewById(R.id.btncancel);
        btnsignup = (Button) findViewById(R.id.btnsignup);
        btncancel.setOnClickListener(this);
        btnsignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btncancel:
                if(isConnected()) {
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(SignupActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btnsignup:
                if (isConnected()) {
                    try {

                        if (fname.getText().toString().equals("") || !checkName(fname)) {
                            Toast.makeText(SignupActivity.this, "Please enter first name", Toast.LENGTH_SHORT).show();
                            fname.setError("please enter a valid name" + "\n" + "Name should only have character input");
                        } else if (lname.getText().toString().equals("") || !checkName(lname)) {
                            Toast.makeText(SignupActivity.this, "Please enter last name", Toast.LENGTH_SHORT).show();
                            lname.setError("please enter a valid name" + "\n" + "Name should only have character input");
                        } else if(fname.getText().toString().equals(lname.getText().toString()))
                        {
                         Toast.makeText(SignupActivity.this,"Please enter a different value for firstname and lastname",Toast.LENGTH_SHORT).show();
                        }
                        else if (email.getText().toString().equals("") || !checkEmail(email)) {
                            Toast.makeText(SignupActivity.this, "Please enter valid email", Toast.LENGTH_SHORT).show();
                            email.setError("please enter a valid email" + "\n" + "Format- xyz@xyz.com");
                        } else if (pwd.getText().toString().equals("")) {
                            Toast.makeText(SignupActivity.this, "Please choose password", Toast.LENGTH_SHORT).show();
                        } else if (rpwd.getText().toString().equals("")) {
                            Toast.makeText(SignupActivity.this, "Please enter the password again to verify", Toast.LENGTH_SHORT).show();
                        } else if (!(pwd.getText().toString().equals(rpwd.getText().toString()))) {
                            Toast.makeText(SignupActivity.this, "Please enter matching passwords", Toast.LENGTH_SHORT).show();
                        } else if (pwd.getText().toString().length() < 6) {
                            Toast.makeText(SignupActivity.this, "Password should be of minimum six characters", Toast.LENGTH_SHORT).show();
                        } else
                            callPostSignUp(fname.getText().toString(), lname.getText().toString(), email.getText().toString(), pwd.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(SignupActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void callPostSignUp(String fname, String lname, String email, String pwd1) throws IOException {

        OkHttpClient okHttpClient = new OkHttpClient();
        if (isConnected()) {
            RequestBody formBody = new FormBody.Builder()
                    .add("fname", fname)
                    .add("lname", lname)
                    .add("password", pwd1)
                    .add("email", email)
                    .build();
            Log.d("demo", fname);
            Request request = new Request.Builder()
                    .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/signup")
                    .post(formBody)
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    err = e.getMessage();
                    SignupActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignupActivity.this, "Please Try Again" + err, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            String responseString = response.body().string();
                            Log.d("demo1", responseString);
                            Gson gson = new Gson();
                            final TokenResponse tokenResponse = gson.fromJson(responseString, TokenResponse.class);
                            Log.d("tokenre", tokenResponse.getToken());
                            saveToken(tokenResponse);
                            if (tokenResponse.getToken() != null) {
                                SignupActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignupActivity.this, "User has been created", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(SignupActivity.this, MessageActivity.class);
                                        i.putExtra(ALIST, tokenResponse.getToken());
                                        startActivity(i);
                                        finishAffinity();
                                    }
                                });
                            }
                        }
                        else {
                            SignupActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SignupActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }
        else
        {
            Toast.makeText(SignupActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean checkEmail(EditText email) {
        String emailReg = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.getText().toString().matches(emailReg)) {
            return true;
        }
        else
        {
            return false;
        }
    }
    public static boolean checkName(EditText name)
    {
        String nameReg = "[a-zA-Z]+";
        if (name.getText().toString().matches(nameReg))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public void saveToken(TokenResponse tokenResponse) {
        SharedPreferences.Editor editor = getSharedPreferences(MYPREFNAME,MODE_PRIVATE).edit();
        editor.putString("token",tokenResponse.getToken());
        editor.putInt("user_id",tokenResponse.getUser_id());
        editor.putString("fullname",tokenResponse.getUser_fname()+ " "+tokenResponse.getUser_lname());
        editor.commit();
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

