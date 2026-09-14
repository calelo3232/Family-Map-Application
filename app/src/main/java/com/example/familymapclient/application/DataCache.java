package com.example.familymapclient.application;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import main.models.Event;
import main.models.Person;
import main.results.EventsResult;
import main.results.PersonsResult;

public class DataCache {
    private static DataCache instance = new DataCache();
    private Person userPerson;
    private Set<Person> userPeople;
    private Set<Event> userEvents;
    private Map<String, Person> personByID;
    private Map<String, Event> eventByID;
    private float colorHue;
    private Map<String, Float> colorsByEventType;
    private Set<Event> maleFilter;
    private Set<Event> femaleFilter;
    private Set<Event> paternalFilter;
    private Set<Event> maternalFilter;
    private Set<Person> paternalSide;
    private Set<Person> maternalSide;
    private Set<Event> totalFilteredEvents;
    private boolean lifeStorySwitch = true;
    private boolean familyTreeSwitch = true;
    private boolean spouseSwitch = true;
    private boolean paternalSwitch = true;
    private boolean maternalSwitch = true;
    private boolean maleSwitch = true;
    private boolean femaleSwitch = true;
    private boolean settingsChangedFlag = false;

    public static DataCache getInstance() {
        return instance;
    }

    private DataCache() {
    }

    public void initializeData(String userPersonID, PersonsResult people, EventsResult events) {
        personByID = new HashMap<>();
        eventByID = new HashMap<>();
        colorsByEventType = new HashMap<>();
        totalFilteredEvents = new HashSet<>();
        setPeopleData(people);
        userPerson = getPersonByID(userPersonID);
        setEventsData(events);
        colorHue = BitmapDescriptorFactory.HUE_RED;
    }

    private void setPeopleData(PersonsResult people) {
        ArrayList<Person> peopleList = people.getData();
        userPeople = new HashSet<>();
        for (Person person : peopleList) {
            userPeople.add(person);
            personByID.put(person.getPersonID(), person);
        }
    }

    private void setEventsData(EventsResult events) {
        ArrayList<Event> eventList = events.getData();
        userEvents = new HashSet<>();
        maleFilter = new HashSet<>();
        femaleFilter = new HashSet<>();
        for (Event event : eventList) {
            userEvents.add(event);
            eventByID.put(event.getEventID(), event);
            if (!colorsByEventType.containsKey(event.getEventType().toLowerCase())) {
                colorsByEventType.put(event.getEventType().toLowerCase(), colorHue);
                setNextColor();
            }
            if (Objects.equals(getPersonByID(event.getPersonID()).getGender(), "m")) {
                maleFilter.add(event);
            } else {
                femaleFilter.add(event);
            }
        }
        paternalFilter();
        maternalFilter();
        setEventFilter();
    }

    public List<Event> allEventsFromPersonID(String personID) {
        List<Event> events = new ArrayList<>();
        for (Event userEvent : userEvents) {
           if (Objects.equals(userEvent.getPersonID(), personID)) {
               events.add(userEvent);
           }
        }
        events.sort(Comparator.comparingInt(Event::getYear));
        return events;
    }

    public List<Person> allRelativesFromPersonID(String personID) {
        List<Person> people = new ArrayList<>();
        Person person = getPersonByID(personID);
        for (Person userPerson : userPeople) {
            String relationship = determineFamilialRelationship(userPerson, person);
            if (!Objects.equals(relationship, "Same person") && !relationship.equals("No relationship")) {
                people.add(userPerson);
            }
        }
        return people;
    }

    public String determineFamilialRelationship(Person person1, Person person2) { // returns "person2 is the _____ of person1"
        if (Objects.equals(person1.getPersonID(), person2.getPersonID())) {
            return "Same person";
        } else if (Objects.equals(person1.getPersonID(), person2.getFatherID()) || Objects.equals(person1.getPersonID(), person2.getMotherID())) {
            return "Child";
        } else if (Objects.equals(person1.getPersonID(), person2.getSpouseID())) {
            if (Objects.equals(person2.getGender(), "m")) {
                return "Husband";
            } else {
                return "Wife";
            }
        } else if (Objects.equals(person1.getFatherID(), person2.getPersonID())) {
            return "Father";
        } else if (Objects.equals(person1.getMotherID(), person2.getPersonID())) {
            return "Mother";
        } else {
            return "No relationship";
        }
    }

    public List<Event> getEventsFromSearch(CharSequence s) {
        String text = s.toString().toLowerCase();
        List<Event> events = new ArrayList<>();
        for (Event userEvent : userEvents) {
            if (userEvent.getCountry().toLowerCase().contains(text) || userEvent.getCity().toLowerCase().contains(text) || userEvent.getEventType().toLowerCase().contains(text) || Integer.toString(userEvent.getYear()).contains(s)) {
                events.add(userEvent);
            }
        }
        return events;
    }

    public List<Person> getPeopleFromSearch(CharSequence s) {
        String text = s.toString().toLowerCase();
        List<Person> people = new ArrayList<>();
        for (Person userPerson : userPeople) {
            if (userPerson.getFirstName().toLowerCase().contains(text) || userPerson.getLastName().toLowerCase().contains(text)) {
                people.add(userPerson);
            }
        }
        return people;
    }

    public Person getPersonByID(String personID) {
        return personByID.get(personID);
    }

    public Event getEventByID(String eventID) {
        return eventByID.get(eventID);
    }

    private void setNextColor() {
        if (colorHue >= 330.0F) {
            colorHue = 0.0F;
        } else {
            colorHue += 30.0F;
        }
    }

    public float getColorByEventType(String eventType) {
        return colorsByEventType.get(eventType.toLowerCase());
    }

    private void paternalFilter() {
        paternalSide = new HashSet<>();
        Person father = getPersonByID(userPerson.getFatherID());
        paternalSide.add(father);
        paternalSide.addAll(getAncestors(father));
    }

    private void maternalFilter() {
        maternalSide = new HashSet<>();
        Person mother = getPersonByID(userPerson.getMotherID());
        maternalSide.add(mother);
        maternalSide.addAll(getAncestors(mother));
    }

    private Set<Person> getAncestors(Person person) {
        Set<Person> ancestors = new HashSet<>();
        Person father = getPersonByID(person.getFatherID());
        Person mother = getPersonByID(person.getMotherID());
        if (father != null) {
            ancestors.add(father);
            ancestors.addAll(getAncestors(father));
        } if (mother != null) {
            ancestors.add(mother);
            ancestors.addAll(getAncestors(mother));
        }
        return ancestors;
    }

    private void setEventFilter() {
        paternalFilter = new HashSet<>();
        maternalFilter = new HashSet<>();
        for (Person person : paternalSide) {
            List<Event> events = allEventsFromPersonID(person.getPersonID());
            paternalFilter.addAll(events);
        }
        for (Person person : maternalSide) {
            List<Event> events = allEventsFromPersonID(person.getPersonID());
            maternalFilter.addAll(events);
        }
    }

    public void updateFilteredEvents() {
        totalFilteredEvents.clear();
        totalFilteredEvents.addAll(userEvents);
        if (!paternalSwitch)
            totalFilteredEvents.removeIf(paternalFilter::contains);
        if (!maternalSwitch)
            totalFilteredEvents.removeIf(maternalFilter::contains);
        if (!maleSwitch)
            totalFilteredEvents.removeIf(maleFilter::contains);
        if (!femaleSwitch)
            totalFilteredEvents.removeIf(femaleFilter::contains);
    }

    public Set<Event> getFilteredEvents() {
        return totalFilteredEvents;
    }

    public Set<Event> getMaleFilter() {
        return maleFilter;
    }

    public Set<Event> getFemaleFilter() {
        return femaleFilter;
    }

    public Set<Event> getPaternalFilter() {
        return paternalFilter;
    }

    public Set<Event> getMaternalFilter() {
        return maternalFilter;
    }

    public Set<Person> getPaternalSide() {
        return paternalSide;
    }

    public Set<Person> getMaternalSide() {
        return maternalSide;
    }

    public boolean isSettingsChangedFlag() {
        return settingsChangedFlag;
    }

    public void setSettingsChangedFlag(boolean settingsChangedFlag) {
        this.settingsChangedFlag = settingsChangedFlag;
    }

    public boolean isLifeStorySwitch() {
        return lifeStorySwitch;
    }

    public void setLifeStorySwitch(boolean lifeStorySwitch) {
        this.lifeStorySwitch = lifeStorySwitch;
    }

    public boolean isFamilyTreeSwitch() {
        return familyTreeSwitch;
    }

    public void setFamilyTreeSwitch(boolean familyTreeSwitch) {
        this.familyTreeSwitch = familyTreeSwitch;
    }

    public boolean isSpouseSwitch() {
        return spouseSwitch;
    }

    public void setSpouseSwitch(boolean spouseSwitch) {
        this.spouseSwitch = spouseSwitch;
    }

    public boolean isPaternalSwitch() {
        return paternalSwitch;
    }

    public void setPaternalSwitch(boolean paternalSwitch) {
        this.paternalSwitch = paternalSwitch;
    }

    public boolean isMaternalSwitch() {
        return maternalSwitch;
    }

    public void setMaternalSwitch(boolean maternalSwitch) {
        this.maternalSwitch = maternalSwitch;
    }

    public boolean isMaleSwitch() {
        return maleSwitch;
    }

    public void setMaleSwitch(boolean maleSwitch) {
        this.maleSwitch = maleSwitch;
    }

    public boolean isFemaleSwitch() {
        return femaleSwitch;
    }

    public void setFemaleSwitch(boolean femaleSwitch) {
        this.femaleSwitch = femaleSwitch;
    }
}
