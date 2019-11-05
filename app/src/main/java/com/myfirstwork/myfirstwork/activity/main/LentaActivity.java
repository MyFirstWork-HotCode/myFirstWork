package com.myfirstwork.myfirstwork.activity.main;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.myfirstwork.myfirstwork.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LentaActivity extends AppCompatActivity implements Button.OnClickListener {
    int count = 0;
    VideoView videoView;
    Button bloger,finder,work;
    DisplayMetrics displayMetrics;
    BottomNavigationView bottomNavigationView;
    ProgressBar progressBar;
    ImageView like,dislike;
    TextView textLike,textDiss;
    ArrayList<String> pathVideos = new ArrayList<>();
    int height;
    Handler handler = new Handler();
    private GestureDetector gestureDetector;
    Animation animation, videoAnimation;

    private String [] child = {"vacansi/", "finder/","blog/"};
    private int position = 0;
    private FirebaseFirestore firestore;
    private ArrayList<Map<String,Object>> videos = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lenta);
        displayMetrics = getResources().getDisplayMetrics();
        firestore = FirebaseFirestore.getInstance();
        gestureDetector = new GestureDetector(this,new GestureListener());
        animation = AnimationUtils.loadAnimation(this,R.anim.scale_like);
        videoAnimation = AnimationUtils.loadAnimation(this,R.anim.video_animation);
        findIDResourse();
        setOnClickListener();
        makeListVideo();
        firestore.collection("videos")
                .whereEqualTo("child",child[position])
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult() ){
                        Map<String,Object> map = new HashMap<>();
                        Log.d("READ", snapshot.getId() + " => " + snapshot.getData());
                        map=snapshot.getData();
                        videos.add(map);
                    }
                }else {
                        Log.d("READ", "Error getting documents: ", task.getException());
                }
            }
        });
        height=displayMetrics.heightPixels-(int)(90*2*displayMetrics.scaledDensity);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()){
                    case R.id.camera:
                        intent = new Intent(LentaActivity.this, CameraActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.history:
                        intent = new Intent(LentaActivity.this, History.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
        setButtonSelect(finder);
        videoView = findViewById(R.id.video);
       // setVideoView(pathVideos);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bloger:
                position = 2;
                setButtonSelect(view);
                setButtonNoSelect(finder);
                setButtonNoSelect(work);
                break;
            case R.id.finder:
                position = 1;
                setButtonSelect(view);
                setButtonNoSelect(bloger);
                setButtonNoSelect(work);
                break;
            case R.id.work:
                position = 0;
                setButtonSelect(view);
                setButtonNoSelect(finder);
                setButtonNoSelect(bloger);
                break;
            case R.id.diss:
                dislike.startAnimation(animation);
                videoView.startAnimation(videoAnimation);
                nextVideo();
                break;
            case R.id.like:
                like.startAnimation(animation);
                videoView.startAnimation(videoAnimation);
                nextVideo();
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
//        pathVideos.add("https://getfile.dokpub.com/yandex/get/https://yadi.sk/i/8lqi5zJXF27i0Q");
//        pathVideos.add("https://getfile.dokpub.com/yandex/get/https://yadi.sk/i/tMUqA9WVbEaCWg");
//        pathVideos.add("https://getfile.dokpub.com/yandex/get/https://yadi.sk/i/PWPqRkS8CCe6Hg");
//        pathVideos.add("https://getfile.dokpub.com/yandex/get/https://yadi.sk/i/3UeJXH38B-vq7Q");
//        pathVideos.add("/storage/emulated/0/DCIM/Camera/VID_20191024_201417.mp4");
//        pathVideos.add("/storage/emulated/0/DCIM/Camera/VID_20191024_201352.mp4");
//        pathVideos.add("/storage/emulated/0/DCIM/Camera/VID_20191024_201410.mp4");
//        pathVideos.add("/storage/emulated/0/DCIM/Camera/VID_20191024_201401.mp4");
    }
    private void findIDResourse(){    //find resourse
        like=findViewById(R.id.like);
        textDiss=findViewById(R.id.diss_text);
        textLike=findViewById(R.id.like_text);
        dislike=findViewById(R.id.diss);
        bloger=findViewById(R.id.bloger);
        finder=findViewById(R.id.finder);
        work=findViewById(R.id.work);
        progressBar=findViewById(R.id.progress_bar);
        bottomNavigationView=findViewById(R.id.bottom_menu);
        bottomNavigationView.setSelectedItemId(R.id.lenta);
    }
    private void setOnClickListener(){
        bloger.setOnClickListener(this);
        finder.setOnClickListener(this);
        work.setOnClickListener(this);
        like.setOnClickListener(this);
        dislike.setOnClickListener(this);
        textLike.setOnClickListener(this);
        textDiss.setOnClickListener(this);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setVideoView(final ArrayList<String> videoPath){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int)(displayMetrics.heightPixels-(180*displayMetrics.scaledDensity)));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoView.setLayoutParams(params);
        videoView.setVideoURI(Uri.parse(videoPath.get(count)));
        videoView.requestFocus();
        videoView.seekTo(1);
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if(videoView.isPlaying()) {
                        videoView.pause();
                        Toast.makeText(LentaActivity.this, "PAUSE VIDEO", Toast.LENGTH_SHORT).show();
                    }
                    else if(videoView.canPause()) {
                        videoView.start();
                        Toast.makeText(LentaActivity.this, "PLAY VIDEO", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        progressBar.setVisibility(View.GONE);
                        onClick(videoView);
                    }
                });
    }

    private void nextVideo(){
        videoView.setVideoURI(Uri.parse(pathVideos.get(count)));
        videoView.seekTo(1);
    }
    private void scaleDisslike(){
        final Animation animation = AnimationUtils.loadAnimation(this,R.anim.scale_like);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textDiss.startAnimation(animation);
            }
        },2000);
    }
    private void scaleLike(){
        final Animation animation = AnimationUtils.loadAnimation(this,R.anim.scale_like);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textLike.startAnimation(animation);
            }
        },2000);
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
                setVideoView(pathVideos);
                return true; // Right to left
            }
            else if(e2.getX()-e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX)>SWIPE_THRESHOLD_VELOCITY){
                Toast.makeText(LentaActivity.this, "Left to right", Toast.LENGTH_SHORT).show();
                count++;
                setVideoView(pathVideos);
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
