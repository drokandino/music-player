package com.example.musicapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.entities.Album;
import com.example.musicapp.entities.DataSingleton;
import com.example.musicapp.R;
import com.example.musicapp.entities.Song;
import com.example.musicapp.activities.MainActivity;
import com.example.musicapp.adapters.SongsViewAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


public class LibraryFragment extends Fragment {

    private FirebaseFirestore db;
    private Map<String, String> usersSongCollRef;
    public ArrayList<Song> allSongs, artistSongs, genreSongs, releaseYearSongs;
    public ArrayList<Album> allAlbums;
    public ArrayList<String> allArtists;
    RecyclerView allSongsView;
    LinearLayoutManager allSongsViewManager;
    SongsViewAdapter songsViewAdapter;
    Spinner filterBySpinner, filterSpinner;
    ArrayAdapter<String> filterByAdapter, filterAdapter;
    ArrayList<String> allReleaseYears;
    Button filterBtn;
    String selectedItem;
    Integer counter;
    int numOfFetchedURLs;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    MainActivity activity;


    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false);
    }


    AdapterView.OnItemSelectedListener filterBySpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedItem = (String) parent.getItemAtPosition(position);

            if (selectedItem.equals("Genre")) {
                String[] entries = getResources().getStringArray(R.array.genre_array);
                filterAdapter.clear();
                filterAdapter.addAll(entries);
                filterAdapter.notifyDataSetChanged();
            } else if (selectedItem.equals("Artist")) {
                Log.d("promjena", allArtists.toString());
                filterAdapter.clear();
                filterAdapter.addAll(allArtists);
                filterAdapter.notifyDataSetChanged();
            } else if (selectedItem.equals("Release year")) {
                Log.d("godine", allReleaseYears.toString());
                filterAdapter.clear();
                filterAdapter.addAll(allReleaseYears);
                filterAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Handle case when no item is selected
        }
    };

    private void getAllReleaseYears() {
        allReleaseYears = new ArrayList<>();
        for (Album album : allAlbums) {
            Timestamp timestamp = album.getReleaseDate();
            Date date = new Date();
            date.setTime(timestamp.getTime());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
            String releaseYear = simpleDateFormat.format(calendar.getTime()).toString();


            if (!allReleaseYears.contains(releaseYear)) {
                allReleaseYears.add(releaseYear);
            }
        }
    }
    /*
    private void getAllArtists(){
        for (Song song: allSongs){
            if(!allArtists.contains(song.getArtistName())){
                allArtists.add(song.getArtistName());
            }
        }
        ArrayList<String> allArtistsCopy = new ArrayList<>(allArtists);

        filterAdapter  = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, allArtistsCopy);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);

        filterBySpinner.setOnItemSelectedListener(filterBySpinnerListener);

    }

     */

    /*
    private void   updateSongsWithAlbumData(){
        counter += 1;
        Log.d("songsPrint", allAlbums.toString());
        for(Song song: allSongs){
            String songAlbumID = song.getAlbumUUDI();
            for (Album album: allAlbums){
                if(album.getAlbumID().equals(songAlbumID)){
                    song.setAlbumName(album.getAlbumName());
                    song.setReleaseDate(album.getReleaseDate());
                    Log.d("songAlbum", songAlbumID + "  "+ album.getAlbumID());
                    Log.d("songAlbum", song.getReleaseDate().toString());
                }
            }

        }

    }

     */

    /*
    private void getAlbumData(){
        CollectionReference albums = db.collection("albums/");
        albums.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Object timestampObject = document.get("releaseDate");
                        com.google.firebase.Timestamp timestamp = (com.google.firebase.Timestamp) timestampObject;
                        long timestampMillis = timestamp.toDate().getTime();
                        Timestamp releaseDate = new Timestamp(timestamp.toDate().getTime());
                        // Use the timestampMillis as needed
                        Log.d("vreme", String.valueOf(releaseDate));

                        Album album = new Album();
                        album.setAlbumName(document.get("albumName").toString());
                        album.setReleaseDate(releaseDate);
                        album.setAlbumID(document.getId().toString());
                        allAlbums.add(album);

                    }

                } else {
                    Log.d("allSongs", "Error getting documents: ", task.getException());
                }
                //updateSongsWithAlbumData();
                DataSingleton.getDataSingleton().setAllSongs(allSongs);
                getAllReleaseYears();
            }
        });
    }
     */

    /*
    private void getSongsDocuments(){
        Log.d("allSongs", usersSongCollRef.toString());

        for(Map.Entry<String, String> entry: usersSongCollRef.entrySet()){
            CollectionReference userSongColl = db.collection("users/" + entry.getKey() + "/songs");
            Log.d("allSongs", "looking for user: " + entry.getKey());


            userSongColl.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("allSongs", document.getId() + " => " + document.getData());

                            Song song = new Song();
                            song.setAlbumUUDI(document.getString("album"));
                            song.setGenre(document.getString("genre"));
                            song.setSongName(document.getString("songName"));
                            song.setSongFileUUID(document.getString("songUUID"));
                            song.setArtistName(entry.getValue());
                            song.setArtistID(entry.getKey());
                            //song.setSongPath(document.get);
                            Log.d("pesma", song.toString());

                            allSongs.add(song);
                        }
                    } else {
                        Log.d("allSongs", "Error getting documents: ", task.getException());
                    }

                    // Finally pass songs to adapter to show them in recycle view
                    setAllSongsAdapter(allSongs);
                    getAlbumData();
                    getAllArtists();
                    getSongsURL();

                    //getAllReleaseYears();

                }
            });
        }

        //getAlbumData();
    }
     */

    /*
    private  void  getSongsURL() {


        for (Song song : allSongs) {
            try {
                StorageReference songRef = storageRef.child("/" + "songs/" + song.getSongFileUUID() + ".mp3");

                songRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUrl) {

                        //Log.d("URLgetzika", song.getSongPath());
                        if(song.getSongPath() == null){
                            song.setSongPath(downloadUrl.toString());
                            numOfFetchedURLs += 1;
                        }
                        //DataSingleton.getDataSingleton().setNumOfFetchedURLs(numOfFetchedURLs);
                        //Log.d("URLgetzika", String.valueOf(DataSingleton.getDataSingleton().getNumOfFetchedURLs()));
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

     */


    /*
    private void  getAllSongs(){
        usersSongCollRef = new HashMap<String, String>();

        // Users collection reference
        CollectionReference usersCollRef = db.collection("users");

        usersCollRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("allSongs", document.getId() + " => " + document.getData());
                        usersSongCollRef.put(document.getId().toString(), document.getString("name"));
                    }
                    getSongsDocuments();
                } else {
                    Log.d("allSongs", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    */


    private void setAllSongsAdapter(ArrayList<Song> songs) {
        songsViewAdapter = new SongsViewAdapter(getContext(), songs, activity);
        allSongsView.setAdapter(songsViewAdapter);
    }

    private void getSongsByArtist(String artistName) {
        artistSongs = new ArrayList<>();
        for (Song song : allSongs) {
            if (song.getArtistName().equals(artistName)) {
                artistSongs.add(song);
            }
        }
    }

    private void getSongsByGenre(String genre) {
        genreSongs = new ArrayList<>();
        for (Song song : allSongs) {
            if (song.getGenre().equals(genre)) {
                genreSongs.add(song);
            }
        }
    }

    private void getSongsByYear(String year) {
        releaseYearSongs = new ArrayList<>();
        for (Song song : allSongs) {

            if (song.getReleaseDate() != null) {
                Timestamp timestamp = song.getReleaseDate();
                Date date = new Date();
                date.setTime(timestamp.getTime());

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
                String releaseYear = simpleDateFormat.format(calendar.getTime()).toString();
                Log.d("byyear", song.getReleaseDate().toString());

                if (releaseYear.equals(year)) {
                    releaseYearSongs.add(song);
                }
            }
        }
        Log.d("byyear", String.valueOf(allSongs.size()));

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        assert activity != null;

        activity.isArtistChange();
        super.onViewCreated(view, savedInstanceState);


        AppCompatImageButton settingsButton = view.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> {

                    activity.settingsFragment.setCalledFromFragment(activity.libraryFragment);
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                            .replace(R.id.main_fragment_container, activity.settingsFragment)
                            .addToBackStack("settings")
                            .commit();
                }
        );

        AppCompatImageButton socialButton = view.findViewById(R.id.social_button);
        socialButton.setOnClickListener(v -> {
            activity.socialFragment.setCalledFromFragment(activity.libraryFragment);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.main_fragment_container, activity.socialFragment)
                    .addToBackStack("settings")
                    .commit();
            System.out.println("Recently played button clicked");
        });

        AppCompatImageButton createPlaylistButton = view.findViewById(R.id.create_playlist_button);
        createPlaylistButton.setOnClickListener(v ->
        {
            activity.playlistCreateFragment.setCalledFromFragment(activity.libraryFragment);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.main_fragment_container, activity.playlistCreateFragment)
                    .commit();
        });

        TextView greeting = view.findViewById(R.id.greeting_message);
        greeting.setText("Hello " + DataSingleton.getDataSingleton().getCurrentUserName());

        counter = 0;

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        allSongs = DataSingleton.getDataSingleton().getAllSongs();
        allAlbums = DataSingleton.getDataSingleton().getAllAlbums();
        allArtists = DataSingleton.getDataSingleton().getAllArtists();
        ArrayList<String> allArtistsCopy = new ArrayList<>(allArtists);

        try {
            Log.d("libsig", allSongs.toString());
            Log.d("libsig", allAlbums.toString());
            Log.d("libsig", allArtists.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        getAllReleaseYears();


        filterBySpinner = view.findViewById(R.id.filterBySpinner);
        filterSpinner = view.findViewById(R.id.filterSpinner);
        filterBtn = view.findViewById(R.id.filter_button);

        filterBtn.setOnClickListener(v -> {
            if (filterBySpinner.getSelectedItem().toString().equals("Artist")) {
                String selectedArtist = filterSpinner.getSelectedItem().toString();
                getSongsByArtist(selectedArtist);
                Log.d("byartist", artistSongs.toString());
                setAllSongsAdapter(artistSongs);
            } else if (filterBySpinner.getSelectedItem().toString().equals("Genre")) {
                String selectedGenre = filterSpinner.getSelectedItem().toString();
                getSongsByGenre(selectedGenre);
                setAllSongsAdapter(genreSongs);
            } else if (filterBySpinner.getSelectedItem().toString().equals("Release year")) {
                String selectedYear = filterSpinner.getSelectedItem().toString();
                //updateSongsWithAlbumData();
                getSongsByYear(selectedYear);
                setAllSongsAdapter(releaseYearSongs);
                Log.d("byartist", allSongs.toString());
            }

        });


        filterBtn.setOnLongClickListener(v -> {
            setAllSongsAdapter(allSongs);
            Toast.makeText(getContext(), "All songs are displayed", Toast.LENGTH_SHORT).show();
            return true;
        });


        allSongsView = view.findViewById(R.id.all_songs_view);
        allSongsViewManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        allSongsView.setLayoutManager(allSongsViewManager);
        setAllSongsAdapter(allSongs);


        filterAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, allArtistsCopy);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);

        filterBySpinner.setOnItemSelectedListener(filterBySpinnerListener);

        numOfFetchedURLs = 0;


    }

    public ArrayList<Song> getAllSongsGetter() {
        return allSongs;
    }

    public ArrayList<Album> getAllAlbumsGetter() {
        return allAlbums;
    }

    public int getNumOfFetchedURLs() {
        return numOfFetchedURLs;
    }

    public void dataUpdate() {
        allSongs = DataSingleton.getDataSingleton().getAllSongs();
        allAlbums = DataSingleton.getDataSingleton().getAllAlbums();
        allArtists = DataSingleton.getDataSingleton().getAllArtists();
        ArrayList<String> allArtistsCopy = new ArrayList<>(allArtists);
        getAllReleaseYears();
        setAllSongsAdapter(allSongs);
    }
}
