package com.rainbean.bigimageview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private BigImageView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = findViewById(R.id.big_image);

        InputStream inputStream = null;
        inputStream = getResources().openRawResource(R.raw.img);

        view.setImage(inputStream);
    }
}
