package com.example.manup.group32_inclass10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ChatroomActivity extends AppCompatActivity {

    private  RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private  RecyclerView.LayoutManager mLayoutManager;
    public static final String MYPREFNAME = "pref";
    SharedPreferences sharedPreferences;
    ArrayList<Chatroom.Message> messageArrayList;
    TextView tvchatname;
    MessageThread.Thread messageThread;
    ImageView imghome,imgsend;
    EditText etmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        setTitle("Chatroom");
        final ArrayList<MessageThread.Thread> alist = (ArrayList<MessageThread.Thread>) getIntent().getSerializableExtra("ALIST");
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        tvchatname = (TextView)findViewById(R.id.tvchatname);
        imghome = (ImageView)findViewById(R.id.imghome);
        imgsend = (ImageView)findViewById(R.id.imgsend);
        sharedPreferences = getSharedPreferences(MYPREFNAME,MODE_PRIVATE);
        etmessage = (EditText)findViewById(R.id.etmessage);
        messageArrayList = new ArrayList<Chatroom.Message>();
        if(getIntent().getExtras().getSerializable("thread")!=null){
            messageThread= (MessageThread.Thread) getIntent().getExtras().getSerializable("thread");
            tvchatname.setText(messageThread.getTitle());

        }

        mLayoutManager = new LinearLayoutManager(ChatroomActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        getMessages();

        imghome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()) {
                    Intent i = new Intent(ChatroomActivity.this, MessageActivity.class);
                    i.putExtra("ALIST", alist);
                    startActivity(i);
                    finish();
                }
                else {
                    Toast.makeText(ChatroomActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    addMessages(etmessage.getText().toString());
                    etmessage.setText("");
                }
                else
                {
                    Toast.makeText(ChatroomActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private void addMessages(String s) {

        OkHttpClient okHttpClient = new OkHttpClient();
        if (isConnected()) {
            RequestBody formBody = new FormBody.Builder()
                    .add("message", s)
                    .add("thread_id", String.valueOf(messageThread.getId()))
                    .build();
            Request request = new Request.Builder()
                    .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/message/add")
                    .header("Authorization", "BEARER " + sharedPreferences.getString("token", ""))
                    .post(formBody).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.getMessage();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {

                        String val = response.body().string();
                        Log.d("demoAddSuccess", val);
                        getMessages();


                    } else {
                        Log.d("demoerror", "Not Success\ncode : " + response.code());
                    }

                }
            });
        } else {
            Toast.makeText(ChatroomActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getMessages() {


        OkHttpClient okHttpClient = new OkHttpClient();
        if (isConnected()) {

            Request request = new Request.Builder()
                    .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/messages/" + messageThread.getId())
                    .addHeader("Authorization", "BEARER " + sharedPreferences.getString("token", ""))
                    // .post(formBody)  // Use PUT on this line.
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.getMessage();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final ArrayList<Chatroom.Message> chatMessage;
                        String responseString = response.body().string();
                        Log.d("msgs", responseString);
                        try {
                            Gson gson = new Gson();
                            Chatroom chatroom = gson.fromJson(responseString, Chatroom.class);
                            chatMessage = chatroom.getMessages();
                            getMessageThreadResponse(chatMessage);
                            Log.d("demochat", chatMessage.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("demoerror", "Not Success\ncode : " + response.code());
                    }


                }
            });
        } else {
            Toast.makeText(ChatroomActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }


    public void callDelete(int id) {
        Log.d("demodelete", "" + id);

        OkHttpClient okHttpClient = new OkHttpClient();
        if (isConnected()) {

            Request request = new Request.Builder()
                    .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/message/delete/" + id)
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
                        Log.d("demoDeleteSuccess", val);


                    } else {
                        Log.d("demoerror", "Not Success\ncode : " + response.code());
                    }
                }
            });
        }
        else
        {
            Toast.makeText(ChatroomActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getMessageThreadResponse(final ArrayList<Chatroom.Message> chats) {
        Log.d("demo_list_newMethod",chats.toString());

        ChatroomActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setHasFixedSize(true);
                mAdapter = new ChatroomAdapter(ChatroomActivity.this,chats,messageThread.getUser_id());
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

   /* @Override
    protected void onPause() {
        super.onPause();
        startActivity(new Intent(this, MessageActivity.class));
    }
*/
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
