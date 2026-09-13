package com.example.familymapclient.userinterface.activities.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.familymapclient.R;
import com.example.familymapclient.application.tasks.LoginTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import main.requests.LoginRequest;
import main.requests.RegisterRequest;

public class LoginFragment extends Fragment {
    private Button login, register;
    private RadioButton maleButton, femaleButton;
    private EditText serverHost, serverPort, username, password, firstName, lastName, email;
    private Listener listener;

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        serverHost = view.findViewById(R.id.serverHost);
        serverPort = view.findViewById(R.id.serverPort);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        email = view.findViewById(R.id.email);
        maleButton = view.findViewById(R.id.maleButton);
        femaleButton = view.findViewById(R.id.femaleButton);
        login = view.findViewById(R.id.signInButton);
        register = view.findViewById(R.id.registerButton);

        setLoginTextListener(serverHost);
        setLoginTextListener(serverPort);
        setLoginTextListener(username);
        setLoginTextListener(password);
        setRegisterTextListener(firstName);
        setRegisterTextListener(lastName);
        setRegisterTextListener(email);
        maleButton.setOnClickListener(v ->
                register.setEnabled(serverHost.getText().toString().length() > 0 && serverPort.getText().toString().length() > 0 && username.getText().toString().length() > 0 && password.getText().toString().length() > 0 && email.getText().toString().length() > 0 && firstName.getText().toString().length() > 0 && lastName.getText().toString().length() > 0 && (maleButton.isChecked() || femaleButton.isChecked())));
        femaleButton.setOnClickListener(v ->
                register.setEnabled(serverHost.getText().toString().length() > 0 && serverPort.getText().toString().length() > 0 && username.getText().toString().length() > 0 && password.getText().toString().length() > 0 && email.getText().toString().length() > 0 && firstName.getText().toString().length() > 0 && lastName.getText().toString().length() > 0 && (maleButton.isChecked() || femaleButton.isChecked())));

        login.setOnClickListener(v -> {
            Handler handler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message message) {
                    Bundle bundle = message.getData();
                    if (bundle.getBoolean("Success")) {
                        Toast.makeText(getContext(), "Hello, " + bundle.getString("First") + " " + bundle.getString("Last"), Toast.LENGTH_SHORT).show();
                        listener.notifyDone();
                    } else {
                        Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            LoginTask task = new LoginTask(handler, serverHost.getText().toString(), serverPort.getText().toString());
            task.setLoginRequest(new LoginRequest(username.getText().toString(), password.getText().toString()));
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(task);
        });

        register.setOnClickListener(v -> {
            RadioGroup radioGroup = view.findViewById(R.id.gender);
            RadioButton radioButton = view.findViewById(radioGroup.getCheckedRadioButtonId());
            String gender = radioButton.getText().toString();
            if (gender.equalsIgnoreCase("male")) {
                gender = "m";
            } else {
                gender = "f";
            }
            Handler handler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message message) {
                    Bundle bundle = message.getData();
                    if (bundle.getBoolean("Pass")) {
                        Toast.makeText(getContext(), "Hello, " + bundle.getString("First") + " " + bundle.getString("Last"), Toast.LENGTH_SHORT).show();
                        listener.notifyDone();
                    } else {
                        Toast.makeText(getContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            LoginTask task = new LoginTask(handler, serverHost.getText().toString(), serverPort.getText().toString());
            task.setRegisterRequest(new RegisterRequest(username.getText().toString(), password.getText().toString(), email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), gender));
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(task);
        });

        login.setEnabled(false);
        register.setEnabled(false);
        return view;
    }

    private void setLoginTextListener(EditText text) {
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                login.setEnabled(serverHost.getText().toString().length() > 0 && serverPort.getText().toString().length() > 0 && username.getText().toString().length() > 0 && password.getText().toString().length() > 0);
                register.setEnabled(serverHost.getText().toString().length() > 0 && serverPort.getText().toString().length() > 0 && username.getText().toString().length() > 0 && password.getText().toString().length() > 0 && email.getText().toString().length() > 0 && firstName.getText().toString().length() > 0 && lastName.getText().toString().length() > 0 && (maleButton.isChecked() || femaleButton.isChecked()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setRegisterTextListener(EditText text) {
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                register.setEnabled(serverHost.getText().toString().length() > 0 && serverPort.getText().toString().length() > 0 && username.getText().toString().length() > 0 && password.getText().toString().length() > 0 && email.getText().toString().length() > 0 && firstName.getText().toString().length() > 0 && lastName.getText().toString().length() > 0 && (maleButton.isChecked() || femaleButton.isChecked()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


}