package com.example.familymapclient.application.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.example.familymapclient.application.ServerProxy;
import main.requests.LoginRequest;
import main.results.LoginResult;
import main.requests.RegisterRequest;
import main.results.RegisterResult;

public class LoginTask implements Runnable {
    private Handler handler;
    private LoginRequest loginRequest;
    private LoginResult loginResult;
    private RegisterRequest registerRequest;
    private RegisterResult registerResult;
    private ServerProxy serverProxy;
    private String serverHost;
    private String serverPort;
    private String firstName;
    private String lastName;
    private boolean loginPass;
    private boolean loginFlag;

    public LoginTask(Handler handler, String serverHost, String serverPort) {
        this.handler = handler;
        serverProxy = ServerProxy.getInstance();
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void setLoginRequest(LoginRequest loginRequest) {
        this.loginRequest = loginRequest;
        loginFlag = true;
    }

    public void setRegisterRequest(RegisterRequest registerRequest) {
        this.registerRequest = registerRequest;
        loginFlag = false;
    }

    public void setLoginFlag(boolean loginFlag) {
        this.loginFlag = loginFlag;
    }

    @Override
    public void run() {
        if (loginFlag) {
            loginResult = serverProxy.login(serverHost, serverPort, loginRequest);
            if (loginResult.isSuccess()) {
                DataSyncTask dataSyncTask = new DataSyncTask(serverHost, serverPort, loginResult.getAuthtoken());
                dataSyncTask.setData(loginResult.getPersonID());
                firstName = dataSyncTask.getFirstName();
                lastName = dataSyncTask.getLastName();
                loginPass = true;
            } else {
                loginPass = false;
            }
        } else {
            registerResult = serverProxy.register(serverHost, serverPort, registerRequest);
            if (registerResult.isSuccess()) {
                DataSyncTask dataSyncTask = new DataSyncTask(serverHost, serverPort, registerResult.getAuthtoken());
                dataSyncTask.setData(registerResult.getPersonID());
                firstName = dataSyncTask.getFirstName();
                lastName = dataSyncTask.getLastName();
                loginPass = true;
            } else {
                loginPass = false;
            }
        }
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putBoolean("Pass", loginPass);
        bundle.putString("First", firstName);
        bundle.putString("Last", lastName);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}
