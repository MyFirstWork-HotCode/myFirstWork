package com.myfirstwork.myfirstwork.activity.post;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.myfirstwork.myfirstwork.R;
import com.myfirstwork.myfirstwork.activity.post.adapter.AdapterGallery;

import java.io.File;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG =" PreviewActivity" ;
    VideoView videoView;
    Gallery gallery;
    AdapterGallery adapterGallery;
    Button button;
    DisplayMetrics displayMetrics;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_video);
        Bundle bundle = getIntent().getExtras();
        displayMetrics=getResources().getDisplayMetrics();
        videoView=findViewById(R.id.video_preview);
        gallery = findViewById(R.id.gallery);
        button = findViewById(R.id.post);
        adapterGallery = new AdapterGallery(this);
        gallery.setAdapter(adapterGallery);
        //gallery.setSelection(gallery.getCount()/2);
        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int posit;
            @SuppressLint("Range")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getSelectedView().setBackgroundColor(Color.BLUE);
                posit=position;
                Log.i(LOG_TAG, String.valueOf(position));
            }


            @SuppressLint("Range")
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        File file = new File(bundle.getString("video"));
        setVideoView(file.getAbsolutePath());
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gallery:

                break;
        }
    }

    private void setVideoView(String path){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int)(displayMetrics.heightPixels-(180*displayMetrics.scaledDensity)));
        layoutParams.gravity= Gravity.CENTER;
        videoView.setLayoutParams(layoutParams);
        videoView.setVideoPath(path);
        videoView.seekTo(1);
        videoView.start();
    }
}
