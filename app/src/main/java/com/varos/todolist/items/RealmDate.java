package com.varos.todolist.items;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Calendar;

import io.realm.RealmObject;

public class RealmDate extends RealmObject implements Parcelable {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private long time;
    private boolean timeChanged;
    private boolean dateChanged;

    private RealmDate(Parcel in) {
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        time = in.readLong();
        dateChanged = in.readByte() == 1;
        timeChanged = in.readByte() == 1;
    }

    public RealmDate(Calendar calendar) {
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.hour = calendar.get(Calendar.HOUR);
        this.minute = calendar.get(Calendar.MINUTE);
        this.time = calendar.getTimeInMillis();
    }

    public RealmDate() {
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }


    public String getTimeInfo() {
        if (timeChanged) {
            String h = hour < 10 ? "0" + hour : String.valueOf(hour);
            String m = minute < 10 ? "0" + minute : String.valueOf(minute);
            return h + ":" + m;
        } else {
            return "";
        }
    }

    public String getDateInfo() {
        if (dateChanged) {
            String m = month + 1 < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
            String d = day < 10 ? "0" + day : String.valueOf(day);
            return year + "/" + m + "/" + d;
        } else {
            return "";
        }
    }

    public void initCalendar(Calendar calendar) {
        calendar.set(year, month, day, hour, minute);
    }

    public void setTimeChanged(boolean timeChanged) {
        this.timeChanged = timeChanged;
    }

    public void setDateChanged(boolean dateChanged) {
        this.dateChanged = dateChanged;
    }

    public boolean isDateChanged() {
        return dateChanged;
    }

    public boolean isTimeChanged() {
        return timeChanged;
    }

    @NonNull
    public static String getTimeInfo(Calendar calendar) {
        String hour = calendar.get(Calendar.HOUR_OF_DAY) < 10 ?
                "0" + calendar.get(Calendar.HOUR_OF_DAY) : String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = calendar.get(Calendar.MINUTE) < 10 ?
                "0" + calendar.get(Calendar.MINUTE) : String.valueOf(calendar.get(Calendar.MINUTE));
        return hour + ":" + minute;
    }

    public static String getDateInfo(Calendar calendar) {
        String month = calendar.get(Calendar.MONTH) + 1 < 10 ?
                "0" + (calendar.get(Calendar.MONTH) + 1) : String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = calendar.get(Calendar.DAY_OF_MONTH) < 10 ?
                "0" + calendar.get(Calendar.DAY_OF_MONTH) : String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        return calendar.get(Calendar.YEAR) + "/" + month + "/" + day;
    }

    @Override
    public String toString() {
        return year + "/" + month + "/" + day + " " + hour + ":" + minute;
    }

    public static final Creator<RealmDate> CREATOR = new Creator<RealmDate>() {
        @Override
        public RealmDate createFromParcel(Parcel in) {
            return new RealmDate(in);
        }

        @Override
        public RealmDate[] newArray(int size) {
            return new RealmDate[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(year);
        parcel.writeInt(month);
        parcel.writeInt(day);
        parcel.writeInt(hour);
        parcel.writeInt(minute);
        parcel.writeLong(time);
        parcel.writeByte((byte) (dateChanged ? 1 : 0));
        parcel.writeByte((byte) (timeChanged ? 1 : 0));
    }
}
