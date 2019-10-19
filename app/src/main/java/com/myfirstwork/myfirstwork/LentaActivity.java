package com.myfirstwork.myfirstwork;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class LentaActivity extends AppCompatActivity implements Button.OnClickListener {
    float x;
    float y;
    float[] sDown = new float[2],sUp=new float[2];
    VideoView videoView;
    Button bloger,finder,work;
    DisplayMetrics displayMetrics;
    ImageButton imageButton;
    BottomNavigationView bottomNavigationView;
    ProgressBar progressBar;
    //String path = "https://radikal.ru/vf/tCjVkzcaCAW";
    String path="https://getfile.dokpub.com/yandex/get/https://yadi.sk/i/UcUt-b0Q7mWdtw";
    int height;
    Handler handler = new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lenta_activity);
        displayMetrics = getResources().getDisplayMetrics();
        bloger=findViewById(R.id.bloger);
        finder=findViewById(R.id.finder);
        work=findViewById(R.id.work);
        progressBar=findViewById(R.id.progress_bar);
        imageButton=findViewById(R.id.start_stop);
        bottomNavigationView=findViewById(R.id.bottom_menu);
        height=displayMetrics.heightPixels-(int)(90*2*displayMetrics.scaledDensity);
        bloger.setOnClickListener(this);
        finder.setOnClickListener(this);
        work.setOnClickListener(this);
       // bottomNavigationView.getViewTreeObserver().addOnPreDrawListener(onPreDrawListener);
        bottomNavigationView.setSelectedItemId(R.id.lenta);
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
        setVideoView(path);
        Log.d("HeightMenu", String.valueOf(finder.getHeight()));
        Log.d("HeightMax", String.valueOf(displayMetrics.heightPixels));
        Log.d("Height", String.valueOf(height));
        Log.d("Width", String.valueOf(videoView.getWidth()));
        imageButton.setVisibility(View.GONE);
    }

//    ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
//        @Override
//        public boolean onPreDraw() {
//            Log.d("HeightBottom", String.valueOf(bottomNavigationView.getHeight()/displayMetrics.scaledDensity));
//            return true;
//        }
//    };
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

    private void setVideoView(String videoPath){

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(displayMetrics.heightPixels-(180*displayMetrics.scaledDensity)));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoView.setLayoutParams(params);
        videoView.setOnClickListener(videoListener);
        videoView.setKeepScreenOn(true);
        videoView.setVideoURI(Uri.parse(videoPath));
        videoView.requestFocus();
        videoView.seekTo(1);
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        sDown[0] =x;
                        sDown[1]=y;
                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                        sUp[0] = x;
                        sUp[1] = y;
                        break;
                }
                if(sDown[0]>sUp[0]){
                    Log.e("SWIPE","Left"+String.valueOf(sDown[0]-sUp[0]));
                }
                else if(sDown[0]<sUp[0]){
                    Log.e("SWIPE","Right"+String.valueOf(sDown[0]-sUp[0]));
                }
                sDown[0]=0;sUp[0]=0;
                return false;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
                imageButton.setVisibility(View.VISIBLE);
                onClick(videoView);
            }
        });
        //videoView.start();
    }
    View.OnClickListener videoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.video:
                    if(videoView.isPlaying()) {
                        visiblePause();
                        videoView.pause();
                    }
                    else if(videoView.canPause()) {
                       // imageButton.setImageResource(R.drawable.pause);
                        videoView.start();
                        visibleStart();
                    }
                    break;
            }
        }
    };

    private void visiblePause(){
        final Animation animation = AnimationUtils.loadAnimation(this,R.anim.invisibal);
        imageButton.setImageResource(R.drawable.pause);
        imageButton.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageButton.setAnimation(animation);
                imageButton.setVisibility(View.GONE);
            }
        },1500);
    }
    private void visibleStart(){
        final Animation animation = AnimationUtils.loadAnimation(this,R.anim.invisibal);
        imageButton.setImageResource(R.drawable.play);
        imageButton.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageButton.setAnimation(animation);
                imageButton.setVisibility(View.GONE);
            }
        },1500);
    }
}
