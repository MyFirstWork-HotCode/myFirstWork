package com.myfirstwork.myfirstwork;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.media.VideoView;

public class LentaActivity extends AppCompatActivity{
    VideoView videoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lenta_activity);
        String videoSource ="https://youtu.be/WtsuJXYWKcQ";

    }

}
