package com.example.manup.group32_inclass10;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import org.json.JSONException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity {

    ImageView imglogout,imgadd;
    public static final String MYPREFNAME = "pref";
    SharedPreferences sharedPreferences;
    ArrayList<MessageThread.Thread> messageThreadList = new ArrayList<>() ;
    MessageThreadAdapter messageThreadAdapter;
    ListView listView;
    TextView tvfullname;
    EditText etthreadname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setTitle("Message Threads");



            listView = (ListView) findViewById(R.id.listView);
            imglogout = (ImageView) findViewById(R.id.imglogout);
            imgadd = (ImageView) findViewById(R.id.imgadd);
            etthreadname = (EditText) findViewById(R.id.etthreadname);
            tvfullname = (TextView) findViewById(R.id.tvfullname);
            sharedPreferences = getSharedPreferences(MYPREFNAME, MODE_PRIVATE);
            threadListData();
            tvfullname.setText(sharedPreferences.getString("fullname", ""));
            imgadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isConnected()) {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    String s = etthreadname.getText().toString();

                    RequestBody formBody = new FormBody.Builder()
                            .add("title", s)
                            .build();
                    Log.d("demoSSSS", s);
                    Request request = new Request.Builder()
                            .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread/add")
                            .header("Authorization", "BEARER " + sharedPreferences.getString("token", ""))
                            .post(formBody)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.getMessage();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            if (response.isSuccessful()) {
                                String val = response.body().string();
                                Log.d("demo_ra_AddSuccess", val);
                                threadListData();

                            } else {
                                Log.d("demoerror", "Not Success\ncode : " + response.code());
                            }


                        }
                    });
                    etthreadname.setText("");
                    }
                    else
                    {
                        Toast.makeText(MessageActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            imglogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isConnected())
                    {
                    SharedPreferences.Editor editor = getSharedPreferences(MYPREFNAME, MODE_PRIVATE).edit();
                    editor.remove("token");
                    editor.clear();
                    editor.commit();
                    Intent in = new Intent(MessageActivity.this, MainActivity.class);
                    startActivity(in);
                    finishAffinity();
                    }
                    else
                    {
                        Toast.makeText(MessageActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }


                }
            });


    }

    public  void threadListData() {

        OkHttpClient okHttpClient = new OkHttpClient();
        if (isConnected()) {
            Request request = new Request.Builder()
                    .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread")
                    .header("Authorization", "BEARER " + sharedPreferences.getString("token", ""))
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.getMessage();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        final ArrayList<MessageThread.Thread> messageThreadListAsync;
                        String val = response.body().string();
                        Log.d("demo_ra_Success", val);
                        try {

                            Gson gson = new Gson();
                            MessageThread messageThread = gson.fromJson(val, MessageThread.class);
                            messageThreadListAsync = messageThread.getThreads();
                            getMessageThreadResponse(messageThreadListAsync);
                            Log.d("demoList_ra", messageThreadListAsync.toString());


                        } catch (Exception e) {

                        }

                    } else {
                        Log.d("demoerror", "Not Success\ncode : " + response.code());
                    }

                }
            });
        }
        else
        {
            Toast.makeText(MessageActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    public  void  getMessageThreadResponse(final ArrayList<MessageThread.Thread> messageThreads){

        messageThreadList = messageThreads;
        Log.d("demo_list_newMethod",messageThreadList.toString());

        MessageActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
               messageThreadAdapter = new MessageThreadAdapter(MessageActivity.this,R.layout.list_row,messageThreadList);
               listView.setAdapter(messageThreadAdapter);
               listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MessageThread.Thread messageThread = messageThreadList.get(position);
                        if(isConnected()) {
                            Intent intent = new Intent(MessageActivity.this, ChatroomActivity.class);
                            intent.putExtra("thread", messageThread);
                            Log.d("messagethread", messageThread + "");
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MessageActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                   }
               });
            }
        });


    }

    public void deleteMessageThread(MessageThread.Thread delMessageThread) {
        int delMessage = delMessageThread.getId();
        OkHttpClient okHttpClient = new OkHttpClient();
        if (isConnected()) {
            Request request = new Request.Builder()
                    .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread/delete/" + delMessage)
                    .header("Authorization", "BEARER " + sharedPreferences.getString("token", ""))
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.getMessage();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {
                        String val = response.body().string();
                        Log.d("demo_ra_Success", val);
                    } else {
                        Log.d("demoerror", "Not Success\ncode : " + response.code());
                    }


                }
            });

        }
        else
        {
            Toast.makeText(MessageActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
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
