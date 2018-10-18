package com.georgescosson.lab01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import junit.framework.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int PICK_FROM_GALLERY = 2;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private Button zoomButton;
    private ImageAdapter adapter;

    private int currentSpanNumber;

    ArrayList<GalleryImage> galleryImages;

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

    public void selectImagesFromStorage(View view) {
        Log.d("debug", "selectImagesfromStorage");
        try {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
            } else {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        Log.d("TEST", "Setting span to :" + String.valueOf(number));
        this.currentSpanNumber = number;
        layoutManager = new GridLayoutManager(getApplicationContext(), currentSpanNumber);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.d("debug", "OnRequestPermissionsResult");
        switch (requestCode) {
            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                    Toast.makeText(MainActivity.this, "We need access to properly run", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("debug", "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            String[] path = picturePath.split("/");
            String filename = path[path.length - 1];
            cursor.close();
            Toast.makeText(MainActivity.this, "Your image has been added", Toast.LENGTH_LONG).show();
            Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);

            // Store image to DB
            String title = filename;
            GalleryImage newImage = new GalleryImage(title, imageBitmap);
            dbHelper.addEntry(newImage);
            adapter.addImage(newImage);

        } else {
            Toast.makeText(MainActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    public void deleteAllFromDB(View view) {
        dbHelper.deleteAll();
        adapter.removeAll();
    }

    public void goToForm(View view) {
        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);
    }
}
