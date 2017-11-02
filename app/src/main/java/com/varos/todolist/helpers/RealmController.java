package com.varos.todolist.helpers;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.varos.todolist.items.NoteItem;
import com.varos.todolist.items.User;
import com.varos.todolist.receivers.NotificationReceiver;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;


public class RealmController {
    private static final String SHARE_PREF_NAME = "ToDo";
    private static final String LOGINED = "logined";
    private static final String USER_ID = "userId";
    private static final String ID = "id";
    private static final String PASSWORD = "password";
    private static final String USER_NAME = "userName";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String ACTION = "com.varos.todolist.notify";
    @SuppressLint("StaticFieldLeak")
    private static RealmController instance;
    private final Realm realm;
    private static User user;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private RealmController() {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController getInstance() {
        if (instance == null) {
            instance = new RealmController();
        }
        return instance;
    }

    public static void initController(Context cnx) {
        context = cnx.getApplicationContext();
    }

    public Realm getRealm() {
        return realm;
    }

    public void refreshNote(NoteItem oldItem, NoteItem newItem) {
        if (oldItem.isNotificationEnabled()) {
            cancelNotifications(oldItem);
        }

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(newItem);
        realm.commitTransaction();
        if (newItem.isNotificationEnabled()) {
            setNotification(newItem);
        }
    }

    public void addNote(NoteItem noteItem) {
        noteItem.setUserId(user.getId());
        if (realm.where(NoteItem.class).findAll().max(ID) != null) {
            noteItem.setId(realm.where(NoteItem.class).max(ID).intValue() + 1);
        } else {
            noteItem.setId(1);
        }
        realm.beginTransaction();
        realm.copyToRealm(noteItem);
        realm.commitTransaction();
        if (noteItem.isNotificationEnabled()) {
            setNotification(noteItem);
        }
    }

    public void deleteNote(NoteItem noteItem) {
        realm.beginTransaction();
        realm.where(NoteItem.class).equalTo(ID, noteItem.getId()).findAll().first().deleteFromRealm();
        realm.commitTransaction();
    }

    public void logoutUser() {
        if (realm.where(NoteItem.class).equalTo(USER_ID, user.getId()) != null
                && !realm.where(NoteItem.class).equalTo(USER_ID, user.getId()).findAll().isEmpty()) {
            RealmResults<NoteItem> notes = realm.where(NoteItem.class).equalTo(USER_ID, user.getId()).findAll();
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).isNotificationEnabled()) {
                    cancelNotifications(notes.get(i));
                }
            }
        }
        user = null;
        context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(LOGINED, false)
                .putInt(USER_ID, 0).apply();
    }


    public List getUserNotes() {
        if (user == null) {
            return null;
        }
        if (realm.where(NoteItem.class).equalTo(USER_ID, user.getId()).findAll().isEmpty()) {
            return null;
        }
        return new ArrayList<>(realm.where(NoteItem.class).equalTo(USER_ID, user.getId()).findAll());
    }

    public boolean isUserExists(String userName, String password) {
        return !realm.where(User.class).equalTo(USER_NAME, userName).equalTo(PASSWORD, password).findAll().isEmpty();
    }

    public boolean isUsernameExists(String userName) {
        return !realm.where(User.class).equalTo(USER_NAME, userName).findAll().isEmpty();
    }

    public void loginUser(String userName, String password) {
        user = realm.where(User.class).equalTo(USER_NAME, userName).equalTo(PASSWORD, password).findFirst();
        context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(LOGINED, true)
                .putInt(USER_ID, user.getId()).apply();
        if (realm.where(NoteItem.class).equalTo(USER_ID, user.getId()) != null
                && !realm.where(NoteItem.class).equalTo(USER_ID, user.getId()).findAll().isEmpty()) {
            RealmResults<NoteItem> notes = realm.where(NoteItem.class).equalTo(USER_ID, user.getId()).findAll();
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).isNotificationEnabled()) {
                    if (notes.get(i).getDate().getTime() < System.currentTimeMillis()) {
                        realm.beginTransaction();
                        notes.get(i).setOverdue(true);
                        notes.get(i).setNotificationEnabled(false);
                        realm.commitTransaction();
                    } else {
                        setNotification(notes.get(i));
                    }
                }
            }
        }
    }

    public void logInSavedUser() {
        user = realm.where(User.class).equalTo(ID, context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).
                getInt(USER_ID, 0)).findFirst();
    }

    public void registerUser(String userName, String password) {
        User newUser = new User();
        newUser.setUserName(userName);
        newUser.setPassword(password);
        if (realm.where((User.class)).max(ID) != null) {
            newUser.setId(realm.where(User.class).max(ID).intValue() + 1);
        } else {
            newUser.setId(1);
        }
        realm.beginTransaction();
        realm.copyToRealm(newUser);
        realm.commitTransaction();

        user = newUser;
        context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(LOGINED, true)
                .putInt(USER_ID, user.getId()).apply();
    }

    public boolean isLogined() {
        return context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).getBoolean(LOGINED, false);
    }

    private void setNotification(NoteItem noteItem) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(TITLE, noteItem.getTitle());
        intent.putExtra(DESCRIPTION, noteItem.getDescription());
        intent.putExtra(ID, noteItem.getId());
        intent.setAction(ACTION);
        PendingIntent sender = PendingIntent.getBroadcast(context, noteItem.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, noteItem.getDate().getTime(), sender);
    }

    private void cancelNotifications(NoteItem noteItem) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, noteItem.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void closeRealm() {
        realm.close();
    }
}
