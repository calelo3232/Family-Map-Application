package com.example.familymapclient.application;

import java.io.*;
import java.net.*;
import main.results.*;
import main.requests.*;
import com.google.gson.*;

public class ServerProxy {
    private static ServerProxy instance = new ServerProxy();

    public static ServerProxy getInstance() {
        return instance;
    }

    private ServerProxy() {
    }

    public LoginResult login(String serverHost, String serverPort, LoginRequest loginRequest) {
        Gson gson = new Gson();
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            OutputStream os = http.getOutputStream();
            writeString(gson.toJson(loginRequest), os);
            os.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                return gson.fromJson(respData, LoginResult.class);
            } else {
                return new LoginResult(http.getResponseMessage(), false);
            }
        } catch (IOException e) {
            return new LoginResult("Error: Login failed", false);
        }
    }

    public RegisterResult register(String serverHost, String serverPort, RegisterRequest registerRequest) {
        Gson gson = new Gson();
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            OutputStream os = http.getOutputStream();
            writeString(gson.toJson(registerRequest), os);
            os.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                return gson.fromJson(respData, RegisterResult.class);
            } else {
                return new RegisterResult(http.getResponseMessage(), false);
            }
        } catch (IOException e) {
            return new RegisterResult("Error: Registering user failed", false);
        }
    }

    public PersonsResult getPersonsResult(String serverHost, String serverPort, String authtoken) {
        Gson gson = new Gson();
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authtoken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                return gson.fromJson(respData, PersonsResult.class);
            } else {
                return new PersonsResult(http.getResponseMessage(), false);
            }
        } catch (IOException e) {
            return new PersonsResult("Error: Get all people failed", false);
        }
    }

    public EventsResult getEventsResult(String serverHost, String serverPort, String authtoken) {
        Gson gson = new Gson();
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authtoken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                return gson.fromJson(respData, EventsResult.class);
            } else {
                return new EventsResult(http.getResponseMessage(), false);
            }
        } catch (IOException e) {
            return new EventsResult("Error: Get all events failed", false);
        }
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }


}
