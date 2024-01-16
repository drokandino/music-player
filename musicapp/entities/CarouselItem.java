package com.example.musicapp.entities;

public class CarouselItem {
    private final int imageResId;
    private Song song;

    public CarouselItem(int imageResId, Song song) {
        this.imageResId = imageResId;
        this.song = song;
    }

    public int getImageResId() {
        return imageResId;
    }

    public Song getSong() {
        return song;
    }
}

