package com.example.musicapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.entities.User;
import com.example.musicapp.entities.UserActivityEvent;
import com.example.musicapp.views.ActivityEventViewHolder;
import com.example.musicapp.views.UserViewHolder;

import java.util.ArrayList;

public class ActivityEventAdapter extends RecyclerView.Adapter<ActivityEventViewHolder> {
    private ArrayList<UserActivityEvent> eventsList;

    Context context;


    public ActivityEventAdapter(ArrayList<UserActivityEvent> eventsList, Context context) {
        this.eventsList = eventsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ActivityEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_activity_event_item, parent, false);
        return new ActivityEventViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityEventViewHolder holder, int position) {
        UserActivityEvent event = eventsList.get(position);
        holder.eventTextView.setText(event.getEventDescription());
        holder.event = eventsList.get(position);
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}