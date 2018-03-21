package com.example.Mnemonica;

/**
 * Created by Bengu on 18.4.2017.
 */

import java.util.Comparator;

public class Act {
    private String actName;
    private int hour;
    private int minute;
    private int month;
    private int date;
    private int year;
    private int absance;
    private int attendanceLimit;
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

    public int getAbsance() {
        return absance;
    }

    public String getKey() {
        return key;
    }

    public int getAttendanceLimit() {return attendanceLimit;}

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

    public void setKey(String key) {
        this.key = key;
    }

    public void setAbsance(int absance) {
        this.absance = absance;
    }

    public void setAttendanceLimit(int attendanceLimit) {
        this.attendanceLimit = attendanceLimit;
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

}