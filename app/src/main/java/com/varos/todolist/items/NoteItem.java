package com.varos.todolist.items;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NoteItem extends RealmObject implements Parcelable {

    @PrimaryKey
    private int id;
    private int userId;
    private int color;

    private String title;
    private String description;

    private boolean notificationEnabled;
    private boolean overdue;
    private RealmDate date;

    public NoteItem() {
        date = new RealmDate();
        title = "";
        description = "";
    }

    private NoteItem(Parcel in) {
        id = in.readInt();
        userId = in.readInt();
        title = in.readString();
        description = in.readString();
        notificationEnabled = in.readByte() == 1;
        overdue = in.readByte() == 1;
        date = in.readParcelable(RealmDate.class.getClassLoader());
        color = in.readInt();
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDate(RealmDate date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public RealmDate getDate() {
        return date;
    }

    public int getColor() {
        return color;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public boolean isOverdue() {
        return overdue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(userId);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeByte((byte) (notificationEnabled ? 1 : 0));
        parcel.writeByte((byte) (overdue ? 1 : 0));
        parcel.writeParcelable(date, i);
        parcel.writeInt(color);
    }

    public static final Creator<NoteItem> CREATOR = new Creator<NoteItem>() {
        @Override
        public NoteItem createFromParcel(Parcel in) {
            return new NoteItem(in);
        }

        @Override
        public NoteItem[] newArray(int size) {
            return new NoteItem[size];
        }
    };

}
