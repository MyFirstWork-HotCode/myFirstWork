package com.myfirstwork.myfirstwork;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class LentaActivity extends AppCompatActivity implements Button.OnClickListener {
    int count = 0;
    VideoView videoView;
    Button bloger,finder,work;
    DisplayMetrics displayMetrics;
   // ImageButton imageButton;
    BottomNavigationView bottomNavigationView;
    ProgressBar progressBar;
    ArrayList<String> pathVideos = new ArrayList<>();
    int height;
    Handler handler = new Handler();
    private GestureDetector gestureDetector;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lenta);
        displayMetrics = getResources().getDisplayMetrics();
        gestureDetector = new GestureDetector(this,new GestureListener());
        findIDResourse();
        setOnClickListener();
        makeListVideo();
        height=displayMetrics.heightPixels-(int)(90*2*displayMetrics.scaledDensity);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.camera:
                        Intent intent = new Intent(LentaActivity.this,CameraActivity.class);
                        startActivity(intent);
                }
                return false;
            }
        });
        setButtonSelect(finder);
        videoView = findViewById(R.id.video);
        setVideoView(pathVideos);
        videoView.setOnClickListener(videoListener);
        Log.d("HeightMenu", String.valueOf(finder.getHeight()));
        Log.d("HeightMax", String.valueOf(displayMetrics.heightPixels));
        Log.d("Height", String.valueOf(height));
        Log.d("Width", String.valueOf(videoView.getWidth()));
        //imageButton.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bloger:
                setButtonSelect(view);
                setButtonNoSelect(finder);
                setButtonNoSelect(work);
                break;
            case R.id.finder:
                setButtonSelect(view);
                setButtonNoSelect(bloger);
                setButtonNoSelect(work);
                break;
            case R.id.work:
                setButtonSelect(view);
                setButtonNoSelect(finder);
                setButtonNoSelect(bloger);
                break;
        }
    }
    private void setButtonSelect(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(getColor(R.color.black_select));
        }
    }
    private void setButtonNoSelect(View view){
        view.setBackgroundColor(Color.BLACK);
    }

    private void makeListVideo(){     //create list https
        pathVideos.add("https://getfile.dokpub.com/yandex/get/https://yadi.sk/i/8lqi5zJXF27i0Q");
        pathVideos.add("https://getfile.dokpub.com/yandex/get/https://yadi.sk/i/tMUqA9WVbEaCWg");
        pathVideos.add("https://getfile.dokpub.com/yandex/get/https://yadi.sk/i/PWPqRkS8CCe6Hg");
        pathVideos.add("https://getfile.dokpub.com/yandex/get/https://yadi.sk/i/3UeJXH38B-vq7Q");
    }
    private void findIDResourse(){    //find resourse
        bloger=findViewById(R.id.bloger);
        finder=findViewById(R.id.finder);
        work=findViewById(R.id.work);
        progressBar=findViewById(R.id.progress_bar);
        //imageButton=findViewById(R.id.start_stop);
        bottomNavigationView=findViewById(R.id.bottom_menu);
        bottomNavigationView.setSelectedItemId(R.id.lenta);
    }
    private void setOnClickListener(){
        bloger.setOnClickListener(this);
        finder.setOnClickListener(this);
        work.setOnClickListener(this);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setVideoView(final ArrayList<String> videoPath){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(displayMetrics.heightPixels-(180*displayMetrics.scaledDensity)));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoView.setLayoutParams(params);
        videoView.setKeepScreenOn(true);
        videoView.setVideoURI(Uri.parse(videoPath.get(count)));
        videoView.requestFocus();
        videoView.seekTo(1);
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                setVideoView(videoPath);
                return false;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        progressBar.setVisibility(View.GONE);
                        //imageButton.setVisibility(View.VISIBLE);
                        onClick(videoView);
                    }
                });
    }
    View.OnClickListener videoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.video:
                    if(videoView.isPlaying()) {
                        //visiblePause();
                        videoView.pause();
                        Toast.makeText(LentaActivity.this, "PAUSE VIDEO", Toast.LENGTH_SHORT).show();
                    }
                    else if(videoView.canPause()) {
                       // imageButton.setImageResource(R.drawable.pause);
                        videoView.start();
                        //visibleStart();
                        Toast.makeText(LentaActivity.this, "PLAY VIDEO", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    private void nextVideo(){
        videoView.setVideoURI(Uri.parse(pathVideos.get(count)));
        videoView.seekTo(1);
    }
//    private void visiblePause(){
//        final Animation animation = AnimationUtils.loadAnimation(this,R.anim.invisibal);
//        imageButton.setImageResource(R.drawable.pause);
//        imageButton.setVisibility(View.VISIBLE);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                imageButton.setAnimation(animation);
//                imageButton.setVisibility(View.GONE);
//            }
//        },1500);
//    }
//    private void visibleStart(){
//        final Animation animation = AnimationUtils.loadAnimation(this,R.anim.invisibal);
//        imageButton.setImageResource(R.drawable.play);
//        imageButton.setVisibility(View.VISIBLE);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                imageButton.setAnimation(animation);
//                imageButton.setVisibility(View.GONE);
//            }
//        },1500);
//    }

    private class GestureListener extends android.view.GestureDetector.SimpleOnGestureListener{

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX()-e2.getY()>SWIPE_MIN_DISTANCE && Math.abs(velocityX)>SWIPE_THRESHOLD_VELOCITY){
                Toast.makeText(LentaActivity.this, "Right to left", Toast.LENGTH_SHORT).show();
                count++;
                videoView.pause();
                return true; // Right to left
            }
            else if(e2.getX()-e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX)>SWIPE_THRESHOLD_VELOCITY){
                Toast.makeText(LentaActivity.this, "Left to right", Toast.LENGTH_SHORT).show();
                count++;
                videoView.pause();
                return true; // Left to right
            }
            if(e1.getY()-e2.getY()>SWIPE_MIN_DISTANCE && Math.abs(velocityY)>SWIPE_THRESHOLD_VELOCITY){
                Toast.makeText(LentaActivity.this, "Bottom to top", Toast.LENGTH_SHORT).show();
                return false; // Bottom to top
            }
            else if(e2.getY()-e1.getY()>SWIPE_MIN_DISTANCE && Math.abs(velocityY)>SWIPE_THRESHOLD_VELOCITY){
                Toast.makeText(LentaActivity.this, "Top to bottom", Toast.LENGTH_SHORT).show();
                return false; // Top to bottom
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
