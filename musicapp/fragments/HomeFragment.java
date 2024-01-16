package com.example.musicapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.adapters.CarouselAdapter;
import com.example.musicapp.entities.CarouselItem;
import com.example.musicapp.entities.Song;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    ArrayList<Song> allSongs = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            allSongs = getArguments().getParcelableArrayList("allSongs");
        }

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.isArtistChange();

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (hour < 5) {
            greeting = "Good morning? \uD83E\uDD14";
        } else if (hour < 12) {
            greeting = "Good morning!";
        } else if (hour < 18) {
            greeting = "Good afternoon!";
        } else {
            greeting = "Good evening!";
        }

        MaterialTextView greetingTextView = view.findViewById(R.id.greeting_message);
        greetingTextView.setText(greeting);



        AppCompatImageButton socialButton = view.findViewById(R.id.social_button);
        socialButton.setOnClickListener(v -> {
            activity.socialFragment.setCalledFromFragment(activity.homeFragment);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.main_fragment_container, activity.socialFragment)
                    .addToBackStack("settings")
                    .commit();
            System.out.println("Recently played button clicked");
        });

        AppCompatImageButton settingsButton = view.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> {

            activity.settingsFragment.setCalledFromFragment(activity.homeFragment);
                    activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.main_fragment_container, activity.settingsFragment)
                .addToBackStack("settings")
                .commit();
            }
        );

        AppCompatImageButton createPlaylistButton = view.findViewById(R.id.create_playlist_button);
        createPlaylistButton.setOnClickListener(v ->
        {
            activity.playlistCreateFragment.setCalledFromFragment(activity.homeFragment);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.main_fragment_container, activity.playlistCreateFragment)
                    .commit();
        });


        List<CarouselItem> items = new ArrayList<>();

        for (Song song : allSongs) {
            items.add(new CarouselItem(R.drawable.playlist_image, song));
        }

//        items.add(new CarouselItem(R.drawable.playlist_image, "Album 1"));
//        items.add(new CarouselItem(R.drawable.playlist_or_artist_image, "Album 2"));
//        items.add(new CarouselItem(R.drawable.playlist_image, "Album 3"));
//        items.add(new CarouselItem(R.drawable.playlist_or_artist_image, "Album 4"));
//        items.add(new CarouselItem(R.drawable.playlist_image, "Album 5"));
//        items.add(new CarouselItem(R.drawable.playlist_or_artist_image, "Album 6"));
//        items.add(new CarouselItem(R.drawable.playlist_image, "Album 7"));
//        items.add(new CarouselItem(R.drawable.playlist_or_artist_image, "Album 8"));
//        items.add(new CarouselItem(R.drawable.playlist_image, "Album 9"));
//        items.add(new CarouselItem(R.drawable.playlist_or_artist_image, "Album 10"));


        RecyclerView recommendedRecyclerView = view.findViewById(R.id.recommended_recycler);
        LinearLayoutManager recommendedLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        recommendedRecyclerView.setLayoutManager(recommendedLayoutManager);


        //Sort items by number of listens
        List<CarouselItem> itemsRecomend = new ArrayList<>();

        for(CarouselItem item: items){
            itemsRecomend.add(item);
        }
        Collections.sort(itemsRecomend, new Comparator<CarouselItem>() {
            @Override
            public int compare(CarouselItem item1, CarouselItem item2) {
                return Integer.compare(item2.getSong().getNumberOfListens(), item1.getSong().getNumberOfListens());
            }
        });
        CarouselAdapter recommendedAdapter = new CarouselAdapter(activity, itemsRecomend);
        recommendedRecyclerView.setAdapter(recommendedAdapter);


        RecyclerView recentlyPlayedRecyclerView = view.findViewById(R.id.recently_played_recycler);
        LinearLayoutManager recentlyPlayedLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        recentlyPlayedRecyclerView.setLayoutManager(recentlyPlayedLayoutManager);

        CarouselAdapter recentlyPlayedAdapter = new CarouselAdapter(activity, items);
        recentlyPlayedRecyclerView.setAdapter(recentlyPlayedAdapter);


        RecyclerView popularRecyclerView = view.findViewById(R.id.popular_recycler);
        LinearLayoutManager popularLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        popularRecyclerView.setLayoutManager(popularLayoutManager);

        CarouselAdapter popularAdapter = new CarouselAdapter(activity, items);
        popularRecyclerView.setAdapter(popularAdapter);


        RecyclerView featuredRecyclerView = view.findViewById(R.id.featured_recycler);
        LinearLayoutManager featuredLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        featuredRecyclerView.setLayoutManager(featuredLayoutManager);

        CarouselAdapter featuredAdapter = new CarouselAdapter(activity, items);
        featuredRecyclerView.setAdapter(featuredAdapter);
    }
}