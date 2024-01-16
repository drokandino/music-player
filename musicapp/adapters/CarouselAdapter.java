package com.example.musicapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.entities.CarouselItem;
import com.example.musicapp.views.CarouselViewHolder;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselViewHolder> {
    private final MainActivity activity;

    private final List<CarouselItem> items;

    public CarouselAdapter(MainActivity activity, List<CarouselItem> items) {
        this.activity = activity;
        this.items = items;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carousel_item, parent, false);
        return new CarouselViewHolder(activity, view);
    }

    @Override
    public void onBindViewHolder(CarouselViewHolder holder, int position) {
        CarouselItem item = items.get(position);
        holder.imageView.setImageResource(item.getImageResId());
        holder.textView.setText(item.getSong().getSongName());
        holder.song = item.getSong();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

