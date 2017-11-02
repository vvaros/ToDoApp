package com.varos.todolist.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.varos.todolist.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginRegisterFragment extends Fragment {

    EditText usernameEd;
    EditText passwordEd;
    Button registerButton;
    Button loginButton;
    LoginRegisterListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usernameEd = view.findViewById(R.id.user_name);
        passwordEd = view.findViewById(R.id.password);
        registerButton = view.findViewById(R.id.register_button);
        loginButton = view.findViewById(R.id.sign_in_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (String.valueOf(usernameEd.getText()).length() < 6) {
                    usernameEd.setError(getString(R.string.min_characters_count));
                    return;
                }
                if (String.valueOf(passwordEd.getText()).length() < 6) {
                    passwordEd.setError(getString(R.string.min_characters_count));
                    return;
                }
                hideKeyboard();

                if (!listener.onLoginButtonClick(String.valueOf(usernameEd.getText()),
                        get_SHA_512_SecurePassword(String.valueOf(passwordEd.getText()), String.valueOf(usernameEd.getText())))) {
                    Toast.makeText(getActivity(), R.string.string_wrong_username, Toast.LENGTH_LONG).show();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (String.valueOf(usernameEd.getText()).length() < 6) {
                    usernameEd.setError(getString(R.string.min_characters_count));
                    return;
                }
                if (String.valueOf(passwordEd.getText()).length() < 6) {
                    passwordEd.setError(getString(R.string.min_characters_count));
                    return;
                }
                hideKeyboard();
                if (!listener.onRegisterButtonClick(String.valueOf(usernameEd.getText()),
                        get_SHA_512_SecurePassword(String.valueOf(passwordEd.getText()), String.valueOf(usernameEd.getText())))) {
                    Toast.makeText(getActivity(), R.string.username_exists, Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public String get_SHA_512_SecurePassword(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes("UTF-8"));
            byte[] bytes = md.digest(passwordToHash.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_register, container, false);
    }


    public void setListener(LoginRegisterListener listener) {
        this.listener = listener;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    public interface LoginRegisterListener {
        boolean onLoginButtonClick(String username, String password);

        boolean onRegisterButtonClick(String username, String password);
    }

}
