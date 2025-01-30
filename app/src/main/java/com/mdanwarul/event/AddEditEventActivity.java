package com.mdanwarul.event;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;

import java.util.Calendar;

public class AddEditEventActivity extends AppCompatActivity {

    private EditText eventNameEdit498, eventDateEdit498, eventDescriptionEdit498;
    private Button saveEventButton498;
    private DatabaseReference databaseEvents;
    private FirebaseUser currentUser;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase if not already done
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        setContentView(R.layout.activity_add_edit_event);

        // Initialize Firebase Authentication and Database reference
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseEvents = FirebaseDatabase.getInstance().getReference("events");

        // Ensure the user is logged in before proceeding
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to create or edit events.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI elements
        eventNameEdit498 = findViewById(R.id.event_name_input498);
        eventDateEdit498 = findViewById(R.id.event_date_input498);
        eventDescriptionEdit498 = findViewById(R.id.event_description_input498);
        saveEventButton498 = findViewById(R.id.save_event_button498);

        // Check if it's edit mode (i.e., eventId is passed from the previous screen)
        if (getIntent().hasExtra("EVENT_ID")) {
            eventId = getIntent().getStringExtra("EVENT_ID");
            eventNameEdit498.setText(getIntent().getStringExtra("EVENT_NAME"));  // Set the event name
            eventDateEdit498.setText(getIntent().getStringExtra("EVENT_DATE"));  // Set the event date
            eventDescriptionEdit498.setText(getIntent().getStringExtra("EVENT_DESCRIPTION"));  // Set the event description
        }

        // Show Date Picker when the event date EditText is clicked
        eventDateEdit498.setOnClickListener(v -> showDatePicker());

        // Save event to Firebase when the save button is clicked
        saveEventButton498.setOnClickListener(v -> saveEventToFirebase());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
            eventDateEdit498.setText(selectedDate);
        }, year, month, day).show();
    }

    private void saveEventToFirebase() {
        String name = eventNameEdit498.getText().toString().trim();
        String date = eventDateEdit498.getText().toString().trim();
        String description = eventDescriptionEdit498.getText().toString().trim();

        // Check for empty fields
        if (name.isEmpty() || date.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a new event ID if it's a new event (edit mode will have eventId passed)
        if (eventId == null) {
            eventId = databaseEvents.push().getKey();  // Generate unique key for the event
        }

        // Create the event object with the current user's ID
        Event event = new Event(eventId, name, date, description, currentUser.getUid());

        // Save the event object to Firebase under "events" node
        databaseEvents.child(eventId).setValue(event)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Event saved successfully!", Toast.LENGTH_SHORT).show();

                        // After saving, navigate back to ContentActivity with the flags to prevent back navigation
                        Intent intent = new Intent(AddEditEventActivity.this, ContentActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();  // Close the current activity
                    } else {
                        // Log the error to help with debugging
                        Toast.makeText(this, "Failed to save event. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
