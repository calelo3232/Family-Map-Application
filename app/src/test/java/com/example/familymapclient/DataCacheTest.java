package com.example.familymapclient;

import static org.junit.jupiter.api.Assertions.*;
import com.example.familymapclient.application.DataCache;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import main.models.Event;
import main.models.Person;
import main.results.EventsResult;
import main.results.PersonsResult;

public class DataCacheTest {
    private static DataCache dataCache = DataCache.getInstance();
    private static Person child = new Person("childID", "uname", "first", "last", "m", "fatherID", "motherID", null);
    private static Person father = new Person("fatherID", "uname", "hubby", "last", "m", null, null, "motherID");
    private static Person mother = new Person("motherID", "uname", "wifey", "last", "f", "grandfatherID", null, "fatherID");
    private static Person grandfather = new Person("grandfatherID", "uname", "papa", "eugene", "m", "greatGrandfatherID", null, null);
    private static Person greatGrandfather = new Person("greatGrandfatherID", "uname", "prof", "neil", "m", null, null, null);
    private static Event childBirth = new Event("birthID","uname","childID",1000,1000,"United States","Dallas","Birth",2000);
    private static Event marriage1 = new Event("marriageID","uname","fatherID",500,0,"Egypt","Cairo","Marriage",1985);
    private static Event marriage2 = new Event("marriageID","uname","motherID",500,0,"Egypt","Cairo","Marriage",1985);
    private static Event death = new Event("deathID","uname","fatherID",0,750,"Honduras","Tegucigalpa","Death",2018);

    @BeforeAll
    public static void setUp() {
        ArrayList<Person> people = new ArrayList<>();
        people.add(child);
        people.add(father);
        people.add(mother);
        people.add(grandfather);
        people.add(greatGrandfather);
        PersonsResult personsResult = new PersonsResult(people, true);
        ArrayList<Event> events = new ArrayList<>();
        events.add(childBirth);
        events.add(marriage1);
        events.add(marriage2);
        events.add(death);
        EventsResult eventsResult = new EventsResult(events, true);
        dataCache.initializeData("childID", personsResult, eventsResult);
    }

    @Test
    public void familyRelationshipPass() {
        assertEquals("Child", dataCache.determineFamilialRelationship(father, child));
        assertEquals("Wife", dataCache.determineFamilialRelationship(father, mother));
        assertEquals("Mother", dataCache.determineFamilialRelationship(child, mother));
    }

    @Test
    public void familyRelationshipFail() {
        assertEquals("Same person", dataCache.determineFamilialRelationship(child, child));
        assertNotEquals("No relationship", dataCache.determineFamilialRelationship(child, father));
    }

    @Test
    public void filterGenderPass() {
        Set<Event> maleEvents = dataCache.getMaleFilter();
        for (Event event : maleEvents) {
            assertEquals("m", dataCache.getPersonByID(event.getPersonID()).getGender());
        }
    }

    @Test
    public void filterGenderFail() {
        Set<Event> femaleEvents = dataCache.getFemaleFilter();
        for (Event event : femaleEvents) {
            assertNotEquals("m", dataCache.getPersonByID(event.getPersonID()).getGender());
        }
    }

    @Test
    public void filterSidePass() {
        Set<Person> people = dataCache.getMaternalSide();
        assertTrue(people.contains(mother));
        assertTrue(people.contains(grandfather));
        assertTrue(people.contains(greatGrandfather));
    }

    @Test
    public void filterSideFail() {
        Set<Event> events = dataCache.getPaternalFilter();
        for (Event event : events) {
            assertFalse(dataCache.getMaternalFilter().contains(event));
        }
    }

    @Test
    public void sortEventsPass() {
        List<Event> events = dataCache.allEventsFromPersonID(father.getPersonID());
        assertTrue(events.get(0).getYear() < events.get(1).getYear());
    }

    @Test
    public void sortEventsFail() {
        List<Event> events = dataCache.allEventsFromPersonID(mother.getPersonID());
        assertNotEquals("Birth", events.get(0).getEventType());
    }

    @Test
    public void peopleSearchPass() {
        List<Person> people = dataCache.getPeopleFromSearch("irs");
        assertEquals(child.getPersonID(), people.get(0).getPersonID());
    }

    @Test
    public void peopleSearchFail() {
        List<Person> people = dataCache.getPeopleFromSearch("wrong");
        assertFalse(people.size() > 0);
    }

    @Test
    public void eventSearchPass() {
        List<Event> events = dataCache.getEventsFromSearch("marr");
        assertEquals(marriage1.getEventID(), events.get(0).getEventID());
        assertEquals(marriage2.getEventID(), events.get(1).getEventID());
    }

    @Test
    public void eventSearchFail() {
        List<Event> events = dataCache.getEventsFromSearch("wrong");
        assertFalse(events.size() > 0);
    }
}
