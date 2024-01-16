package com.example.musicapp.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.example.musicapp.entities.TempSong;
import com.example.musicapp.services.MusicPlayerService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerActivity extends AppCompatActivity {

    private static final String NOTIFICATION_CHANNEL_ID = "music_player_channel";
    private MediaBrowserCompat mediaBrowser;
    private MediaControllerCompat mediaController;
    private String songTitle = "";
    private String songArtist = "";
    private int songDuration = 0;
    private int songElapsed = 0;
    private SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        findViewById(R.id.title).setSelected(true);
        findViewById(R.id.artist).setSelected(true);

        Button closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> onBackPressed());

        mediaBrowser = new MediaBrowserCompat(this,
                new ComponentName(this, MusicPlayerService.class),
                connectionCallbacks,
                null);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!MusicPlayerService.isRunning()) {
            initializeData();
        }

        mediaBrowser.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Disconnect MediaBrowser when it's not in use
        if (MediaControllerCompat.getMediaController(MusicPlayerActivity.this) != null) {
            MediaControllerCompat.getMediaController(MusicPlayerActivity.this).unregisterCallback(controllerCallback);
        }
        mediaBrowser.disconnect();
    }

    private final MediaBrowserCompat.ConnectionCallback connectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    // Get the token for the MediaSession
                    MediaSessionCompat.Token token = mediaBrowser.getSessionToken();

                    // Create a MediaControllerCompat
                    mediaController = new MediaControllerCompat(MusicPlayerActivity.this, token);
                    MediaControllerCompat.setMediaController(MusicPlayerActivity.this, mediaController);

                    // Finish building the UI
                    buildTransportControls();
                }

                @Override
                public void onConnectionSuspended() {
                    // The Service has crashed. Disable transport controls until it automatically reconnects
                }

                @Override
                public void onConnectionFailed() {
                    // The Service has refused our connection
                }
            };

    private void initializeData() {
        ArrayList<Integer> songList = new ArrayList<>();
        TempSong firstSong = new TempSong(1, "Danza Kuduro", "Don Omar", "Don Omar Presents: Meet The Orphans", "https://firebasestorage.googleapis.com/v0/b/music-app-7dc1d.appspot.com/o/songs%2F0ee95f21-6bd9-41aa-8bdd-50ee26c216f4.mp3?alt=media&token=412ea96d-008b-4b6b-a19e-db57d1d0fb24");
        TempSong secondSong = new TempSong(2, "In Da Club", "50 Cent", "Get Rich Or Die Tryin'", "https://firebasestorage.googleapis.com/v0/b/music-app-7dc1d.appspot.com/o/songs%2Fde4d5dcf-6d7c-4b60-87bb-6a0f044d1923.mp3?alt=media&token=0e420a8f-92c2-4f1a-8aba-0ae79de098d5");
        songList.add(firstSong.getSongId());
        songList.add(secondSong.getSongId());

        ArrayList<String> songPaths = new ArrayList<>();
        songPaths.add(firstSong.getSongPath());
        songPaths.add(secondSong.getSongPath());

        Intent startServiceIntent = new Intent(this, MusicPlayerService.class);
        startServiceIntent.putExtra("songList", songPaths);

        Bundle songBundle = new Bundle();
        songBundle.putIntegerArrayList("songList", songList);
        mediaBrowser.subscribe("media", songBundle, new MediaBrowserCompat.SubscriptionCallback() {
            @Override
            public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
                super.onChildrenLoaded(parentId, children);
            }
        });

        this.startService(startServiceIntent);
    }

    void buildTransportControls() {
        // Grab the view for the play/pause button
        MaterialButton playPauseButton = findViewById(R.id.play_pause_button);
        MaterialButton nextButton = findViewById(R.id.next_button);
        MaterialButton previousButton = findViewById(R.id.previous_button);
        mSeekBar = findViewById(R.id.song_progress);
        playPauseButton.setEnabled(true);

        PlaybackStateCompat playbackState = mediaController.getPlaybackState();
        if (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            playPauseButton.setIconResource(R.drawable.ic_pause_circle);
        } else {
            playPauseButton.setIconResource(R.drawable.ic_play_circle);
        }

        // Attach a listener to the button
        playPauseButton.setOnClickListener(v -> {
            PlaybackStateCompat pbStateCompat = mediaController.getPlaybackState();
            if (pbStateCompat != null) {
                int pbState = pbStateCompat.getState();
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    mediaController.getTransportControls().pause();
                } else {
                    mediaController.getTransportControls().play();
                }
            }
        });

        nextButton.setOnClickListener(v -> mediaController.getTransportControls().skipToNext());

        previousButton.setOnClickListener(v -> mediaController.getTransportControls().skipToPrevious());

        // if song is playing then update the seekbar
        if (playbackState.getState() != PlaybackStateCompat.STATE_NONE) {
            MediaMetadataCompat metadata = mediaController.getMetadata();

            int songDuration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) / 1000;
            int durationMinutes = songDuration / 60;
            int durationSeconds = songDuration % 60;

            songElapsed = (int) playbackState.getPosition() / 1000;
            int minutes = songElapsed / 60;
            int seconds = songElapsed % 60;

            mSeekBar.setMax(songDuration);
            mSeekBar.setProgress(songElapsed);

            String durationString = String.format("%02d:%02d", durationMinutes, durationSeconds);
            String elapsedTimeString = String.format("%02d:%02d", minutes, seconds);

            MaterialTextView durationView = findViewById(R.id.duration);
            MaterialTextView elapsedTimeView = findViewById(R.id.elapsed_time);

            elapsedTimeView.setText(elapsedTimeString);
            durationView.setText(durationString);

            mHandler.post(mUpdateElapsedTimeRunnable);
        }

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                songElapsed = progress;
                int minutes = songElapsed / 60;
                int seconds = songElapsed % 60;

                MaterialTextView elapsedTimeView = findViewById(R.id.elapsed_time);
                String elapsedTimeString = String.format("%02d:%02d", minutes, seconds);
                elapsedTimeView.setText(elapsedTimeString);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mUpdateElapsedTimeRunnable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaController.getTransportControls().seekTo((long) songElapsed * 1000);
                mHandler.post(mUpdateElapsedTimeRunnable);
            }
        });

        // update song title and artist
        MediaMetadataCompat metadata = mediaController.getMetadata();
        String songTitle = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
        String songArtist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);

        MaterialTextView songTitleView = findViewById(R.id.title);
        MaterialTextView songArtistView = findViewById(R.id.artist);

        songTitleView.setText(songTitle);
        songArtistView.setText(songArtist);

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback);
    }

    private final MediaControllerCompat.Callback controllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(PlaybackStateCompat state) {
                    // Handle change in playback state,
                    // e.g. use state.getState() and update your UI accordingly
                    MaterialButton playPauseButton = findViewById(R.id.play_pause_button);
                    if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                        playPauseButton.setIconResource(R.drawable.ic_pause_circle);
                        mHandler.post(mUpdateElapsedTimeRunnable);
                    } else {
                        playPauseButton.setIconResource(R.drawable.ic_play_circle);
                        mHandler.removeCallbacks(mUpdateElapsedTimeRunnable);
                    }
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    // Handle change in metadata,
                    // e.g. use metadata.getDescription().getTitle() and update your UI accordingly
                    songTitle = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
                    songArtist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
                    songDuration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) / 1000;
                    songElapsed = 0;

                    int durationMinutes = songDuration / 60;
                    int durationSeconds = songDuration % 60;
                    String durationString = String.format("%02d:%02d", durationMinutes, durationSeconds);

                    MaterialTextView titleView = findViewById(R.id.title);
                    MaterialTextView artistView = findViewById(R.id.artist);
                    MaterialTextView durationView = findViewById(R.id.duration);
                    MaterialTextView elapsedTimeView = findViewById(R.id.elapsed_time);
                    MaterialButton playPauseButton = findViewById(R.id.play_pause_button);

                    titleView.setText(songTitle);
                    artistView.setText(songArtist);
                    durationView.setText(durationString);
                    elapsedTimeView.setText(R.string.song_start_time);
                    mSeekBar.setMax(songDuration);
                    playPauseButton.setEnabled(true);
                }

            };

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mUpdateElapsedTimeRunnable = new Runnable() {
        @Override
        public void run() {
            PlaybackStateCompat playbackState = mediaController.getPlaybackState();
            boolean isPlaying = playbackState != null && playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
            if (mediaBrowser.isConnected() && isPlaying) {
                songElapsed = (int) playbackState.getPosition() / 1000;
                int minutes = songElapsed / 60;
                int seconds = songElapsed % 60;

                MaterialTextView elapsedTimeView = findViewById(R.id.elapsed_time);
                String elapsedTimeString = String.format("%02d:%02d", minutes, seconds);
                elapsedTimeView.setText(elapsedTimeString);

                mSeekBar = findViewById(R.id.song_progress);
                mSeekBar.setProgress(songElapsed);

                mHandler.postDelayed(this, 1000); // update every second
            }
        }
    };


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }
}