package com.example.musicapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.entities.DataSingleton;
import com.example.musicapp.entities.SearchElement;
import com.example.musicapp.entities.Song;
import com.example.musicapp.services.MusicPlayerService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView elementName;
    public TextView elementType;
    public SearchElement searchElement;
    View view;

    private MainActivity activity;
    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;
    FirebaseFirestore db;

    public SearchViewHolder(View itemView, MainActivity activity) {
        super(itemView);
        elementName = (TextView) itemView.findViewById(R.id.search_element_name);
        elementType = (TextView) itemView.findViewById(R.id.search_element_type);
        view = itemView;
        itemView.setOnClickListener(this);
        this.activity = activity;
        mediaBrowser = activity.getMediaBrowser();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public void onClick(View view) {
        // send song to MusicPlayerService
        MaterialCardView musicPlayerBar = activity.findViewById(R.id.music_player_bar);
        musicPlayerBar.setVisibility(View.VISIBLE);
        mediaController = MediaControllerCompat.getMediaController(activity);
        Log.d("CarouselViewHolder", "onClick: " + mediaController);
        updateNumOfListens();
        initializeData();
    }

    private void initializeData() {
        ArrayList<Song> songList = new ArrayList<>();

        if(searchElement.getElementType().equals("Playlist")){
            songList = searchElement.getPlaylist().getPlaylistSongs();
            Log.d("playlista", songList.toString());
        }else if (searchElement.getElementType().equals("Album")){
            String albumID = searchElement.getAlbum().getAlbumID();
            for(Song song: DataSingleton.getDataSingleton().getAllSongs()){
                if (song.getAlbumUUDI().equals(albumID)){
                    songList.add(song);
                }
            }
            Log.d("album", songList.toString());

        }else if (searchElement.getElementType().equals("Song")){
            songList.add(searchElement.getSong());
            Log.d("song", songList.toString());
        }

        crateEvent(searchElement.getElementType());


        Intent startServiceIntent = new Intent(activity, MusicPlayerService.class);
//        startServiceIntent.putExtra("songList", songList);

        Bundle songBundle = new Bundle();
        songBundle.putParcelableArrayList("songList", songList);
        mediaBrowser.subscribe("media", songBundle, new MediaBrowserCompat.SubscriptionCallback() {
            @Override
            public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
                super.onChildrenLoaded(parentId, children);
            }
        });

        activity.startService(startServiceIntent);

//        handler.post(skipToNext);
    }

    private Handler handler = new Handler();
    private Runnable skipToNext = new Runnable() {
        @Override
        public void run() {
            if (mediaController != null) {
                if (mediaController.getPlaybackState().getState() != PlaybackStateCompat.STATE_NONE) {
                    mediaController.getTransportControls().skipToNext();
                } else {
                    mediaController.getTransportControls().play();
                }
            } else {
                handler.postDelayed(skipToNext, 100);
            }
        }
    };

    private void updateNumOfListens(){

        if(searchElement.getElementType().equals("Playlist")){
            ArrayList<Song> songList = searchElement.getPlaylist().getPlaylistSongs();
            for(Song song: songList){
                DocumentReference docRef = db.collection("users/" + song.getArtistID()+"/songs").document(song.getSongID());
                String numOfListens = String.valueOf(song.getNumberOfListens() + 1);
                Map<String, Object> updates = new HashMap<>();
                updates.put("numberOfListens", numOfListens);

                docRef.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }

        }else if (searchElement.getElementType().equals("Album")){
            String albumID = searchElement.getAlbum().getAlbumID();
            for(Song song: DataSingleton.getDataSingleton().getAllSongs()){
                if (song.getAlbumUUDI().equals(albumID)){
                    DocumentReference docRef = db.collection("users/" + song.getArtistID()+"/songs").document(song.getSongID());
                    String numOfListens = String.valueOf(song.getNumberOfListens() + 1);
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("numberOfListens", numOfListens);

                    docRef.update(updates)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            }
        }else if (searchElement.getElementType().equals("Song")){
            DocumentReference docRef = db.collection("users/" + searchElement.getSong().getArtistID()+"/songs").document(searchElement.getSong().getSongID());

            String numOfListens = String.valueOf(searchElement.getSong().getNumberOfListens() + 1);

            Map<String, Object> updates = new HashMap<>();
            updates.put("numberOfListens", numOfListens);

            docRef.update(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }



    }

    private void crateEvent(String type){
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("creator", DataSingleton.getDataSingleton().getCurrentUserID());

        if(type.equals("Playlist")){
            eventData.put("type", "playlistPlay");
            eventData.put("description", DataSingleton.getDataSingleton().getCurrentUserName() + " played " + searchElement.getPlaylist().getPlaylistName() + " playlist");
        }else if (type.equals("Album")){
            eventData.put("type", "albumPlay");
            eventData.put("description", DataSingleton.getDataSingleton().getCurrentUserName() + " played " + searchElement.getAlbum().getAlbumName() + " album");

        }else if (type.equals("Song")){
            eventData.put("type", "songPlay");
            eventData.put("description", DataSingleton.getDataSingleton().getCurrentUserName() + " played " + searchElement.getSong().getSongName() + " song");
        }


        UUID uuidEvent = UUID.randomUUID();
        String uuidEventString = uuidEvent.toString();
        CollectionReference events = db.collection("events");
        events.document(uuidEventString).set(eventData).addOnSuccessListener(aVoid -> {
            // Refresh backend data
            activity.getCurrentUserData();
            activity.getAllBackendData();


            Toast.makeText(activity, "Event Created", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Log.w("TAG", "Error writing event", e));
    }


}