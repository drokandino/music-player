package com.example.musicapp.views;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.entities.User;
import com.example.musicapp.entities.UserActivityEvent;

public class ActivityEventViewHolder extends  RecyclerView.ViewHolder {


    public TextView eventTextView;
    public UserActivityEvent event;
    Context context;  // Assuming you have access to these fragments in MainActivity.

    public ActivityEventViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        eventTextView = itemView.findViewById(R.id.event_description);
        this.context = context;


    }

}
