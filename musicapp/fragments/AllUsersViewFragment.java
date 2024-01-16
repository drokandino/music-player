package com.example.musicapp.fragments;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.adapters.ArtistViewAlbumsAdapter;
import com.example.musicapp.adapters.UserViewAdapter;
import com.example.musicapp.entities.DataSingleton;
import com.example.musicapp.entities.User;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class AllUsersViewFragment extends Fragment {
    private RecyclerView usersRecyclerView;
    private UserViewAdapter usersAdapter;
    private ArrayList<User> usersList;
    public FragmentManager fragmentManager;
    MainActivity activity;


    public AllUsersViewFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_allusers_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        assert activity != null;

        fragmentManager = getChildFragmentManager();

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, activity.socialFragment)
                .commit());

        // Initialize usersList here (or fetch the list from a data source)
        usersList = new ArrayList<>();
        getAllUsers();
        usersRecyclerView = view.findViewById(R.id.recyclerview_all_users);
        usersAdapter = new UserViewAdapter(usersList, activity, this);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        usersRecyclerView.setAdapter(usersAdapter);


    }

    public void getAllUsers(){
        usersList = new ArrayList<>();
        ArrayList<User> usersListTmp = DataSingleton.getDataSingleton().getAllUsers();
        for(User user: usersListTmp){
            if(!user.isArtist()){
                usersList.add(user);
            }
        }
    }
}