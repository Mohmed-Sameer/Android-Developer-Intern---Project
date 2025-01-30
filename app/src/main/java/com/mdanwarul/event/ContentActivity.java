package com.mdanwarul.event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity implements EventAdapter.OnEventActionListener {

    private RecyclerView eventRecyclerView498;
    private FloatingActionButton addEventButton498;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    private TextView welcomeTextView498;
    private ImageView emptyIcon498;
    private TextView emptyTextView498;

    private DatabaseReference databaseEvents;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    // Define the launcher
    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        String id = extras.getString("EVENT_ID");
                        String name = extras.getString("EVENT_NAME");
                        String date = extras.getString("EVENT_DATE");
                        String description = extras.getString("EVENT_DESCRIPTION");
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        Event event = new Event(id, name, date, description, userId);
                        updateEventList(event);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        eventRecyclerView498 = findViewById(R.id.event_recyclerview498);
        addEventButton498 = findViewById(R.id.add_event_button498);
        welcomeTextView498 = findViewById(R.id.welcome_text_view498);
        emptyIcon498 = findViewById(R.id.empty_icon498);
        emptyTextView498 = findViewById(R.id.empty_text_view498);
        TextView logoutTextView = findViewById(R.id.logout_text_view);

        // Set the onClickListener for the logout TextView
        logoutTextView.setOnClickListener(view -> {
            // Log out the current user
            FirebaseAuth.getInstance().signOut();

            // Redirect to the login screen

            Intent intent = new Intent(ContentActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Finish current activity to prevent going back to it
        });

        // Check if the user is logged in
        if (currentUser != null) {
            String username = currentUser.getDisplayName(); // Use getDisplayName() if available
            if (username == null) {
                username = currentUser.getEmail(); // Fallback to email if display name is not available
            }
            welcomeTextView498.setText(String.format("Welcome, %s!", username));

            // Fetch events after setting the welcome message
            fetchEvents(currentUser.getUid());
        }

        // Initialize event list and adapter
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this, this); // Pass this as the listener
        eventRecyclerView498.setLayoutManager(new LinearLayoutManager(this));
        eventRecyclerView498.setAdapter(eventAdapter);

        // Floating action button to add new event
        addEventButton498.setOnClickListener(view -> {
            Intent intent = new Intent(ContentActivity.this, AddEditEventActivity.class);
            resultLauncher.launch(intent);
        });

        // Check if event list is empty initially and update UI accordingly
        updateEmptyState();
    }

    private void fetchEvents(String userId) {
        databaseEvents = FirebaseDatabase.getInstance().getReference("events");

        // Listen for changes in the "events" node for the specified userId
        databaseEvents.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear(); // Clear the existing list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                        eventList.add(event); // Add each event to the list
                    }
                }
                Log.d("EventListSize", "Event list size: " + eventList.size());
                eventAdapter.notifyDataSetChanged();
                updateEmptyState();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ContentActivity.this, "Failed to load events."+databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEventList(Event newEvent) {
        boolean updated = false;
        for (int i = 0; i < eventList.size(); i++) {
            if (eventList.get(i).getId().equals(newEvent.getId())) {
                eventList.set(i, newEvent);
                updated = true;
                break;
            }
        }
        if (!updated) {
            eventList.add(newEvent);
        }
        eventAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        // Show empty state if no events are available
        if (eventList.isEmpty()) {
            emptyIcon498.setVisibility(View.VISIBLE);
            emptyTextView498.setVisibility(View.VISIBLE);
            eventRecyclerView498.setVisibility(View.GONE);
        } else {
            emptyIcon498.setVisibility(View.GONE);
            emptyTextView498.setVisibility(View.GONE);
            eventRecyclerView498.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditEvent(int position) {
        Event event = eventList.get(position);
        Intent intent = new Intent(ContentActivity.this, AddEditEventActivity.class);
        intent.putExtra("EVENT_ID", event.getId());
        intent.putExtra("EVENT_NAME", event.getName());  // Pass the event name
        intent.putExtra("EVENT_DATE", event.getDate());  // Pass the event date
        intent.putExtra("EVENT_DESCRIPTION", event.getDescription());  // Pass the event description
        startActivity(intent);
    }

    @Override
    public void onDeleteEvent(int position) {
        Event event = eventList.get(position);
        // Delete from Firebase
        databaseEvents.child(event.getId()).removeValue();
        // Remove locally and notify the adapter
        eventList.remove(position);
        eventAdapter.notifyItemRemoved(position);
        Toast.makeText(ContentActivity.this, "Event deleted.", Toast.LENGTH_SHORT).show();
    }
}
