package com.example.familymapclient.userinterface.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.familymapclient.R;
import com.example.familymapclient.application.DataCache;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import java.util.List;
import java.util.Set;
import main.models.Event;
import main.models.Person;

public class SearchActivity extends AppCompatActivity {
    private static final int LIFE_EVENTS_ITEM_VIEW_TYPE = 1;
    private static final int FAMILY_ITEM_VIEW_TYPE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        ImageView image = findViewById(R.id.search_bar_image);
        image.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_search).sizeDp(40));
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DataCache dataCache = DataCache.getInstance();
                List<Person> peopleSearch = dataCache.getPeopleFromSearch(s);
                List<Event> eventSearch = dataCache.getEventsFromSearch(s);

                dataCache.updateFilteredEvents();
                Set<Event> remainingEvents = dataCache.getFilteredEvents();
                eventSearch.removeIf(event -> !remainingEvents.contains(event));

                SearchAdapter adapter = new SearchAdapter(eventSearch, peopleSearch);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    private class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {
        private List<Event> lifeEvents;
        private List<Person> relatives;

        public SearchAdapter(List<Event> lifeEvents, List<Person> relatives) {
            this.lifeEvents = lifeEvents;
            this.relatives = relatives;
        }

        @Override
        public int getItemViewType(int position) {
            return position < relatives.size() ? FAMILY_ITEM_VIEW_TYPE : LIFE_EVENTS_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == LIFE_EVENTS_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.life_event_item, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.relative_item, parent, false);
            }
            return new SearchHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
            if (position < relatives.size()) {
                holder.bind(relatives.get(position));
            } else {
                holder.bind(lifeEvents.get(position - relatives.size()));
            }
        }

        @Override
        public int getItemCount() {
            return lifeEvents.size() + relatives.size();
        }
    }

    private class SearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final int viewType;
        private Event lifeEvent;
        private Person relative;
        private ImageView imageView;
        private TextView upperText;
        private TextView lowerText;
        private DataCache dataCache;

        public SearchHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            dataCache = DataCache.getInstance();

            itemView.setOnClickListener(this);
            if (viewType == LIFE_EVENTS_ITEM_VIEW_TYPE) {
                imageView = itemView.findViewById(R.id.life_event_image);
                upperText = itemView.findViewById(R.id.life_event_description);
                lowerText = itemView.findViewById(R.id.life_event_name);
            } else {
                imageView = itemView.findViewById(R.id.relative_image);
                upperText = itemView.findViewById(R.id.relative_name);
                lowerText = null;
            }
        }

        private void bind(Event lifeEvent) {
            this.lifeEvent = lifeEvent;
            imageView.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).sizeDp(40));
            String text = lifeEvent.getEventType().toUpperCase() + ": " + lifeEvent.getCity() + ", " + lifeEvent.getCountry() + " (" + lifeEvent.getYear() + ")";
            upperText.setText(text);
            Person person = dataCache.getPersonByID(lifeEvent.getPersonID());
            text = person.getFirstName() + " " + person.getLastName();
            lowerText.setText(text);
        }

        private void bind(Person relative) {
            this.relative = relative;
            if (relative.getGender().equalsIgnoreCase("m")) {
                imageView.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).colorRes(R.color.blue).sizeDp(40));
            } else {
                imageView.setImageDrawable(new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).colorRes(R.color.pink).sizeDp(40));
            }
            String text = relative.getFirstName() + " " + relative.getLastName();
            upperText.setText(text);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            if (viewType == LIFE_EVENTS_ITEM_VIEW_TYPE) {
                intent.putExtra("Event", lifeEvent.getEventID());
                intent.setClass(SearchActivity.this, EventActivity.class);
            } else {
                intent.putExtra("Person", relative.getPersonID());
                intent.setClass(SearchActivity.this, PersonActivity.class);
            }
            SearchActivity.this.startActivity(intent);
        }
    }
}