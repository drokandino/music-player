package com.example.musicapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.entities.Album;
import com.example.musicapp.views.ArtistViewAlbumViewHolder;
import com.example.musicapp.R;

import java.util.List;

public class ArtistViewAlbumsAdapter extends RecyclerView.Adapter<ArtistViewAlbumViewHolder> {

    Context context;

    List<Album> items;
    MainActivity activity;

    public ArtistViewAlbumsAdapter(Context context, List<Album> items, MainActivity activity) {
        this.context = context;
        this.items = items;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ArtistViewAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistViewAlbumViewHolder(LayoutInflater.from(context).inflate(R.layout.artist_view_album_item, parent, false), context, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewAlbumViewHolder holder, int position) {
        holder.albumName.setText(items.get(position).getAlbumName());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
