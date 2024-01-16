package com.example.musicapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

public class Song implements Parcelable {

    private String songName, artistName, songFileUUID, genre, artistID, albumUUDI, albumName, songPath, artistBio, songID;
    private Timestamp releaseDate;
    private int numberOfLikes, numberOfListens;

    public Song() {
    }

    public Song(Parcel in) {
        songName = in.readString();
        artistName = in.readString();
        songFileUUID = in.readString();
        genre = in.readString();
        artistID = in.readString();
        albumUUDI = in.readString();
        albumName = in.readString();
        songPath = in.readString();
        artistBio = in.readString();
        releaseDate = (Timestamp) in.readSerializable();
        numberOfLikes = in.readInt();
        numberOfListens = in.readInt();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(songName);
        dest.writeString(artistName);
        dest.writeString(songFileUUID);
        dest.writeString(genre);
        dest.writeString(artistID);
        dest.writeString(albumUUDI);
        dest.writeString(albumName);
        dest.writeString(songPath);
        dest.writeString(artistBio);
        dest.writeSerializable(releaseDate);
        dest.writeInt(numberOfLikes);
        dest.writeInt(numberOfListens);
    }

    public Timestamp getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Timestamp releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumUUDI() {
        return albumUUDI;
    }

    public void setAlbumUUDI(String albumUUDI) {
        this.albumUUDI = albumUUDI;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getSongFileUUID() {
        return songFileUUID;
    }

    public void setSongFileUUID(String songFileUUID) {
        this.songFileUUID = songFileUUID;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getArtistID() {
        return artistID;
    }

    public void setArtistID(String artistID) {
        this.artistID = artistID;
    }


    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getArtistBio() {
        return artistBio;
    }

    public void setArtistBio(String artistBio) {
        this.artistBio = artistBio;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(String numberOfLikes) {
        this.numberOfLikes = Integer.valueOf(numberOfLikes);
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public int getNumberOfListens() {
        return numberOfListens;
    }

    public void setNumberOfListens(int numberOfListens) {
        this.numberOfListens = numberOfListens;
    }

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songName='" + songName + '\'' +
                ", artistName='" + artistName + '\'' +
                ", songFileUUID='" + songFileUUID + '\'' +
                ", genre='" + genre + '\'' +
                ", artistID='" + artistID + '\'' +
                ", albumUUDI='" + albumUUDI + '\'' +
                ", albumName='" + albumName + '\'' +
                ", songPath='" + songPath + '\'' +
                ", artistBio='" + artistBio + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
