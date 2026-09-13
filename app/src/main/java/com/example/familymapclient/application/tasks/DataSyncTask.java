package com.example.familymapclient.application.tasks;

import com.example.familymapclient.application.DataCache;
import com.example.familymapclient.application.ServerProxy;
import main.results.EventsResult;
import main.results.PersonsResult;

public class DataSyncTask {
    private String serverHost;
    private String serverPort;
    private String authtoken;
    private ServerProxy serverProxy;
    private DataCache dataCache;
    private String firstName;
    private String lastName;

    public DataSyncTask(String serverHost, String serverPort, String authtoken) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.authtoken = authtoken;
        serverProxy = ServerProxy.getInstance();
        dataCache = DataCache.getInstance();
    }

    public void setData(String personID) {
        PersonsResult personsResult = serverProxy.getPersonsResult(serverHost, serverPort, authtoken);
        EventsResult eventsResult = serverProxy.getEventsResult(serverHost, serverPort, authtoken);
        dataCache.initializeData(personID, personsResult, eventsResult);
        firstName = dataCache.getPersonByID(personID).getFirstName();
        lastName = dataCache.getPersonByID(personID).getLastName();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
