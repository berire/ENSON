package com.example.Mnemonica;

/**
 * Created by Bengu on 18.4.2017.
 */

import java.util.Comparator;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Act {

    public String text;
    public String uid;
    public String author;
    public String title;
    public String body;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
    private String actName;
    private int hour;
    private int minute;
    private int month;
    private int date;
    private int year;
    private String key;
    private String destination;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getDate() {
        return date;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getActName() {
        return actName;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static Comparator<Act> ActHourComp = new Comparator<Act>() {

        public int compare(Act a1, Act a2) {

            int hour1 = a1.getHour();
            int hour2 = a2.getHour();

	   /*For ascending order*/
            return hour1-hour2;

	   /*For descending order*/
            //rollno2-rollno1;
        }};
    public static Comparator<Act> ActMinuteComp = new Comparator<Act>() {

        public int compare(Act a1, Act a2) {

            int minute1 = a1.getMinute();
            int minute2 = a2.getMinute();

	   /*For ascending order*/
            return minute1-minute2;

	   /*For descending order*/
            //rollno2-rollno1;
        }};
    public static Comparator<Act> ActDateComp = new Comparator<Act>() {

        public int compare(Act a1, Act a2) {

            int date1 = a1.getDate();
            int date2 = a2.getDate();

	   /*For ascending order*/
            return date1-date2;

	   /*For descending order*/
            //rollno2-rollno1;
        }};
    public static Comparator<Act> ActMonthComp = new Comparator<Act>() {

        public int compare(Act a1, Act a2) {

            int month1 = a1.getMonth();
            int month2 = a2.getMonth();

	   /*For ascending order*/
            return month1-month2;

	   /*For descending order*/
            //rollno2-rollno1;
        }};

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);

        return result;
    }
    // [END post_to_map]
}
