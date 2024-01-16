package com.example.musicapp.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.musicapp.entities.DataSingleton;
import com.example.musicapp.R;
import com.example.musicapp.activities.MainActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadSongFragment extends Fragment {

    private String songPath;
    private TextView fileToUpload;
    private EditText songName, albumName, albumDatePicker;
    private int calDay, calMonth, calYear;
    private String dateString;
    private Spinner genre, album;
    private int year, month, day;
    private CheckBox newAlbumCheck;
    private LinearLayout albumNameLay, albumDateLay;
    private DatePickerDialog picker;
    private Uri uri;
    private File songFile;
    protected FirebaseAuth auth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String albumUUID;
    private UUID uuidAlbum;
    private String uuidAlbumString;
    private TextView albumSelectLabel;
    private MainActivity activity;

    public UploadSongFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_song, container, false);
    }


    // File picker callback
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Log.d("DATA", data.toString());
                        uri = data.getData();

                        fileToUpload.setText(uri.getPath());
                        Toast.makeText(getContext(), "Music file selected", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.isArtistChange();




        /*
        MaterialToolbar toolbar = view.findViewById(R.id.top_bar);
        toolbar.setNavigationOnClickListener(v -> activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_container, activity.homeFragment)
                .commit());
         */

        // Current user firebase storage reference
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        DocumentReference docRef = db.collection("users").document(currentUser.getUid());

        // Firebase storage instance and root reference
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        fileToUpload = view.findViewById(R.id.fragment_upload_upload_file);
        songName = view.findViewById(R.id.fragment_upload_song_name);
        genre = view.findViewById(R.id.fragment_upload_song_genre_spinner);
        album = view.findViewById(R.id.fragment_upload_song_album_spinner);
        newAlbumCheck = view.findViewById(R.id.album_checkBox);
        albumNameLay = view.findViewById(R.id.album_name_layout);
        albumDateLay = view.findViewById(R.id.album_date_layout);
        albumName = view.findViewById(R.id.fragment_upload_album_name);
        albumDatePicker = view.findViewById(R.id.fragment_upload_album_date);
        albumSelectLabel = view.findViewById(R.id.albumSelectLabel);

        CollectionReference albumsColl = db.collection("albums");
        ArrayList<String> albumList = new ArrayList<>();
        Map<String, String> albumListWithIDs = new HashMap<>();
        albumsColl.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d("currentUserUp", DataSingleton.getDataSingleton().getCurrentUserID());
                    if (document.getString("artistID").equals(DataSingleton.getDataSingleton().getCurrentUserID())) {
                        albumList.add(document.getString("albumName"));
                        albumListWithIDs.put(document.getString("albumName"), document.getId().toString());

                    }

                    //Log.d(TAG, document.getId() + " => " + document.getData());
                }
                Log.d("album", albumList.toString());
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, albumList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Apply the adapter to the spinner
                album.setAdapter(arrayAdapter);
            } else {
                //Log.d(TAG, "Error getting documents: ", task.getException());
            }
            Log.d("albums", albumListWithIDs.toString());
        });

        albumDatePicker.setOnClickListener(v -> {
            final Calendar cldr = Calendar.getInstance();
            day = cldr.get(Calendar.DAY_OF_MONTH);
            month = cldr.get(Calendar.MONTH);
            year = cldr.get(Calendar.YEAR);
            // date picker dialog
            picker = new DatePickerDialog(getActivity(),
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        dateString = String.join("/", String.valueOf(dayOfMonth), String.valueOf(monthOfYear + 1), String.valueOf(year));
                        albumDatePicker.setText(dateString);
                        Log.d("datum", dateString);
                        calDay = dayOfMonth;
                        calMonth = monthOfYear + 1;
                        calYear = year;
                    }, year, month, day);
            picker.show();
        });

        // Browse button click listener
        Button browseBtn = view.findViewById(R.id.fragment_upload_song_browse_btn);
        browseBtn.setOnClickListener(v -> {
            // Define the intent to open the file picker
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*"); // Set the MIME type of the files to select

            someActivityResultLauncher.launch(intent);

        });

        newAlbumCheck.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                albumNameLay.setVisibility(View.VISIBLE);
                albumDateLay.setVisibility(View.VISIBLE);
                album.setVisibility(View.GONE);
                albumSelectLabel.setVisibility(View.GONE);
            } else {
                albumNameLay.setVisibility(View.GONE);
                albumDateLay.setVisibility(View.GONE);
                album.setVisibility(View.VISIBLE);
                albumSelectLabel.setVisibility(View.VISIBLE);
            }
        });


        // Upload btn listener and upload of song
        Button uploadBtn = view.findViewById(R.id.fragment_upload_upload_song_btn);
        uploadBtn.setOnClickListener(v -> {


            // If new album is selected, first create new album in database
            if (newAlbumCheck.isChecked()) {
                uuidAlbum = UUID.randomUUID();
                uuidAlbumString = uuidAlbum.toString();


                Map<String, Object> albumData = new HashMap<>();
                albumData.put("albumName", albumName.getText().toString());

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date date = dateFormat.parse(dateString);

                    long time = date.getTime();
                    Timestamp timestamp = new Timestamp(date);
                    albumData.put("releaseDate", timestamp);
                    albumData.put("artistID", DataSingleton.getDataSingleton().getCurrentUserID());
                    Log.d("date", dateString);

                } catch (Exception e) {
                    Log.e("parse date", "ERROR");
                }

                albumsColl.document(uuidAlbumString).set(albumData)
                        .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));

                /*
                albumsColl.add(albumData).addOnSuccessListener(documentReference ->{
                                Log.d("TAG", "Document added with ID: " + documentReference.getId());
                                albumUUID = documentReference.getId();
                }).addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));

                 */

                // Update album picker
                albumsColl.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        albumList.clear();
                        albumListWithIDs.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("artistID").equals(DataSingleton.getDataSingleton().getCurrentUserID())) {
                                albumList.add(document.getString("albumName"));
                                albumListWithIDs.put(document.getString("albumName"), document.getId().toString());

                            }

                            //Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        Log.d("album", albumList.toString());
                        // Create an ArrayAdapter using the string array and a default spinner layout
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, albumList);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // Apply the adapter to the spinner
                        album.setAdapter(arrayAdapter);
                    } else {
                        //Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    Log.d("albums", albumListWithIDs.toString());
                });
            }
            // Get selected album UUID
            else {
                String selectedAlbumName = album.getSelectedItem().toString();
                uuidAlbumString = albumListWithIDs.get(selectedAlbumName);

            }

            // Create UUID
            UUID uuid = UUID.randomUUID();
            String uuidString = uuid.toString();

            //Make storage reference with uuid as the file name on the cloud
            StorageReference song = storageRef.child("songs/" + uuidString + ".mp3");
            Toast.makeText(getContext(), "Song upload started", Toast.LENGTH_SHORT).show();
            //Uri file = Uri.fromFile(songFile);
            UploadTask uploadTask = song.putFile(uri);
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(exception -> {
                // Handle unsuccessful uploads
                Log.d("TAG", "Upload failed");
                Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_LONG).show();
            }).addOnSuccessListener(taskSnapshot -> {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Log.d("TAG", "Upload successful");
                Toast.makeText(getContext(), "Song uploaded successfully", Toast.LENGTH_SHORT).show();

                // EVENT
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("creator", DataSingleton.getDataSingleton().getCurrentUserID());
                eventData.put("type", "upload");
                eventData.put("description", DataSingleton.getDataSingleton().getCurrentUserName() + " uploaded " + songName.getText().toString() + " song");

                UUID uuidEvent = UUID.randomUUID();
                String uuidEventString = uuidEvent.toString();
                CollectionReference events = db.collection("events");
                events.document(uuidEventString).set(eventData).addOnSuccessListener(aVoid -> {
                    // Refresh backend data
                    activity.getCurrentUserData();
                    activity.getAllBackendData();


                    Toast.makeText(getContext(), "Event Created", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Log.w("TAG", "Error writing event", e));

                updateAlbumSpinner();
                resetFields();
            });

            // Update user document to reference the uploaded song
            CollectionReference subCollectionRef = docRef.collection("songs");
            Map<String, String> data = new HashMap<>();
            data.put("songUUID", uuidString);
            data.put("songName", songName.getText().toString());
            data.put("genre", genre.getSelectedItem().toString());
            data.put("album", uuidAlbumString);
            data.put("numberOfLikes", "0");
            data.put("numberOfListens", "0");

            subCollectionRef.add(data)
                    .addOnSuccessListener(documentReference -> Log.d("TAG", "Document added with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));


        });

    }

    private void updateAlbumSpinner() {
        CollectionReference albumsColl = db.collection("albums");
        ArrayList<String> albumList = new ArrayList<>();
        Map<String, String> albumListWithIDs = new HashMap<>();
        albumsColl.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getString("artistID").equals(DataSingleton.getDataSingleton().getCurrentUserID())) {
                        albumList.add(document.getString("albumName"));
                        albumListWithIDs.put(document.getString("albumName"), document.getId().toString());

                    }

                    //Log.d(TAG, document.getId() + " => " + document.getData());
                }
                Log.d("album", albumList.toString());
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, albumList);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // Apply the adapter to the spinner
                album.setAdapter(arrayAdapter);
            } else {
                //Log.d(TAG, "Error getting documents: ", task.getException());
            }
            Log.d("albums", albumListWithIDs.toString());
        });
    }

    private void resetFields() {
        uri = null;
        fileToUpload.setText("");
        songName.setText("");
        genre.setSelection(0);
        albumName.setText("");
        albumDatePicker.setText("");
        album.setVisibility(View.GONE);
        albumSelectLabel.setVisibility(View.GONE);
        albumDateLay.setVisibility(View.GONE);
        newAlbumCheck.setChecked(false);
    }
}