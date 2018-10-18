package com.georgescosson.lab01;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 10;
    private static final int PICK_FROM_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

    }

    public void validateForm(View view) {
        EditText titleView = (EditText) findViewById(R.id.form_title);
        String title = titleView.getText().toString();
        EditText tagsView = (EditText) findViewById(R.id.form_tags);
        String tags = tagsView.getText().toString();
        Log.d("DEBUG", "title : " + title + ", tags : " + tags);
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
            Toast.makeText(AddActivity.this, "Your image has been added", Toast.LENGTH_LONG).show();
            Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);

            // Store image to DB
            ImageView imagePreview = (ImageView) findViewById(R.id.form_image);
            imagePreview.setImageBitmap(imageBitmap);

        } else {
            Toast.makeText(AddActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    public void selectImagesFromStorage(View view) {
        Log.d("debug", "selectImagesfromStorage");
        try {
            if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
            } else {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
