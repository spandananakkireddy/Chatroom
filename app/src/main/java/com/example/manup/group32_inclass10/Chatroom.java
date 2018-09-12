package com.example.manup.group32_inclass10;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Spandana Nakkireddy on 4/6/2018.
 */
/*{
        "status": "ok",
        "message": {
        "user_fname": "Bob",
        "user_lname": "Smith",
        "user_id": 1,
        "id": 1846,
        "message": "ps2",
        "created_at": "2018-04-06 17:14:59"
        }
        }*/

public class Chatroom implements Serializable {

    ArrayList<Message> messages;

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "messages=" + messages +
                '}';
    }

    public static class Message{
        String user_fname,user_lname,message,created_at;
        int user_id,id;

        public String getUser_fname() {
            return user_fname;
        }

        public void setUser_fname(String user_fname) {
            this.user_fname = user_fname;
        }

        public String getUser_lname() {
            return user_lname;
        }

        public void setUser_lname(String user_lname) {
            this.user_lname = user_lname;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "user_fname='" + user_fname + '\'' +
                    ", user_lname='" + user_lname + '\'' +
                    ", message='" + message + '\'' +
                    ", created_at='" + created_at + '\'' +
                    ", user_id=" + user_id +
                    ", id=" + id +
                    '}';
        }
    }

}
