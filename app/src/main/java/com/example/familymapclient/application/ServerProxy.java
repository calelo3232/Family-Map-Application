package com.example.familymapclient.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Function;
import main.results.EventsResult;
import main.results.LoginResult;
import main.results.PersonsResult;
import main.results.RegisterResult;
import main.requests.LoginRequest;
import main.requests.RegisterRequest;
import com.google.gson.Gson;

public class ServerProxy {
    private static ServerProxy instance = new ServerProxy();

    public static ServerProxy getInstance() {
        return instance;
    }

    private ServerProxy() {
    }

    public LoginResult login(String serverHost, String serverPort, LoginRequest loginRequest) {
        return sendRequest(serverHost, serverPort, "/user/login", "POST", loginRequest, null,
                LoginResult.class, msg -> new LoginResult(msg, false), "Error: Login failed");
    }

    public RegisterResult register(String serverHost, String serverPort, RegisterRequest registerRequest) {
        return sendRequest(serverHost, serverPort, "/user/register", "POST", registerRequest, null,
                RegisterResult.class, msg -> new RegisterResult(msg, false), "Error: Registering user failed");
    }

    public PersonsResult getPersonsResult(String serverHost, String serverPort, String authtoken) {
        return sendRequest(serverHost, serverPort, "/person", "GET", null, authtoken,
                PersonsResult.class, msg -> new PersonsResult(msg, false), "Error: Get all people failed");
    }

    public EventsResult getEventsResult(String serverHost, String serverPort, String authtoken) {
        return sendRequest(serverHost, serverPort, "/event", "GET", null, authtoken,
                EventsResult.class, msg -> new EventsResult(msg, false), "Error: Get all events failed");
    }

    private <T> T sendRequest(String serverHost, String serverPort, String path, String method,
                               Object requestBody, String authtoken, Class<T> resultClass,
                               Function<String, T> errorResult, String ioErrorMessage) {
        Gson gson = new Gson();
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + path);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(requestBody != null);
            if (authtoken != null) {
                http.addRequestProperty("Authorization", authtoken);
            }
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            if (requestBody != null) {
                OutputStream os = http.getOutputStream();
                writeString(gson.toJson(requestBody), os);
                os.close();
            }

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                return gson.fromJson(respData, resultClass);
            } else {
                return errorResult.apply(http.getResponseMessage());
            }
        } catch (IOException e) {
            return errorResult.apply(ioErrorMessage);
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
