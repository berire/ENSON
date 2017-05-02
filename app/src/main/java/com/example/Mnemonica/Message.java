package com.example.Mnemonica;

/**
 * Created by user on 27.4.2017.
 */

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Message {
    public String text;
    public String uid;
    public String author;
    public String title;
    public String body;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();


    public Message() {

    }

    public Message(String author, String text) {
        this.author = author;
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }


    public void setAuthor(String au) {
       author=au;
    }

    public void setText(String txt) {
        text=txt;

    }



}



