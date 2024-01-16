package com.example.musicapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.entities.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerService extends MediaBrowserServiceCompat {

    private static final int NOTIFICATION_ID = 101;
    private static final String NOTIFICATION_CHANNEL_ID = "music_player_channel";
    private static final int REQUEST_CODE = 100;
    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songList = new ArrayList<>(); // list of song file URLs or paths
    public static int mCurrentSongIndex = 0; // index of the currently playing song
    private MediaSessionCompat mediaSession;
    private MediaMetadataCompat.Builder metadataBuilder;
    private PlaybackStateCompat.Builder stateBuilder;
    private MediaControllerCompat mediaController;

    private List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
    public static boolean mediaItemsAreReady = false;
    private boolean isBuffering = false;
    private int lastMediaState = PlaybackStateCompat.STATE_NONE;

    private static boolean isRunning = false;

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // create channel for notification
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Music Player Service", NotificationManager.IMPORTANCE_DEFAULT);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        mediaSession = new MediaSessionCompat(this, "MyMusicService");
        mediaSession.setMediaButtonReceiver(null);
        setSessionToken(mediaSession.getSessionToken());

        stateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                        PlaybackStateCompat.ACTION_STOP
        );

        metadataBuilder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "Artist Name")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Album Name")
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, "Song Name")
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0);

        mediaSession.setMetadata(metadataBuilder.build());

        mediaSession.setPlaybackState(stateBuilder.build());

        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                play();
            }

            @Override
            public void onPause() {
                pause();
            }

            @Override
            public void onSkipToNext() {
                skipToNext();
            }

            @Override
            public void onSkipToPrevious() {
                skipToPrevious();
            }

            @Override
            public void onStop() {
                stop();
            }

            @Override
            public void onSeekTo(long pos) {
                seekTo(pos);
            }

            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                if (Intent.ACTION_MEDIA_BUTTON.equals(mediaButtonEvent.getAction())) {
                    KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                    if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode()) {
                        if (mediaPlayer.isPlaying()) {
                            pause();
                        } else {
                            play();
                        }
                        return true;
                    }
                    if (KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode()) {
                        skipToNext();
                        return true;
                    }
                    if (KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode()) {
                        skipToPrevious();
                        return true;
                    }
                }
                return super.onMediaButtonEvent(mediaButtonEvent);
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        if (mediaSession != null) {
            mediaSession.release();
            mediaSession = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        MediaButtonReceiver.handleIntent(mediaSession, intent);

        mediaController = new MediaControllerCompat(this, mediaSession.getSessionToken());

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // Implement a method to verify whether the client is authorized to access the media.
        if (isClientAuthorized(clientPackageName, clientUid)) {
            // Returns the root ID that clients can use with onLoadChildren() to retrieve the content hierarchy.
            return new BrowserRoot("root_id", null);
        } else {
            // Return null if the client is not authorized.
            return null;
        }
    }

    // Dummy method for client verification. You should implement actual logic.
    private boolean isClientAuthorized(String clientPackageName, int clientUid) {
        // Logic to check if the client is authorized to access the media.
        return true;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        // If the media item information is ready, you can call sendResult() immediately.
        if (mediaItemsAreReady) {
            result.sendResult(mediaItems);
        } else {
            result.detach();
            // Load media items and call sendResult() when ready.
            // loadMediaItems(result);
        }
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result, @NonNull Bundle options) {
        ArrayList<Song> songListIn = options.getParcelableArrayList("songList");
        result.detach();
        // Load media items and call sendResult() when ready.
        loadMediaItems(result, songListIn);
    }

    // Dummy method for media items loading. You should implement actual logic.
    private void loadMediaItems(Result<List<MediaBrowserCompat.MediaItem>> result, ArrayList<Song> songListIn) {
        // Check if the song list is available and not empty
        if (songListIn != null && !songListIn.isEmpty()) {
            mediaItemsAreReady = false;

            if (mediaPlayer != null) {
                stop();
            }

            if (songList.isEmpty()) {
                songList.addAll(0, songListIn);
            } else {
                songList.addAll(mCurrentSongIndex + 1, songListIn);
            }
            // Initialize the media items list
            if (mediaItems == null) {
                mediaItems = new ArrayList<>();
            }

            ArrayList<MediaBrowserCompat.MediaItem> tempMediaItems = new ArrayList<>();

            for (Song song : songListIn) {
                MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                        .setMediaId(String.valueOf(song.getSongFileUUID()))
                        .setTitle(song.getSongName())
                        .setSubtitle(song.getArtistName())
                        .setDescription(song.getAlbumName())
                        .setMediaUri(Uri.parse(song.getSongPath()))
                        .build();

                MediaBrowserCompat.MediaItem mediaItem = new MediaBrowserCompat.MediaItem(description,
                        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

                // Add the media item to the list
                tempMediaItems.add(mediaItem);
            }

            if (mediaItems.isEmpty()) {
                mediaItems.addAll(tempMediaItems);
            } else {
                mediaItems.addAll(mCurrentSongIndex + 1, tempMediaItems);

                mCurrentSongIndex++;
            }

            mediaItemsAreReady = true;

            if (mediaController == null) {
                mediaController = new MediaControllerCompat(this, mediaSession.getSessionToken());
            }
            play();

            // Send the result to the connected MediaBrowser
            result.sendResult(mediaItems);
        } else {
            result.sendResult(null);
        }
    }

    private void play() {
        PlaybackStateCompat pbStateCompat = mediaController.getPlaybackState();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            try {
                mediaPlayer.setDataSource(this, Uri.parse(songList.get(mCurrentSongIndex).getSongPath()));
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(mp -> {
                    metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration())
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, (String) mediaItems.get(mCurrentSongIndex).getDescription().getTitle())
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, (String) mediaItems.get(mCurrentSongIndex).getDescription().getSubtitle());
                    mediaSession.setMetadata(metadataBuilder.build());
                    mediaSession.setActive(true);
                    mediaPlayer.start();
                    updatePlaybackState(PlaybackStateCompat.ACTION_PLAY);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setOnCompletionListener(mp -> skipToNext());

        } else if (pbStateCompat.getState() == PlaybackStateCompat.STATE_PAUSED || pbStateCompat.getState() == PlaybackStateCompat.STATE_NONE) {
            mediaPlayer.start();
            updatePlaybackState(PlaybackStateCompat.ACTION_PLAY);
        }
    }

    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            updatePlaybackState(PlaybackStateCompat.ACTION_PAUSE);
        }
    }

    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void stop() {
        if (mediaPlayer != null) {
            clearMediaPlayer();
            mediaSession.setActive(false);
            updatePlaybackState(PlaybackStateCompat.ACTION_STOP);
        }
    }

    private void skipToNext() {
        if (mediaPlayer != null && songList != null && mCurrentSongIndex < songList.size() - 1) {
            clearMediaPlayer();
            mCurrentSongIndex++;
            play();
            updatePlaybackState(PlaybackStateCompat.ACTION_SKIP_TO_NEXT);
        }
    }

    private void skipToPrevious() {
        if (mediaPlayer != null && mCurrentSongIndex > 0) {
            Log.d("TAG1", "skipToPrevious: " + mCurrentSongIndex);
            clearMediaPlayer();
            mCurrentSongIndex--;
            play();
            updatePlaybackState(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        }
    }

    private void seekTo(long position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo((int) position);
            mediaPlayer.setOnSeekCompleteListener(mp -> updatePlaybackState(PlaybackStateCompat.ACTION_SEEK_TO));
        }
    }

    private Notification getNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // replace with your own icon
                .setContentTitle(mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_TITLE)) // replace with dynamic title based on playing track
                .setContentText(mediaController.getMetadata().getString(MediaMetadataCompat.METADATA_KEY_ARTIST)) // replace with dynamic text based on playing track
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.playlist_image)) // replace with dynamic album art
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP))
                .setContentIntent(createContentIntent())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Add previous action
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_previous, "Previous",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));

        // Add play/pause action
        if (mediaPlayer.isPlaying()) {
            builder.addAction(new NotificationCompat.Action(R.drawable.ic_pause, "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        } else {
            builder.addAction(new NotificationCompat.Action(R.drawable.ic_play, "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        }

        // Add next action
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_next, "Next",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));

        // Add stop action
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_stop, "Stop",
                MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP)));

        // Apply the media style template
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession.getSessionToken())
                .setShowActionsInCompactView(0, 1, 2, 3) // indices for "play/pause", "next" and "previous" actions
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_STOP)));

        return builder.build();
    }

    private PendingIntent createContentIntent() {
        Intent openUI = new Intent(this, MainActivity.class);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void updatePlaybackState(long action) {
        if (action == PlaybackStateCompat.ACTION_PLAY) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1, SystemClock.elapsedRealtime());
        } else if (action == PlaybackStateCompat.ACTION_PAUSE) {
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayer.getCurrentPosition(), 1, SystemClock.elapsedRealtime());
        } else if (action == PlaybackStateCompat.ACTION_STOP) {
            stateBuilder.setState(PlaybackStateCompat.STATE_STOPPED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1, SystemClock.elapsedRealtime());
        } else if (action == PlaybackStateCompat.ACTION_SKIP_TO_NEXT) {
            stateBuilder.setState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT, mediaPlayer.getCurrentPosition(), 1, SystemClock.elapsedRealtime());
        } else if (action == PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) {
            stateBuilder.setState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS, mediaPlayer.getCurrentPosition(), 1, SystemClock.elapsedRealtime());
        } else if (action == PlaybackStateCompat.ACTION_SEEK_TO) {
            lastMediaState = mediaController.getPlaybackState().getState();
            // detect when buffering is complete
            if (lastMediaState == PlaybackStateCompat.STATE_PLAYING) {
                stateBuilder.setState(PlaybackStateCompat.STATE_BUFFERING, mediaPlayer.getCurrentPosition(), 1, SystemClock.elapsedRealtime());
                mHandler.post(playbackBuffering);
            } else {
                stateBuilder.setState(lastMediaState, mediaPlayer.getCurrentPosition(), 1, SystemClock.elapsedRealtime());
            }
        } else if (action == PlaybackStateCompat.ACTION_PLAY_PAUSE) {
            if (mediaPlayer.isPlaying()) {
                stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayer.getCurrentPosition(), 1, SystemClock.elapsedRealtime());
            } else {
                stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1, SystemClock.elapsedRealtime());
            }
        }
        mediaSession.setPlaybackState(stateBuilder.build());

        if (action != PlaybackStateCompat.ACTION_STOP) {
            // Now that the state is updated, notify the service to become active
            startForeground(NOTIFICATION_ID, getNotification());
        } else {
            stopForeground(true);
        }
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable playbackBuffering = new Runnable() {
        @Override
        public void run() {
            PlaybackStateCompat playbackState = mediaController.getPlaybackState();
            isBuffering = playbackState != null && playbackState.getState() == PlaybackStateCompat.STATE_BUFFERING;
            if (isBuffering && mediaPlayer.isPlaying()) {
                updatePlaybackState(PlaybackStateCompat.ACTION_PLAY);
                mHandler.removeCallbacks(playbackBuffering);
            }
        }
    };

}