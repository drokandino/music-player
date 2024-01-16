package com.example.musicapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.adapters.PlaylistCreateViewAdapter;
import com.example.musicapp.R;
import com.example.musicapp.entities.DataSingleton;
import com.example.musicapp.entities.Playlist;
import com.example.musicapp.entities.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaylistCreateFragment extends Fragment {


    private RecyclerView allSongsView;
    private LinearLayoutManager allSongsViewManager;
    private PlaylistCreateViewAdapter songsViewAdapter;
    private Button createPlaylistBtn;
    private EditText playlistName;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CheckBox privateCheckbox;

    public Fragment calledFromFragment;
    public PlaylistCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        MaterialToolbar toolbar = view.findViewById(R.id.fragment_create_playlist_top_bar);
        toolbar.setNavigationOnClickListener(v -> activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, calledFromFragment)
                .commit());

        privateCheckbox = view.findViewById(R.id.private_checkbox);
        playlistName = view.findViewById(R.id.playlist_name);
        createPlaylistBtn = view.findViewById(R.id.create_playlist_button);
        createPlaylistBtn.setOnClickListener(v -> {
            Log.d("createPlaylist", DataSingleton.getDataSingleton().getPlaylistCreateSongs().toString());

            // create playlist object
            Playlist playlist = new Playlist();
            playlist.setPlaylistSongs(DataSingleton.getDataSingleton().getPlaylistCreateSongs());
            playlist.setPlaylistName(playlistName.getText().toString());
            playlist.setCreatorID(DataSingleton.getDataSingleton().getCurrentUserID());
            playlist.setCreatorName(DataSingleton.getDataSingleton().getCurrentUserName());


            //UPLOAD TO BACKEND
            // Create UUID
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();

            Map<String, Object> playlistData = new HashMap<>();
            playlistData.put("name", playlist.getPlaylistName());
            playlistData.put("creatorName", playlist.getCreatorName());
            playlistData.put("creatorID", playlist.getCreatorID());
            playlistData.put("songs", playlist.getSongsIDs());
            playlistData.put("private", privateCheckbox.isChecked());

            CollectionReference playlistColl = db.collection("playlist");
            playlistColl.document(uuidString).set(playlistData).addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Playlist Created", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));

            // Clear tmp array for playlist creation
            DataSingleton.getDataSingleton().playlistCreateSongs = new ArrayList<>();




            Map<String, Object> eventData = new HashMap<>();
            eventData.put("creator", DataSingleton.getDataSingleton().getCurrentUserID());
            eventData.put("type", "playlistCreation");
            eventData.put("description", DataSingleton.getDataSingleton().getCurrentUserName() + " created " + playlist.getPlaylistName() + " playlist");

            UUID uuidEvent = UUID.randomUUID();
            String uuidEventString = uuidEvent.toString();
            CollectionReference events = db.collection("events");
            events.document(uuidEventString).set(eventData).addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Event Created", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> Log.w("TAG", "Error writing event", e));
            activity.getCurrentUserData();
            // Refresh backend data
            activity.getAllBackendData();

            }
        );

        allSongsView = view.findViewById(R.id.playlist_create_fragment_all_songs_view);
        allSongsViewManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        allSongsView.setLayoutManager(allSongsViewManager);
        songsViewAdapter = new PlaylistCreateViewAdapter(getContext(), DataSingleton.getDataSingleton().getAllSongs());
        allSongsView.setAdapter(songsViewAdapter);


        DataSingleton.getDataSingleton().playlistCreateSongs = new ArrayList<>();

    }

    public Fragment getCalledFromFragment() {
        return calledFromFragment;
    }

    public void setCalledFromFragment(Fragment calledFromFragment) {
        this.calledFromFragment = calledFromFragment;
    }
}