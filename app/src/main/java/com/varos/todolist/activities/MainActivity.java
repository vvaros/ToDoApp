package com.varos.todolist.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.varos.todolist.fragments.LoginRegisterFragment;
import com.varos.todolist.fragments.NoteCreateFragment;
import com.varos.todolist.items.NoteItem;
import com.varos.todolist.fragments.NoteListFragment;
import com.varos.todolist.R;
import com.varos.todolist.helpers.RealmController;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NoteCreateFragment.CreateNoteListener,
        LoginRegisterFragment.LoginRegisterListener, NoteListFragment.NoteListListerer {

    private static final String LOGIN_REGISTER_TAG = "login_register_fragment";
    private static final String CREATE_NOTE_TAG = "create_note_fragment";
    private static final String NOTE_LIST_TAG = "note_list_fragment";

    Mode mode = Mode.LIST;
    NoteCreateFragment noteCreateFragment;
    NoteListFragment noteListFragment;
    LoginRegisterFragment loginRegisterFragment;
    private int enter_animation = R.anim.appear_from_right;
    private int exit_animation = R.anim.disappear_to_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RealmController.initController(this);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        noteCreateFragment = (NoteCreateFragment) fragmentManager.findFragmentByTag(CREATE_NOTE_TAG);
        if (noteCreateFragment == null) {
            noteCreateFragment = new NoteCreateFragment();
            fragmentManager.beginTransaction().
                    add(R.id.container, noteCreateFragment, CREATE_NOTE_TAG).
                    commit();
        }
        noteListFragment = (NoteListFragment) fragmentManager.findFragmentByTag(NOTE_LIST_TAG);
        if (noteListFragment == null) {
            noteListFragment = new NoteListFragment();
            noteListFragment.setNotes((ArrayList) RealmController.getInstance().getUserNotes());
            fragmentManager.beginTransaction().
                    add(R.id.container, noteListFragment, NOTE_LIST_TAG).
                    commit();
        }

        loginRegisterFragment = (LoginRegisterFragment) fragmentManager.findFragmentByTag(LOGIN_REGISTER_TAG);
        if (loginRegisterFragment == null) {
            loginRegisterFragment = new LoginRegisterFragment();
            fragmentManager.beginTransaction().
                    add(R.id.container, loginRegisterFragment, LOGIN_REGISTER_TAG).
                    commit();


        }


        if (savedInstanceState == null) {
            if (RealmController.getInstance().isLogined()) {
                RealmController.getInstance().logInSavedUser();
                noteListFragment.setNotes((ArrayList) RealmController.getInstance().getUserNotes());
                startNoteList();
            } else {
                mode = Mode.LOGIN;
                startLoginRegister();
            }
        } else {
            mode = (Mode) savedInstanceState.getSerializable("mode");
            switch (mode) {
                case LIST:
                    startNoteList();
                    break;
                case LOGIN:
                    startLoginRegister();
                    break;
                case CREATE:
                    startNoteCreate();
                    break;
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        noteListFragment.setNoteListListerer(this);
        noteCreateFragment.setCreateNoteListener(this);
        loginRegisterFragment.setListener(this);

    }

    private void startNoteList() {
        if (mode == Mode.CREATE) {
            enter_animation = R.anim.appear_from_left;
            exit_animation = R.anim.disappear_to_right;
        } else {
            enter_animation = R.anim.appear_from_right;
            exit_animation = R.anim.disappear_to_left;
        }
        mode = Mode.LIST;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(enter_animation, exit_animation)
                .replace(R.id.container, noteListFragment, NOTE_LIST_TAG)
                .commitAllowingStateLoss();
    }


    private void startNoteCreate() {
        enter_animation = R.anim.appear_from_right;
        exit_animation = R.anim.disappear_to_left;
        mode = Mode.CREATE;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(enter_animation, exit_animation)
                .replace(R.id.container, noteCreateFragment, CREATE_NOTE_TAG)
                .commitAllowingStateLoss();
    }

    private void startLoginRegister() {

        enter_animation = R.anim.appear_from_left;
        exit_animation = R.anim.disappear_to_right;
        mode = Mode.LOGIN;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(enter_animation, exit_animation)
                .replace(R.id.container, loginRegisterFragment, LOGIN_REGISTER_TAG)
                .commitAllowingStateLoss();
    }

    @Override
    public void openNoteList(NoteItem noteItem) {
        RealmController.getInstance().addNote(noteItem);
        noteListFragment.addNote(noteItem);
        startNoteList();
    }

    @Override
    public void refreshNoteList(NoteItem oldItem, NoteItem newItem) {
        RealmController.getInstance().refreshNote(oldItem, newItem);
        noteListFragment.refreshList(oldItem, newItem);
        startNoteList();
    }

    @Override
    public void deleteNote(NoteItem noteItem) {
        noteListFragment.removeNote(noteItem);
        RealmController.getInstance().deleteNote(noteItem);
        startNoteList();
    }

    @Override
    public void cancelCreating() {
        startNoteList();
    }

    @Override
    public boolean onLoginButtonClick(String username, String password) {
        if (RealmController.getInstance().isUserExists(username, password)) {
            RealmController.getInstance().loginUser(username, password);
            noteListFragment.setNotes((ArrayList) RealmController.getInstance().getUserNotes());
            startNoteList();
            return true;
        }
        return false;
    }

    @Override
    public boolean onRegisterButtonClick(String username, String password) {
        if (RealmController.getInstance().isUserExists(username, password)) {
            RealmController.getInstance().loginUser(username, password);
            noteListFragment.setNotes((ArrayList) RealmController.getInstance().getUserNotes());
            startNoteList();
            return true;
        } else if (RealmController.getInstance().isUsernameExists(username)) {
            return false;
        }
        RealmController.getInstance().registerUser(username, password);
        noteListFragment.setNotes((ArrayList) RealmController.getInstance().getUserNotes());
        startNoteList();
        return true;
    }

    @Override
    public void openCreateNoteCreate(NoteItem noteItem) {
        noteCreateFragment.setNoteToCreate(noteItem);
        startNoteCreate();
    }

    @Override
    public void logOut() {
        startLoginRegister();
        RealmController.getInstance().logoutUser();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("mode", mode);
    }

    public enum Mode {
        LOGIN, CREATE, LIST
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RealmController.getInstance().closeRealm();
    }

    @Override
    public void onBackPressed() {
        if (mode == Mode.CREATE) {
            noteCreateFragment.cancelCreating();
        } else if (mode == Mode.LIST) {
            noteListFragment.logOutUser();
        } else {
            super.onBackPressed();
        }
    }
}
