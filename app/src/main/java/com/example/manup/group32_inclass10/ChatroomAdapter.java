package com.example.manup.group32_inclass10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Spandana Nakkireddy on 4/6/2018.
 */

public class ChatroomAdapter extends RecyclerView.Adapter<ChatroomAdapter.ViewHolder> {

    ArrayList<Chatroom.Message> mData;
    ChatroomActivity mContext;
    public static final String MYPREFNAME = "pref";
    SharedPreferences sharedPreferences;
    int userid;

    public ChatroomAdapter(ChatroomActivity mContext, ArrayList<Chatroom.Message> mData, int user_id) {
        this.mData = mData;
        this.mContext = mContext;
        userid = user_id;
    }

    @Override
    public ChatroomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, parent, false);
        Context context = parent.getContext();
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(ChatroomAdapter.ViewHolder holder, final int position) {
        final Chatroom.Message message = mData.get(position);
        sharedPreferences = mContext.getSharedPreferences(MYPREFNAME, MODE_PRIVATE);
        holder.tvmessage.setText(message.getMessage());
        holder.tvname.setText(message.getUser_fname() + message.getUser_lname());
        final String time = message.getCreated_at();
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        TimeZone estTime = TimeZone.getTimeZone("EST");
        DateFormat gmtFormat = new SimpleDateFormat();
        gmtFormat.setTimeZone(estTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date inputDate = null;
        try {
            inputDate = sdf.parse(time);
            String formattedDateString = sdf.format(inputDate);
            Date outputDate = sdf.parse(formattedDateString);
            PrettyTime prettyTime = new PrettyTime();
            String prettyTimeString = prettyTime.format(outputDate);
            holder.tvtime.setText(prettyTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (message.getUser_id() == sharedPreferences.getInt("user_id", 0)) {
            holder.imgdelete1.setVisibility(View.VISIBLE);


        }
        holder.imgdelete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {


                    mData.remove(position);
                    mContext.callDelete(message.getId());
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mData.size());


                }
                else
                {
                    Toast.makeText(mContext,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvmessage, tvname, tvtime;
        ImageView imgdelete1;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            tvname = (TextView) itemView.findViewById(R.id.tvname);
            tvmessage = (TextView) itemView.findViewById(R.id.tvmessage);
            tvtime = (TextView) itemView.findViewById(R.id.tvtime);
            imgdelete1 = (ImageView) itemView.findViewById(R.id.imgdelete1);
            imgdelete1.setVisibility(View.INVISIBLE);

        }

    }


    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }
}
