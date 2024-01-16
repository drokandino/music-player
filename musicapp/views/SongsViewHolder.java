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

import com.example.musicapp.entities.DataSingleton;
import com.example.musicapp.R;
import com.example.musicapp.entities.Song;
import com.example.musicapp.activities.MainActivity;
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

public class SongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {
    public TextView songName;
    public TextView artistName;
    public TextView numberOfPlays;
    String songFileUUID;
    private Context context;
    View view;
    MainActivity activity;
    String artistID;
    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;
    private Song holderSong;
    FirebaseFirestore db;

    public SongsViewHolder(View itemView, Context context, MainActivity activity) {
        super(itemView);
        songName = (TextView) itemView.findViewById(R.id.library_song_name);
        artistName = (TextView) itemView.findViewById(R.id.library_artist_name);
        numberOfPlays = (TextView) itemView.findViewById(R.id.library_song_view_num_of_plays);
        //Log.d("holder", numberOfLikes.getText().toString());
        view = itemView;
        this.context = context;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        this.activity = activity;
        mediaBrowser = activity.getMediaBrowser();
        db = FirebaseFirestore.getInstance();
    }

    public SongsViewHolder(View itemView, Context context){
        super(itemView);
        this.context = context;
        songName = (TextView)itemView.findViewById(R.id.library_song_name);
        artistName = (TextView)itemView.findViewById(R.id.library_artist_name);

    }

    @Override
    public void onClick(View v) {
        // send song to MusicPlayerService
        MaterialCardView musicPlayerBar = activity.findViewById(R.id.music_player_bar);
        musicPlayerBar.setVisibility(View.VISIBLE);
        mediaController = MediaControllerCompat.getMediaController(activity);
        Log.d("CarouselViewHolder", "onClick: " + mediaController);
        initializeData();
        crateEvent();
        updateNumOfListens();

    }

    private void initializeData() {
        ArrayList<Song> songList = new ArrayList<>();

        for(Song song: DataSingleton.getDataSingleton().getAllSongs()){
            if(songFileUUID.equals(song.getSongFileUUID())){
                songList.add(song);
                holderSong = song;
                break;
            }
        }

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

    @Override
    public boolean onLongClick(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.song_menu, popup.getMenu());
        if(artistID.equals(DataSingleton.getDataSingleton().getCurrentUserID())){
            MenuItem editItem = popup.getMenu().findItem(R.id.edit_song);
            editItem.setVisible(true);
        }else {
            MenuItem editItem = popup.getMenu().findItem(R.id.edit_song);
            editItem.setVisible(false);
        }
        popup.setOnMenuItemClickListener(this);
        popup.show();


        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_to_queue:
                addSongToQueue();
                return true;
            case R.id.artist_profile:
                activity.artistViewFragment.setArtistID(artistID);
                activity.artistViewFragment.setCalledFromFragment(activity.libraryFragment);
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, activity.artistViewFragment)
                        .commit();
                return true;
            case R.id.edit_song:
                for(Song song: DataSingleton.getDataSingleton().getAllSongs()){
                    if(song.getSongFileUUID().equals(songFileUUID)){
                        activity.editSongFragment.song = song;
                    }
                }
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, activity.editSongFragment)
                        .commit();
                return true;
            default:
                return false;
        }
    }

    public String getSongFileUUID() {
        return songFileUUID;
    }

    public void setSongFileUUID(String songFileUUID) {
        this.songFileUUID = songFileUUID;
    }

    public void setArtistID(String artistID) {
        this.artistID = artistID;
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
        eventData.put("description", DataSingleton.getDataSingleton().getCurrentUserName() + " played " + holderSong.getSongName() + " song");

        UUID uuidEvent = UUID.randomUUID();
        String uuidEventString = uuidEvent.toString();
        CollectionReference events = db.collection("events");
        events.document(uuidEventString).set(eventData).addOnSuccessListener(aVoid -> {


            Toast.makeText(activity, "Event Created", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Log.w("TAG", "Error writing event", e));
    }
    private void updateNumOfListens(){


        DocumentReference docRef = db.collection("users/" + holderSong.getArtistID()+"/songs").document(holderSong.getSongID());

        String numOfListens = String.valueOf(holderSong.getNumberOfListens() + 1);


        holderSong.setNumberOfListens((int) holderSong.getNumberOfListens() + 1);
        numberOfPlays.setText("Number of plays: " + String.valueOf((int) holderSong.getNumberOfListens()));
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