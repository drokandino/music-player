package com.example.musicapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;

import com.example.musicapp.adapters.ActivityEventAdapter;
import com.example.musicapp.entities.DataSingleton;
import com.example.musicapp.entities.UserActivityEvent;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class SocialFragment extends Fragment {

    private RecyclerView activityFeedView;
    private ArrayList<UserActivityEvent> activityFeed;
    private LinearLayoutManager activityFeedManager;
    public Fragment calledFromFragment;
    private ActivityEventAdapter activityFeedAdapter;
    //private ActivityFeedAdapter activityFeedAdapter;

    public SocialFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_fragment_container, calledFromFragment)
                .commit());

        // Show all users button
        Button showAllUsersButton = view.findViewById(R.id.button_show_all_users);
        showAllUsersButton.setOnClickListener(v -> {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, activity.allUsersViewFragment)
                    .commit();
        });

        // Get activity feed
        getActivityFeed();

        // Set up the RecyclerView

        if(activityFeed!= null) {
            activityFeedView = view.findViewById(R.id.recyclerview_activity_feed);

            activityFeedManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            activityFeedView.setLayoutManager(activityFeedManager);
            activityFeedAdapter = new ActivityEventAdapter(activityFeed, getContext());
            activityFeedView.setAdapter(activityFeedAdapter);
        }

    }

    private void getActivityFeed() {
        activityFeed = DataSingleton.getDataSingleton().getAllEvents();

    }

    public Fragment getCalledFromFragment() {
        return calledFromFragment;
    }

    public void setCalledFromFragment(Fragment calledFromFragment) {
        this.calledFromFragment = calledFromFragment;
    }
}
