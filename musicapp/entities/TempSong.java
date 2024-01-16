package com.example.musicapp.entities;

public class TempSong {
    private int songId;
    private String songName;
    private String artistName;
    private String albumName;
    private String songPath;

    public TempSong(int songId, String songName, String artistName, String albumName, String songPath) {
        this.songId = songId;
        this.songName = songName;
        this.artistName = artistName;
        this.albumName = albumName;
        this.songPath = songPath;
    }

    public int getSongId() {
        return songId;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getSongPath() {
        return songPath;
    }
}
