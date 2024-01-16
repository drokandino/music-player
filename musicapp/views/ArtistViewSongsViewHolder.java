package com.example.musicapp.views;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.entities.DataSingleton;
import com.example.musicapp.R;
import com.example.musicapp.entities.Song;
import com.example.musicapp.activities.MusicPlayerActivity;
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

public class ArtistViewSongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {


    public TextView songName;
    String songFileUUID;
    private Context context;
    View view;

    MainActivity activity;
    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;
    FirebaseFirestore db;
    Song song;
    //String artistID;

    public ArtistViewSongsViewHolder(View itemView, Context context, MainActivity activity) {
        super(itemView);
        songName = (TextView) itemView.findViewById(R.id.artist_view_song_name);
        view = itemView;
        this.context = context;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        this.activity = activity;
        mediaBrowser = activity.getMediaBrowser();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View v) {

        MaterialCardView musicPlayerBar = activity.findViewById(R.id.music_player_bar);
        musicPlayerBar.setVisibility(View.VISIBLE);
        mediaController = MediaControllerCompat.getMediaController(activity);
        Log.d("CarouselViewHolder", "onClick: " + mediaController);
        for(Song songIt: DataSingleton.getDataSingleton().getAllSongs()){
            if(songIt.getSongFileUUID().equals(songFileUUID)){
                song = songIt;
            }
        }
        initializeData();
        crateEvent();
        updateNumOfListens();
    }

    @Override
    public boolean onLongClick(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.artist_song_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();


        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.artist_view_add_to_queue:
                addSongToQueue();
                return true;
            default:
                return false;
        }
    }

    private void initializeData() {
        ArrayList<Song> songList = new ArrayList<>();
        songList.add(song);

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
    public String getSongFileUUID() {
        return songFileUUID;
    }

    public void setSongFileUUID(String songFileUUID) {
        this.songFileUUID = songFileUUID;
    }


    private void addSongToQueue() {
        for (Song song : DataSingleton.getDataSingleton().getAllSongs()) {
            if (song.getSongFileUUID().equals(this.songFileUUID)) {
                DataSingleton.getDataSingleton().getSongsQueue().add(song);
            }
        }
        Log.d("Songqueue", DataSingleton.getDataSingleton().getSongsQueue().toString());
        Toast.makeText(context, "Added to playing queue", Toast.LENGTH_SHORT).show();
    }

    private void crateEvent(){
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("creator", DataSingleton.getDataSingleton().getCurrentUserID());
        eventData.put("type", "songPlay");
        eventData.put("description", DataSingleton.getDataSingleton().getCurrentUserName() + " played " + song.getSongName() + " song");

        UUID uuidEvent = UUID.randomUUID();
        String uuidEventString = uuidEvent.toString();
        CollectionReference events = db.collection("events");
        events.document(uuidEventString).set(eventData).addOnSuccessListener(aVoid -> {



            Toast.makeText(activity, "Event Created", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Log.w("TAG", "Error writing event", e));
    }

    private void updateNumOfListens(){
        DocumentReference docRef = db.collection("users/" + song.getArtistID()+"/songs").document(song.getSongID());

        String numOfListens = String.valueOf(song.getNumberOfListens() + 1);

        song.setNumberOfListens((int) song.getNumberOfListens() + 1);
        Map<String, Object> updates = new HashMap<>();
        updates.put("numberOfListens", numOfListens);
        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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

}
