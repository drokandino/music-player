package com.example.musicapp.fragments;

import android.content.Context;
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

import com.example.musicapp.adapters.UserViewPlaylistAdapter;
import com.example.musicapp.entities.Album;
import com.example.musicapp.entities.DataSingleton;
import com.example.musicapp.R;
import com.example.musicapp.entities.Song;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.adapters.ArtistViewAlbumsAdapter;
import com.example.musicapp.adapters.ArtistViewSongsAdapter;
import com.example.musicapp.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArtistViewFragment extends Fragment {


    public TextView name, bio, follwingInfo;

    protected FirebaseAuth auth;
    private ArrayList<Song> songs;
    private ArrayList<Album> albums;
    public String artistName, artistBio, artistID;
    RecyclerView songsView, albumsView;
    LinearLayoutManager songsViewManager, albumViewManager;
    ArtistViewSongsAdapter songsViewAdapter;
    ArtistViewAlbumsAdapter albumViewAdapter;
    Button followBtn;
    Fragment calledFromFragment;
    FirebaseStorage storage;
    private StorageReference storageRef;
    private boolean isArtistFollowed;
    MainActivity activity;
    public ArtistViewFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        Log.d("artistLife", "onAttach" + " " + artistID);
        getSongsAndAlbums();
        getNameAndBio();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("artistLife", "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Log.d("artistLife", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        activity = (MainActivity) getActivity();
        assert activity != null;

        MaterialToolbar toolbar = view.findViewById(R.id.artist_profile_top_bar);
        toolbar.setNavigationOnClickListener(v -> {


            activity.getSupportFragmentManager()
                .beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.fade_out)
                .replace(R.id.main_fragment_container, calledFromFragment)
                .commit();
                });

        name = view.findViewById(R.id.artist_profile_artist_name);
        bio = view.findViewById(R.id.artist_profile_artist_bio);
        follwingInfo = view.findViewById(R.id.artist_profile_follow_info);
        followBtn = view.findViewById(R.id.artist_follow_button);
        getNameAndBio();
        getFollowInfo();
        name.setText(artistName);
        bio.setText(artistBio);
        if(isArtistFollowed){
            follwingInfo.setText("Following");
            followBtn.setText("Unfollow");
        }else {
            follwingInfo.setText("Not following");
            followBtn.setText("follow");
        }


        getSongsAndAlbums();


        songsView = view.findViewById(R.id.artist_songs_recycler_view);
        songsViewManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        songsView.setLayoutManager(songsViewManager);
        songsViewAdapter = new ArtistViewSongsAdapter(getContext(), songs, activity);
        songsView.setAdapter(songsViewAdapter);

        albumsView = view.findViewById(R.id.artist_albums_recycler_view);
        albumViewManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        albumsView.setLayoutManager(albumViewManager);
        albumViewAdapter = new ArtistViewAlbumsAdapter(getContext(), albums, activity);
        albumsView.setAdapter(albumViewAdapter);


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        followBtn.setOnClickListener(v -> {
                    DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document(DataSingleton.getDataSingleton().getCurrentUserID());

                    Map<String, Object> updates = new HashMap<>();
                    ArrayList<String> currentUserFollowingIDs = DataSingleton.getDataSingleton().getCurrentUserFollowingIDs();
                    //Log.d("follow", "singleton list " + currentUserFollowingIDs.toString());

                    Map<String, Object> eventData = new HashMap<>();
                    eventData.put("creator", DataSingleton.getDataSingleton().getCurrentUserID());
                    eventData.put("type", "follow");

                    if(isArtistFollowed){
                        ArrayList<String> newCurrUserFollowing = new ArrayList<>();
                        for(String currUserFollowing: currentUserFollowingIDs){
                            if(!currUserFollowing.equals(artistID)){
                                newCurrUserFollowing.add(currUserFollowing);
                            }
                        }
                        DataSingleton.getDataSingleton().setCurrentUserFollowingIDs(newCurrUserFollowing);
                        updates.put("following", newCurrUserFollowing);

                        eventData.put("description", DataSingleton.getDataSingleton().getCurrentUserName() + " stopped following " + artistName);

                    }else {
                        if(currentUserFollowingIDs != null){
                            currentUserFollowingIDs.add(artistID);
                        }else {
                            currentUserFollowingIDs = new ArrayList<>();
                            currentUserFollowingIDs.add(artistID);
                        }
                        DataSingleton.getDataSingleton().setCurrentUserFollowingIDs(currentUserFollowingIDs);
                        updates.put("following", currentUserFollowingIDs);
                        eventData.put("description", DataSingleton.getDataSingleton().getCurrentUserName() + " started following " + artistName);

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


    @Override
    public void onDetach() {
        super.onDetach();

        songs.clear();
        albums.clear();
        name = null;
        bio = null;
    }

    private void updateUI(){
        if(isArtistFollowed){
            follwingInfo.setText("Following");
            followBtn.setText("Unfollow");
        }else {
            follwingInfo.setText("Not following");
            followBtn.setText("follow");
        }
    }

    private void getSongsAndAlbums() {
        songs = new ArrayList<>();
        albums = new ArrayList<>();

        for (Song song : DataSingleton.getDataSingleton().getAllSongs()) {
            if (song.getArtistID().equals(artistID)) {
                songs.add(song);
            }
        }

        for (Album album : DataSingleton.getDataSingleton().getAllAlbums()) {
            if (album.getArtistID().equals(artistID)) {
                albums.add(album);
            }
        }
    }

    private void getNameAndBio() {
        for (Song song : DataSingleton.getDataSingleton().getAllSongs()) {
            if (song.getArtistID().equals(this.artistID)) {
                artistName = song.getArtistName();
                artistBio = song.getArtistBio();
            }
        }
    }

    public void setArtistID(String artistID) {
        this.artistID = artistID;

        if(name != null){
            getNameAndBio();
            getSongsAndAlbums();
            name.setText(artistName);
            bio.setText(artistBio);
            albumViewAdapter = new ArtistViewAlbumsAdapter(getContext(), albums, activity);
            songsViewAdapter = new ArtistViewSongsAdapter(getContext(), songs, activity);

        }else {
            Log.d("artistAllusers", "name == null");
        }

    }

    private void getFollowInfo(){
        isArtistFollowed = false;
        ArrayList<String> currUserFollowing = DataSingleton.getDataSingleton().getCurrentUserFollowingIDs();
        if(currUserFollowing != null){
            for(String follow: currUserFollowing){
                if(follow.equals(artistID)){
                    isArtistFollowed = true;
                }
            }
        }

    }

    public Fragment getCalledFromFragment() {
        return calledFromFragment;
    }

    public void setCalledFromFragment(Fragment calledFromFragment) {
        this.calledFromFragment = calledFromFragment;
    }
}
