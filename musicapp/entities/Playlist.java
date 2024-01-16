package com.example.musicapp.entities;

import java.util.ArrayList;

public class Playlist {

    private String creatorID, playlistName, playlistID, creatorName;
    private ArrayList<Song> playlistSongs;
    private ArrayList<String> songsIDs;
    private boolean isPrivate;

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public ArrayList<Song> getPlaylistSongs() {
        return playlistSongs;
    }

    public void setPlaylistSongs(ArrayList<Song> playlistSongs) {
        this.playlistSongs = playlistSongs;

        // create a new songIDs list when setting playlist songs
        songsIDs = new ArrayList<>();
        for(Song song: playlistSongs){
            songsIDs.add(song.getSongFileUUID());
        }
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public ArrayList<String> getSongsIDs() {
        return songsIDs;
    }

    public void setSongsIDs(ArrayList<String> songsIDs) {
        this.songsIDs = songsIDs;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "creatorID='" + creatorID + '\'' +
                ", playlistName='" + playlistName + '\'' +
                ", playlistID='" + playlistID + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", playlistSongs=" + playlistSongs +
                ", songsIDs=" + songsIDs +
                '}';
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
