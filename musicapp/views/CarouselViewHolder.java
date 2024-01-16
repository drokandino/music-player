package com.example.musicapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.entities.Album;
import com.example.musicapp.entities.DataSingleton;
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

public class CarouselViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {

    public ImageView imageView;
    public TextView textView;
    public Song song;
    private MainActivity activity;
    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;
    FirebaseFirestore db;
    boolean eventCreated = false;

    public CarouselViewHolder(MainActivity activity, View itemView) {
        super(itemView);
        this.activity = activity;
        imageView = itemView.findViewById(R.id.image_view);
        textView = itemView.findViewById(R.id.text_view);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
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
        initializeData();
        crateEvent();
        updateNumOfListens();
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
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

    // skip to next runnable
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

    private void crateEvent(){
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("creator", DataSingleton.getDataSingleton().getCurrentUserID());
        eventData.put("type", "songPlay");
        eventData.put("description", DataSingleton.getDataSingleton().getCurrentUserName() + " played " + song.getSongName() + " song");

        UUID uuidEvent = UUID.randomUUID();
        String uuidEventString = uuidEvent.toString();
        CollectionReference events = db.collection("events");
        events.document(uuidEventString).set(eventData).addOnSuccessListener(aVoid -> {
            eventCreated = true;


            Toast.makeText(activity, "Event Created", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Log.w("TAG", "Error writing event", e));
    }
}
