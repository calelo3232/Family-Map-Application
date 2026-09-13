package com.example.familymapclient.userinterface.activities.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.familymapclient.R;
import com.example.familymapclient.application.DataCache;
import com.example.familymapclient.userinterface.activities.PersonActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import main.models.Event;
import main.models.Person;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private DataCache dataCache;
    private TextView textView;
    private ImageView imageView;
    private Polyline spouseLine;
    private Polyline lifeStoryLine;
    private List<Polyline> paternalLines = new ArrayList<>();
    private List<Polyline> maternalLines = new ArrayList<>();
    private Set<Event> visibleEvents;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        dataCache = DataCache.getInstance();
        textView = view.findViewById(R.id.mapTextView);
        imageView = view.findViewById(R.id.mapIconView);
        imageView.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_map_marker).sizeDp(60));
        textView.setTextSize(24);
        dataCache.updateFilteredEvents();
        visibleEvents = dataCache.getFilteredEvents();
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);
        setMap();
    }

    public void setMap() {
        map.clear();
        dataCache.updateFilteredEvents();
        visibleEvents = dataCache.getFilteredEvents();
        for (Event event : visibleEvents) {
            float googleColor = dataCache.getColorByEventType(event.getEventType());
            Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(event.getLatitude(), event.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(googleColor)));
            marker.setTag(event);
        }

        EventActivityHelper setEventActivity = event -> {
            map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(event.getLatitude(), event.getLongitude())));
            Person person = dataCache.getPersonByID(event.getPersonID());
            String text = person.getFirstName() + " " + person.getLastName() + "\n" + event.getEventType().toUpperCase() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
            textView.setText(text);
            textView.setTextSize(20);

            if (person.getGender().equalsIgnoreCase("m")) {
                imageView.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(60));
            } else {
                imageView.setImageDrawable(new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).colorRes(R.color.pink).sizeDp(60));
            }

            textView.setOnClickListener(v -> {
                Intent intentTemp = new Intent();
                intentTemp.putExtra("Person", person.getPersonID());
                intentTemp.setClass(getActivity(), PersonActivity.class);
                getActivity().startActivity(intentTemp);
            });

            clearLines();
            if (dataCache.isSpouseSwitch())
                drawSpouseLine(event);
            if (dataCache.isFamilyTreeSwitch())
                drawFamilyTreeLines(event, 18.0F);
            if (dataCache.isLifeStorySwitch())
                drawLifeStoryLine(event);
        };

        map.setOnMarkerClickListener(marker -> {
            Event event = (Event) marker.getTag();
            setEventActivity.setEventActivity(event);
            return false;
        });

        if (getArguments() != null) {
            Event event = dataCache.getEventByID(getArguments().getString("Event"));
            setEventActivity.setEventActivity(event);
        }
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onPause() {
        super.onPause();
        dataCache.setSettingsChangedFlag(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataCache.isSettingsChangedFlag()) {
            setMap();
        }
    }

    private interface EventActivityHelper {
        void setEventActivity(Event event);
    }

    private void drawSpouseLine(Event event) {
        Person person = dataCache.getPersonByID(event.getPersonID());
        if (person.getSpouseID() != null) {
            List<Event> events = dataCache.allEventsFromPersonID(person.getSpouseID());
            dataCache.updateFilteredEvents();
            visibleEvents = dataCache.getFilteredEvents();
            events.removeIf(filterEvent -> !visibleEvents.contains(filterEvent));
            if (!events.isEmpty()) {
                Event firstEvent = events.get(0);
                LatLng start = new LatLng(event.getLatitude(), event.getLongitude());
                LatLng end = new LatLng(firstEvent.getLatitude(), firstEvent.getLongitude());
                spouseLine = map.addPolyline(new PolylineOptions().add(start).add(end).color(Color.RED));
            }
        }
    }

    private void drawFamilyTreeLines(Event event, float lineWidth) {
        Person person = dataCache.getPersonByID(event.getPersonID());
        if (person.getFatherID() != null) {
            List<Event> events = dataCache.allEventsFromPersonID(person.getFatherID());
            dataCache.updateFilteredEvents();
            visibleEvents = dataCache.getFilteredEvents();
            events.removeIf(filterEvent -> !visibleEvents.contains(filterEvent));
            if (!events.isEmpty()) {
                Event fatherEvent = events.get(0);
                LatLng start = new LatLng(event.getLatitude(), event.getLongitude());
                LatLng end = new LatLng(fatherEvent.getLatitude(), fatherEvent.getLongitude());
                Polyline fatherLine = map.addPolyline(new PolylineOptions().add(start).add(end).width(lineWidth).color(Color.BLUE));
                paternalLines.add(fatherLine);
                paternalLines.addAll(drawParentalTreeLines(fatherEvent, lineWidth / 2));
            }
        }
        if (person.getMotherID() != null) {
            List<Event> events = dataCache.allEventsFromPersonID(person.getMotherID());
            dataCache.updateFilteredEvents();
            visibleEvents = dataCache.getFilteredEvents();
            events.removeIf(filterEvent -> !visibleEvents.contains(filterEvent));
            if (!events.isEmpty()) {
                Event motherEvent = events.get(0);
                LatLng start = new LatLng(event.getLatitude(), event.getLongitude());
                LatLng end = new LatLng(motherEvent.getLatitude(), motherEvent.getLongitude());
                Polyline motherLine = map.addPolyline(new PolylineOptions().add(start).add(end).width(lineWidth).color(Color.BLUE));
                maternalLines.add(motherLine);
                maternalLines.addAll(drawParentalTreeLines(motherEvent, lineWidth / 2));
            }
        }
    }

    private List<Polyline> drawParentalTreeLines(Event event, float lineWidth) {
        Person person = dataCache.getPersonByID(event.getPersonID());
        List<Polyline> lines = new ArrayList<>();
        if (person.getFatherID() != null) {
            List<Event> events = dataCache.allEventsFromPersonID(person.getFatherID());
            dataCache.updateFilteredEvents();
            visibleEvents = dataCache.getFilteredEvents();
            events.removeIf(filterEvent -> !visibleEvents.contains(filterEvent));
            if (!events.isEmpty()) {
                Event fatherEvent = events.get(0);
                LatLng start = new LatLng(event.getLatitude(), event.getLongitude());
                LatLng end = new LatLng(fatherEvent.getLatitude(), fatherEvent.getLongitude());
                Polyline fatherLine = map.addPolyline(new PolylineOptions().add(start).add(end).width(lineWidth).color(Color.BLUE));
                lines.add(fatherLine);
                lines.addAll(drawParentalTreeLines(fatherEvent, lineWidth / 2));
            }
        }
        if (person.getMotherID() != null) {
            List<Event> events = dataCache.allEventsFromPersonID(person.getMotherID());
            dataCache.updateFilteredEvents();
            visibleEvents = dataCache.getFilteredEvents();
            events.removeIf(filterEvent -> !visibleEvents.contains(filterEvent));
            if (!events.isEmpty()) {
                Event motherEvent = events.get(0);
                LatLng start = new LatLng(event.getLatitude(), event.getLongitude());
                LatLng end = new LatLng(motherEvent.getLatitude(), motherEvent.getLongitude());
                Polyline motherLine = map.addPolyline(new PolylineOptions().add(start).add(end).width(lineWidth).color(Color.BLUE));
                lines.add(motherLine);
                lines.addAll(drawParentalTreeLines(motherEvent, lineWidth / 2));
            }
        }
        return lines;
    }

    private void drawLifeStoryLine(Event event) {
        List<Event> events = dataCache.allEventsFromPersonID(event.getPersonID());
        dataCache.updateFilteredEvents();
        visibleEvents = dataCache.getFilteredEvents();
        events.removeIf(filterEvent -> !visibleEvents.contains(filterEvent));
        if (events.size() > 1) {
            List<LatLng> eventLocations = new ArrayList<>();
            for (Event lifeEvent : events) {
                eventLocations.add(new LatLng(lifeEvent.getLatitude(), lifeEvent.getLongitude()));
            }
            lifeStoryLine = map.addPolyline(new PolylineOptions().addAll(eventLocations).color(Color.GREEN));
        }
    }

    private void clearLines() {
        if (spouseLine != null) {
            spouseLine.remove();
        }
        if (lifeStoryLine != null) {
            lifeStoryLine.remove();
        }
        if (!paternalLines.isEmpty()) {
            for (Polyline line : paternalLines) {
                if (line != null) {
                    line.remove();
                }
            }
        }
        if (!maternalLines.isEmpty()) {
            for (Polyline line : maternalLines) {
                if (line != null) {
                    line.remove();
                }
            }
        }
    }
}