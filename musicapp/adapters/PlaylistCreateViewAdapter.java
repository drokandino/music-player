package com.example.musicapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.entities.Song;
import com.example.musicapp.views.PlaylistCreateViewHolder;
import com.example.musicapp.R;

import java.util.List;

public class PlaylistCreateViewAdapter extends RecyclerView.Adapter<PlaylistCreateViewHolder>{

        Context context;

        List<Song> items;
        MainActivity activity;

        public PlaylistCreateViewAdapter (Context context, List<Song> items) {
            this.context = context;
            this.items = items;
        }

        @NonNull
        @Override
        public PlaylistCreateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PlaylistCreateViewHolder(LayoutInflater.from(context).inflate(R.layout.playlist_create_list_item, parent, false), context);
        }


    @Override
        public void onBindViewHolder(@NonNull PlaylistCreateViewHolder holder, int position) {
            holder.songName.setText(items.get(position).getSongName());
            holder.artistName.setText(items.get(position).getArtistName());
            Log.d("adapter", String.valueOf(items.get(position).getNumberOfLikes()));
            holder.setSongFileUUID(items.get(position).getSongFileUUID());
            holder.setArtistID(items.get(position).getArtistID());
            holder.song = items.get(position);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

}
