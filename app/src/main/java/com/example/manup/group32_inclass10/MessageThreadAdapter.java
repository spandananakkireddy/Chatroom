package com.example.manup.group32_inclass10;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Spandana Nakkireddy on 4/5/2018.
 */

public class MessageThreadAdapter extends ArrayAdapter<MessageThread.Thread>{

    public static final String MYPREFNAME = "pref";
    SharedPreferences sharedPreferences;
    List<MessageThread.Thread> mdata;
    MessageActivity mContext;
    int mResource;



    public MessageThreadAdapter(MessageActivity context, int resource, List<MessageThread.Thread> objects) {
        super(context,resource,objects);
        this.mContext = context;
        this.mdata = objects;
        this.mResource = resource;

    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final MessageThread.Thread  thread= mdata.get(position);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,parent,false);
        }
        sharedPreferences = mContext.getSharedPreferences(MYPREFNAME,MODE_PRIVATE);
        TextView tvthread= (TextView)convertView.findViewById(R.id.tvthread);
        ImageView imgdelete = (ImageView)convertView.findViewById(R.id.imgdelete);
        imgdelete.setVisibility(View.INVISIBLE);
        if(thread.getUser_id()==sharedPreferences.getInt("user_id",0)){
            imgdelete.setVisibility(View.VISIBLE);
            imgdelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mdata.remove(position);
                    mContext.deleteMessageThread(thread);
                    notifyDataSetChanged();
                }

            });
        }

        tvthread.setText(thread.getTitle());
        return convertView;
    }
}
