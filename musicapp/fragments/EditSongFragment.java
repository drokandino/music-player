package com.example.musicapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.adapters.UserViewPlaylistAdapter;
import com.example.musicapp.entities.Album;
import com.example.musicapp.entities.DataSingleton;
import com.example.musicapp.entities.Playlist;
import com.example.musicapp.entities.Song;
import com.example.musicapp.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditSongFragment extends Fragment {

    EditText songNameEditText;
    Spinner genreSpinner, albumSpinner;
    Button confirmBtn;
    public  Song song;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_song, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        MaterialToolbar toolbar = view.findViewById(R.id.top_bar);
        toolbar.setNavigationOnClickListener(v -> activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_fragment_container, activity.libraryFragment)
                .commit());

        songNameEditText = view.findViewById(R.id.frag_song_edit_name);
        genreSpinner = view.findViewById(R.id.frag_song_edit_genre);
        albumSpinner = view.findViewById(R.id.frag_song_edit_album);
        confirmBtn = view.findViewById(R.id.frag_edit_song_confirm);

        songNameEditText.setText(song.getSongName());

        ArrayList<String> allAlbumNames = new ArrayList<>();
        for(Album album: DataSingleton.getDataSingleton().getAllAlbums()){
            if(album.getArtistID().equals(DataSingleton.getDataSingleton().getCurrentUserID())){
                if(!allAlbumNames.contains(album.getAlbumName())){
                    allAlbumNames.add(album.getAlbumName());
                }

            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, allAlbumNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        albumSpinner.setAdapter(arrayAdapter);

        confirmBtn.setOnClickListener(v -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    DocumentReference docRef = db.collection("users/" + DataSingleton.getDataSingleton().getCurrentUserID()+"/songs").document(song.getSongID());

                    String selectedAlbumID = null;
                    for(Album album: DataSingleton.getDataSingleton().getAllAlbums()){
                        if(album.getAlbumName().equals(albumSpinner.getSelectedItem().toString())){
                            selectedAlbumID = album.getAlbumID();
                            break;
                        }
                    }

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("album", selectedAlbumID);
                    updates.put("genre", genreSpinner.getSelectedItem().toString());
                    updates.put("songName", songNameEditText.getText().toString());

                    docRef.update(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(activity, "Song updated", Toast.LENGTH_SHORT).show();
                                    activity.getCurrentUserData();
                                    activity.getAllBackendData();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });


                }
        );



    }


}
