package com.myfirstwork.myfirstwork;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.net.URI;
import java.util.Objects;

public class PreviewActivity extends AppCompatActivity {

    VideoView videoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_video);
        Bundle bundle = getIntent().getExtras();
        videoView=findViewById(R.id.video_preview);
        File file = new File(Objects.requireNonNull(bundle.getString("path")));
        videoView.setVideoPath(file.getAbsolutePath());
        videoView.seekTo(1);
        videoView.start();

    }
}
