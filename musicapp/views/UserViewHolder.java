package com.example.musicapp.views;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.entities.User;
import com.example.musicapp.fragments.ArtistViewFragment;

import java.nio.file.LinkOption;

public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView userNameTextView;
    public User user;
    FragmentManager fragmentManager;
    Fragment allUsersViewFragment;
    MainActivity activity;  // Assuming you have access to these fragments in MainActivity.

    public UserViewHolder(@NonNull View itemView, MainActivity activity, Fragment allUsersViewFragment) {
        super(itemView);
        userNameTextView = itemView.findViewById(R.id.users_view_user_name);
        this.activity = activity;
        itemView.setOnClickListener(this);
        this.allUsersViewFragment = allUsersViewFragment;

    }

    @Override
    public void onClick(View v) {

        Fragment targetFragment;

        if (user.isArtist()) {
            targetFragment = activity.artistViewFragment;
            //activity.artistViewFragment.setArtistID(user.getUserID());
            activity.artistViewFragment.setArtistID(user.getUserID());
            activity.artistViewFragment.setCalledFromFragment(activity.allUsersViewFragment);
            Log.d("userViewData", user.getUserName() + user.getUserID());
        } else {
            activity.userViewFragment.user = user;
            targetFragment = activity.userViewFragment;
            Log.d("userViewData", user.getUserName() + user.getUserID());

        }

        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, targetFragment)
                .commit();

    }


}