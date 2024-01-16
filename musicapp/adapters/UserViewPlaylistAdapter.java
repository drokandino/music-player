package com.example.musicapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.entities.Playlist;
import com.example.musicapp.entities.Song;
import com.example.musicapp.views.ArtistViewSongsViewHolder;
import com.example.musicapp.views.UserViewPlaylistHolder;

import java.util.List;

public class UserViewPlaylistAdapter extends RecyclerView.Adapter<UserViewPlaylistHolder>{

    Context context;
    List<Playlist> items;
    MainActivity activity;

    public UserViewPlaylistAdapter(Context context, List<Playlist> items, MainActivity activity) {
        this.context = context;
        this.items = items;
        this.activity = activity;
    }
    @NonNull
    @Override
    public UserViewPlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewPlaylistHolder(LayoutInflater.from(context).inflate(R.layout.user_view_playlist_item, parent, false), context, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewPlaylistHolder holder, int position) {
        holder.playlistName.setText(items.get(position).getPlaylistName());
        holder.playlist = items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
