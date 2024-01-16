package com.example.musicapp.views;

import android.content.Context;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.musicapp.R;
import com.example.musicapp.entities.DataSingleton;
import com.example.musicapp.entities.Song;

public class PlaylistCreateViewHolder extends SongsViewHolder implements CheckBox.OnCheckedChangeListener{

    private CheckBox includeInPlaylistCheck;
    public Song song;

    public PlaylistCreateViewHolder(View view, Context context){
        super(view, context);
        includeInPlaylistCheck = (CheckBox) itemView.findViewById(R.id.include_in_playlist);
        includeInPlaylistCheck.setOnCheckedChangeListener(this);
    }



    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
            DataSingleton.getDataSingleton().addPlaylistCreateSongs(song);
        } else {
            DataSingleton.getDataSingleton().removeFromPlaylistCreateSongs(song);
        }
    }
}
