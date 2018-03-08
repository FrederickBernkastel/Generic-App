package com.example.frederic.genericapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

public class TrollActivity extends AppCompatActivity {


    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troll);
        String url = "https://i.imgur.com/tGbaZCY.jpg";

        // Add image to layout made in xml
        LinearLayout layout = (LinearLayout) findViewById(R.id.imageLayout);
        ImageView urlImage = new ImageView(this);

        layout.addView(urlImage);

        ImageResize.loadImageByUrl(TrollActivity.this, url, urlImage,1000,1000);
    }


}
