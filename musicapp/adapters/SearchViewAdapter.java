package com.example.musicapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.entities.SearchElement;
import com.example.musicapp.views.SearchViewHolder;

import java.util.List;

public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewHolder> {


    Context context;
    MainActivity activity;
    List<SearchElement> items;

    public SearchViewAdapter(Context context, List<SearchElement> items, MainActivity activity) {
        this.context = context;
        this.items = items;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context).inflate(R.layout.search_songs_list_item, parent, false), activity);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.elementName.setText(items.get(position).getElementName());
        holder.elementType.setText(items.get(position).getElementType());
        holder.searchElement = items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}