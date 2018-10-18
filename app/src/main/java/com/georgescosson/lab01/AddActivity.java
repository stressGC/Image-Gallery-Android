package com.georgescosson.lab01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

    }

    public void validateForm(View view) {
        EditText titleView = (EditText) findViewById(R.id.form_title);
        String title = titleView.getText().toString();
        EditText tagsView = (EditText) findViewById(R.id.form_tags);
        String tags = tagsView.getText().toString();git
        Log.d("DEBUG", "title : " + title + ", tags : " + tags);
    }
}
