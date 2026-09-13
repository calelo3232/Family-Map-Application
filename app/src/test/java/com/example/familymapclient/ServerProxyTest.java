package com.example.familymapclient;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.example.familymapclient.application.ServerProxy;
import main.requests.LoginRequest;
import main.requests.RegisterRequest;
import main.results.EventsResult;
import main.results.LoginResult;
import main.results.PersonsResult;
import main.results.RegisterResult;

public class ServerProxyTest {
    private static ServerProxy serverProxy;
    private static String serverHost;
    private static String serverPort;
    private static RegisterResult registerResult;

    @BeforeAll
    public static void setUp() {
        serverProxy = ServerProxy.getInstance();
        serverHost = "localhost";
        serverPort = "8080";
        RegisterRequest registerRequest = new RegisterRequest("uname","pword","email","first","last","m");
        registerResult = serverProxy.register(serverHost, serverPort, registerRequest);
    }

    @Test
    public void registerPass() {
        assertTrue(registerResult.isSuccess());
    }

    @Test
    public void registerFail() {
        RegisterRequest repeatRegisterRequest = new RegisterRequest("uname","pword","email","first","last","m");
        RegisterResult repeatRegisterResult = serverProxy.register(serverHost, serverPort, repeatRegisterRequest);
        assertFalse(repeatRegisterResult.isSuccess());
    }

    @Test
    public void loginPass() {
        LoginRequest loginRequest = new LoginRequest("uname", "pword");
        LoginResult loginResult = serverProxy.login(serverHost, serverPort, loginRequest);
        assertTrue(loginResult.isSuccess());
    }

    @Test
    public void loginFail() {
        LoginRequest loginRequest = new LoginRequest("wrong", "wrong");
        LoginResult loginResult = serverProxy.login(serverHost, serverPort, loginRequest);
        assertFalse(loginResult.isSuccess());
    }

    @Test
    public void personRetrievalPass() {
        String authtoken = registerResult.getAuthtoken();
        PersonsResult personsResult = serverProxy.getPersonsResult(serverHost, serverPort, authtoken);
        assertTrue(personsResult.isSuccess());
    }

    @Test
    public void personRetrievalFail() {
        String authtoken = "Invalid Authtoken";
        PersonsResult personsResult = serverProxy.getPersonsResult(serverHost, serverPort, authtoken);
        assertFalse(personsResult.isSuccess());
    }

    @Test
    public void eventRetrievalPass() {
        String authtoken = registerResult.getAuthtoken();
        EventsResult eventsResult = serverProxy.getEventsResult(serverHost, serverPort, authtoken);
        assertTrue(eventsResult.isSuccess());
    }

    @Test
    public void eventRetrievalFail() {
        String authtoken = "Invalid Authtoken";
        EventsResult eventsResult = serverProxy.getEventsResult(serverHost, serverPort, authtoken);
        assertFalse(eventsResult.isSuccess());
    }
}