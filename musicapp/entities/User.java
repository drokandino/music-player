package com.example.musicapp.entities;

import java.util.ArrayList;

public class User {

    private String userName, userBio, userID;
    private ArrayList<String> followingIDs, followersIDs;
    private ArrayList<Playlist> playlists;
    private boolean isArtist;

    public User() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isArtist() {
        return isArtist;
    }

    public void setArtist(boolean artist) {
        isArtist = artist;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public ArrayList<String> getFollowingIDs() {
        return followingIDs;
    }

    public void setFollowingIDs(ArrayList<String> followingIDs) {
        this.followingIDs = followingIDs;
    }

    public ArrayList<String> getFollowersIDs() {
        return followersIDs;
    }

    public void setFollowersIDs(ArrayList<String> followersIDs) {
        this.followersIDs = followersIDs;
    }
}
