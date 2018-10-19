package com.georgescosson.lab01;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private Button zoomButton;
    private ImageAdapter adapter;

    private int currentSpanNumber;

    private ArrayList<GalleryImage> galleryImages;

    private DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        zoomButton = findViewById(R.id.zoom);
        recyclerView.setHasFixedSize(true);
        currentSpanNumber = 2;
        layoutManager = new GridLayoutManager(getApplicationContext(), currentSpanNumber);
        recyclerView.setLayoutManager(layoutManager);
        galleryImages = loadData();
        adapter = new ImageAdapter(getApplicationContext(), galleryImages);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<GalleryImage> loadData() {
        return dbHelper.getEntries();
    }

    public void changeSpanNumber(View view) {
        int number;

        switch(currentSpanNumber){
            case 1:
                number = 2;
                break;
            case 2:
                number = 4;
                break;
            default:
                number = 1;
        }

        this.currentSpanNumber = number;
        layoutManager = new GridLayoutManager(getApplicationContext(), currentSpanNumber);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void deleteAllFromDB(View view) {
        dbHelper.deleteAll();
        adapter.removeAll();
    }

    public void goToForm(View view) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE) {
            if(resultCode == 200){
                long createdIndex = data.getLongExtra("createdIndex", -1);
                if(createdIndex != -1){
                    this.addNewImageToView(createdIndex);
                } else {
                    Toast.makeText(MainActivity.this, "An error occured", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void addNewImageToView(long createdIndex) {
        GalleryImage newImage = dbHelper.getEntry(createdIndex);

        if(newImage != null){
            galleryImages.add(newImage);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(MainActivity.this, "An error occured", Toast.LENGTH_LONG).show();
        }
    }

}
