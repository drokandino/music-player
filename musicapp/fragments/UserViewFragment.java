package com.example.musicapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.adapters.ArtistViewAlbumsAdapter;
import com.example.musicapp.adapters.ArtistViewSongsAdapter;
import com.example.musicapp.adapters.UserViewPlaylistAdapter;
import com.example.musicapp.entities.DataSingleton;
import com.example.musicapp.entities.Playlist;
import com.example.musicapp.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserViewFragment extends Fragment {

    public TextView name, bio, followInfo;
    private RecyclerView playlistView;
    private ArrayList<Playlist> playlists;
    RecyclerView playlistsView;
    LinearLayoutManager playlistManager;
    UserViewPlaylistAdapter playlistAdapter;
    public User user;
    public UserViewFragment(){}
    boolean isUserFollowed;
    Button followBtn;
    MainActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         activity = (MainActivity) getActivity();
        assert activity != null;

        MaterialToolbar toolbar = view.findViewById(R.id.artist_profile_top_bar);
        toolbar.setNavigationOnClickListener(v -> activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_fragment_container, activity.allUsersViewFragment)
                .commit());


        name = view.findViewById(R.id.user_profile_user_name);
        bio = view.findViewById(R.id.user_profile_user_bio);
        followInfo = view.findViewById(R.id.user_profile_follow_info);
        followBtn = view.findViewById(R.id.follow_button);
        getFollowInfo();
        updateUI();
        name.setText(user.getUserName());
        bio.setText(user.getUserBio());


        getPlaylists();



        playlistView = view.findViewById(R.id.users_playlists_recycler_view);
        playlistManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        playlistView.setLayoutManager(playlistManager);
        playlistAdapter = new UserViewPlaylistAdapter(getContext(), playlists, activity);
        playlistView.setAdapter(playlistAdapter);

        followBtn.setOnClickListener(v -> {
                    DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document(DataSingleton.getDataSingleton().getCurrentUserID());

                    Map<String, Object> updates = new HashMap<>();
                    ArrayList<String> currentUserFollowingIDs = DataSingleton.getDataSingleton().getCurrentUserFollowingIDs();
                    //Log.d("follow", "singleton list " + currentUserFollowingIDs.toString());

                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("creator", DataSingleton.getDataSingleton().getCurrentUserID());
                    eventData.put("type", "follow");


                    if(isUserFollowed){
                        ArrayList<String> newCurrUserFollowing = new ArrayList<>();
                        for(String currUserFollowing: currentUserFollowingIDs){
                            if(!currUserFollowing.equals(user.getUserID())){
                                newCurrUserFollowing.add(currUserFollowing);
                            }
                        }
                        DataSingleton.getDataSingleton().setCurrentUserFollowingIDs(newCurrUserFollowing);
                        updates.put("following", newCurrUserFollowing);

                        eventData.put("description", DataSingleton.getDataSingleton().getCurrentUserName() + " stopped following " + user.getUserName());


                    }else {
                        if(currentUserFollowingIDs != null){
                            currentUserFollowingIDs.add(user.getUserID());
                        }else {
                            currentUserFollowingIDs = new ArrayList<>();
                            currentUserFollowingIDs.add(user.getUserID());
                        }
                        DataSingleton.getDataSingleton().setCurrentUserFollowingIDs(currentUserFollowingIDs);
                        updates.put("following", currentUserFollowingIDs);

                        eventData.put("description", DataSingleton.getDataSingleton().getCurrentUserName() + " started following " + user.getUserName());

                    }

                    getFollowInfo();
                    updateUI();

                    UUID uuidEvent = UUID.randomUUID();
                    String uuidEventString = uuidEvent.toString();
                    CollectionReference events = FirebaseFirestore.getInstance().collection("events");
                    events.document(uuidEventString).set(eventData).addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Event Created", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> Log.w("TAG", "Error writing event", e));

                    Log.d("follow", "current list " + currentUserFollowingIDs.toString());

                    userDoc.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                activity.getCurrentUserData();
                                activity.getAllBackendData();
                            } else {

                            }
                        }
                    });
                }
        );



    }

    private  void  getPlaylists() {
        playlists = user.getPlaylists();
    }

    public void setUser(String userID){
        ArrayList<User> allUsers = DataSingleton.getDataSingleton().getAllUsers();
        for(User user: allUsers){
            if (user.getUserID().equals(userID)){
                this.user = user;
                break;
            }
        }
        playlistAdapter = new UserViewPlaylistAdapter(getContext(), user.getPlaylists(), activity);
        name.setText(user.getUserName());
        bio.setText(user.getUserBio());
    }

    private void getFollowInfo(){
        isUserFollowed = false;
        ArrayList<String> currUserFollowing = DataSingleton.getDataSingleton().getCurrentUserFollowingIDs();
        if(currUserFollowing != null){

            for(String follow: currUserFollowing){
                if(follow.equals(user.getUserID())){
                    isUserFollowed = true;
                }
            }
        }
    }
    private void updateUI(){
        if(isUserFollowed){
            followInfo.setText("Following");
            followBtn.setText("Unfollow");
        }else {
            followInfo.setText("Not following");
            followBtn.setText("follow");
        }
    }
}
