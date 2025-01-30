package com.mdanwarul.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton; // Changed from TextView
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private Context context;
    private OnEventActionListener actionListener;

    public interface OnEventActionListener {
        void onEditEvent(int position);
        void onDeleteEvent(int position);
    }

    public EventAdapter(List<Event> eventList, Context context, OnEventActionListener actionListener) {
        this.eventList = eventList;
        this.context = context;
        this.actionListener = actionListener;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventName.setText(event.getName());
        holder.eventDate.setText(event.getDate());
        holder.eventDescription.setText(event.getDescription());

        // Edit button click
        holder.editButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditEvent(position);
            }
        });

        // Delete button click
        holder.deleteButton.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteEvent(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView eventName, eventDate, eventDescription;
        ImageButton editButton, deleteButton; // Changed to ImageButton

        public EventViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.event_name_input498);
            eventDate = itemView.findViewById(R.id.event_date_input498);
            eventDescription = itemView.findViewById(R.id.event_description_input498);
            editButton = itemView.findViewById(R.id.edit_event_button); // ImageButton
            deleteButton = itemView.findViewById(R.id.delete_event_button); // ImageButton
        }
    }
}
