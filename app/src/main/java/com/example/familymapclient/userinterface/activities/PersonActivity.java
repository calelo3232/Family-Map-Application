package com.example.familymapclient.userinterface.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.familymapclient.R;
import com.example.familymapclient.application.DataCache;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import main.models.Event;
import main.models.Person;

public class PersonActivity extends AppCompatActivity {
    private DataCache dataCache;
    private Person originalPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        dataCache = DataCache.getInstance();
        Intent intent = getIntent();
        originalPerson = dataCache.getPersonByID(intent.getStringExtra("Person"));

        TextView firstName = findViewById(R.id.firstNamePersonActivity);
        TextView lastName = findViewById(R.id.lastNamePersonActivity);
        TextView gender = findViewById(R.id.genderPersonActivity);
        firstName.setText(originalPerson.getFirstName());
        lastName.setText(originalPerson.getLastName());

        if (Objects.equals(originalPerson.getGender(), "m")) {
            gender.setText(R.string.male);
        } else {
            gender.setText(R.string.female);
        }
        List<Event> lifeEvents = dataCache.allEventsFromPersonID(originalPerson.getPersonID());
        List<Person> relatives = dataCache.allRelativesFromPersonID(originalPerson.getPersonID());

        dataCache.updateFilteredEvents();
        Set<Event> remainingEvents = dataCache.getFilteredEvents();
        lifeEvents.removeIf(event -> !remainingEvents.contains(event));

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        expandableListView.setAdapter(new ExpandableListAdapter(lifeEvents, relatives));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem .getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int LIFE_EVENTS_POSITION = 0;
        private static final int FAMILY_POSITION = 1;
        private List<Event> lifeEvents;
        private List<Person> relatives;

        public ExpandableListAdapter(List<Event> lifeEvents, List<Person> relatives) {
            this.lifeEvents = lifeEvents;
            this.relatives = relatives;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch(groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return lifeEvents.size();
                case FAMILY_POSITION:
                    return relatives.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch(groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return "Life Events";
                case FAMILY_POSITION:
                    return "Family";
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch(groupPosition) {
                case LIFE_EVENTS_POSITION:
                    return lifeEvents.get(childPosition);
                case FAMILY_POSITION:
                    return relatives.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }
            TextView titleView = convertView.findViewById(R.id.listTitle);
            switch(groupPosition) {
                case LIFE_EVENTS_POSITION:
                    titleView.setText(R.string.life_events);
                    break;
                case FAMILY_POSITION:
                    titleView.setText(R.string.family);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case LIFE_EVENTS_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.life_event_item, parent, false);
                    initializeLifeEventView(itemView, childPosition);
                    break;
                case FAMILY_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.relative_item, parent, false);
                    initializeRelativeView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
            return itemView;
        }

        private void initializeLifeEventView(View lifeEventItemView, final int childPosition) {
            Event event = lifeEvents.get(childPosition);
            ImageView imageView = lifeEventItemView.findViewById(R.id.life_event_image);
            TextView eventDescription = lifeEventItemView.findViewById(R.id.life_event_description);
            TextView name = lifeEventItemView.findViewById(R.id.life_event_name);

            imageView.setImageDrawable(new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).sizeDp(40));
            String text = event.getEventType().toUpperCase() + ": " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
            eventDescription.setText(text);
            Person person = dataCache.getPersonByID(event.getPersonID());
            text = person.getFirstName() + " " + person.getLastName();
            name.setText(text);
            lifeEventItemView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.putExtra("Event", event.getEventID());
                intent.setClass(PersonActivity.this, EventActivity.class);
                PersonActivity.this.startActivity(intent);
            });
        }

        private void initializeRelativeView(View relativeItemView, final int childPosition) {
            Person person = relatives.get(childPosition);
            ImageView imageView = relativeItemView.findViewById(R.id.relative_image);
            TextView name = relativeItemView.findViewById(R.id.relative_name);
            TextView relationship = relativeItemView.findViewById(R.id.relative_relationship);

            if (person.getGender().equalsIgnoreCase("m")) {
                imageView.setImageDrawable(new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(40));
            } else {
                imageView.setImageDrawable(new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).colorRes(R.color.pink).sizeDp(40));
            }
            String text = person.getFirstName() + " " + person.getLastName();
            name.setText(text);
            text = dataCache.determineFamilialRelationship(originalPerson, person);
            relationship.setText(text);
            relativeItemView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.putExtra("Person", person.getPersonID());
                intent.setClass(PersonActivity.this, PersonActivity.class);
                PersonActivity.this.startActivity(intent);
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}