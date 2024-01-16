package com.example.musicapp.entities;


import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

public class Album implements Parcelable {

    private String albumName, albumID, artistID;
    private Timestamp releaseDate;

    public Album() {
    }

    public Album(Parcel in) {
        albumName = in.readString();
        albumID = in.readString();
        artistID = in.readString();
        releaseDate = (Timestamp) in.readSerializable();
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(albumName);
        dest.writeString(albumID);
        dest.writeString(artistID);
        dest.writeSerializable(releaseDate);
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public Timestamp getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Timestamp releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getArtistID() {
        return artistID;
    }

    public void setArtistID(String artistID) {
        this.artistID = artistID;
    }

    @Override
    public String toString() {
        return "Album{" +
                "albumName='" + albumName + '\'' +
                ", albumID='" + albumID + '\'' +
                ", artistID='" + artistID + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Album other = (Album) obj;
        return this.getAlbumID() == other.getAlbumID() && this.getAlbumName().equals(other.getAlbumName());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + albumID.hashCode();
        result = 31 * result + albumName.hashCode();
        return result;
    }
}
