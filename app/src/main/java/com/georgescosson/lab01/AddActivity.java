package com.georgescosson.lab01;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int PICK_FROM_GALLERY = 2;

    private DatabaseHelper dbHelper = new DatabaseHelper(AddActivity.this);

    private EditText titleView, tagsView;
    private ImageView imagePreview;

    private String imageDefault;
    private boolean imageIsSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        titleView = (EditText) findViewById(R.id.form_title);
        tagsView = (EditText) findViewById(R.id.form_tags);
        imagePreview = (ImageView) findViewById(R.id.form_image);
    }

    public void validateForm(View view) {

        String title = titleView.getText().toString();
        String tags = tagsView.getText().toString();

        // Check if form is valid
        if(imageIsSet) {

            if(title.length() == 0){
                title = imageDefault;
            }
            Toast.makeText(AddActivity.this, "Form is valid", Toast.LENGTH_LONG).show();
            ImageView imagePreview = (ImageView) findViewById(R.id.form_image);
            Bitmap bitmap = ((BitmapDrawable)imagePreview.getDrawable()).getBitmap();

            GalleryImage newImage = new GalleryImage(title, tags, bitmap);
            newImage.print();

            //Pass image back
            long index = dbHelper.addEntry(newImage);
            Intent intent = new Intent();
            intent.putExtra("createdIndex", index);
            setResult(200, intent);
            finish();


        } else {
            Toast.makeText(AddActivity.this, "Form not valid", Toast.LENGTH_LONG).show();
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
            Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);

            // Store image to DB
            imagePreview.setImageBitmap(imageBitmap);
            this.imageDefault = filename;
            this.imageIsSet = true;
        } else {
            Toast.makeText(AddActivity.this, "You haven't picked an image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case PICK_FROM_GALLERY:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                    Toast.makeText(AddActivity.this, "We need access to properly run", Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    public void selectImagesFromStorage(View view) {

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
