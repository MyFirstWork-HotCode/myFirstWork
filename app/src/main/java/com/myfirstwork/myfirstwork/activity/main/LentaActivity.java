package com.myfirstwork.myfirstwork.activity.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myfirstwork.myfirstwork.R;

import java.io.File;
import java.io.IOException;
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
    ImageView like,dislike,avatar;
    TextView textLike,textDiss,textInfoWork,date,name,postUser;
    int height;
    Handler handler = new Handler();
    Animation animation, videoAnimation;

    private String [] child = {"finder/","vacansi/","blog/"};
    private int position = 0;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ArrayList<Map<String,Object>> videos = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lenta);
        displayMetrics = getResources().getDisplayMetrics();
        height=displayMetrics.heightPixels-(int)(90*2*displayMetrics.scaledDensity);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        animation = AnimationUtils.loadAnimation(this,R.anim.scale_like);
        videoAnimation = AnimationUtils.loadAnimation(this,R.anim.video_animation);
        findIDResourse();
        setOnClickListener();
        setButtonSelect(finder);

        queryFireBase();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()){
                    case R.id.camera:
                        intent = new Intent(LentaActivity.this, CameraActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.history:
                        intent = new Intent(LentaActivity.this, History.class);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bloger:
                position = 2;
                setButtonSelect(view);
                setButtonNoSelect(finder);
                setButtonNoSelect(work);
                videoView.pause();
                videos.clear();
                count=0;
                setVisibleGone();
                queryFireBase();
                break;
            case R.id.finder:
                position = 0;
                setButtonSelect(view);
                setButtonNoSelect(bloger);
                setButtonNoSelect(work);
                videoView.pause();
                videos.clear();
                count=0;
                setVisibleGone();
                queryFireBase();
                break;
            case R.id.work:
                position = 1;
                setButtonSelect(view);
                setButtonNoSelect(finder);
                setButtonNoSelect(bloger);
                videoView.pause();
                videos.clear();
                count=0;
                setVisibleGone();
                queryFireBase();
                break;
            case R.id.diss:
                videoView.pause();
                dislike.startAnimation(animation);
                videoView.startAnimation(videoAnimation);
                setVisibleGone();
                count++;
                try {
                    nextVideo((String) videos.get(count).get("path"));
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(this, "Новых записей нет записей", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.like:
                videoView.pause();
                like.startAnimation(animation);
                videoView.startAnimation(videoAnimation);
                setVisibleGone();
                count++;
                try {
                    nextVideo((String) videos.get(count).get("path"));
                }catch (IndexOutOfBoundsException e){
                    Toast.makeText(this, "Новых записей нет записей", Toast.LENGTH_SHORT).show();
                }

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

    private void findIDResourse(){    //find resourse

        like=findViewById(R.id.like);
        avatar=findViewById(R.id.avatar_img);
        date=findViewById(R.id.date);
        name=findViewById(R.id.name);
        postUser=findViewById(R.id.post_user);
        textInfoWork=findViewById(R.id.info_work);
        textDiss=findViewById(R.id.diss_text);
        textLike=findViewById(R.id.like_text);
        dislike=findViewById(R.id.diss);
        videoView = findViewById(R.id.video);
        bloger=findViewById(R.id.bloger);
        finder=findViewById(R.id.finder);
        work=findViewById(R.id.work);
        progressBar=findViewById(R.id.progress_bar);
        bottomNavigationView=findViewById(R.id.bottom_menu);
        bottomNavigationView.setSelectedItemId(R.id.lenta);

        setVisibleGone();

    }

    private void setVisibleGone(){
        avatar.setVisibility(View.GONE);
        date.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        postUser.setVisibility(View.GONE);
        textLike.setVisibility(View.GONE);
        textDiss.setVisibility(View.GONE);
        textInfoWork.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void setVisible(){
        progressBar.setVisibility(View.GONE);
        avatar.setVisibility(View.VISIBLE);
        date.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        postUser.setVisibility(View.VISIBLE);
        textLike.setVisibility(View.VISIBLE);
        textDiss.setVisibility(View.VISIBLE);
        textInfoWork.setVisibility(View.VISIBLE);
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
    private void setVideoView(final String videoPath){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int)(displayMetrics.heightPixels-(180*displayMetrics.scaledDensity)));
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        videoView.setLayoutParams(params);
        videoView.setVideoPath(videoPath);
        videoView.requestFocus();
        videoView.seekTo(1);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        progressBar.setVisibility(View.GONE);
                        setVisible();
                        textInfoWork.setText(String.valueOf(videos.get(count).get("info")));
                        date.setText(String.valueOf(videos.get(count).get("dateTime")));
                        onClick(videoView);
                    }
                });
    }

    private void nextVideo(String s){

        storageReference = storage.getReferenceFromUrl("gs://myfirstwork-15e9c.appspot.com/").child(s);
        try {
            File localFile = File.createTempFile("video","mp4");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    setVideoView(localFile.getAbsolutePath());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoView.seekTo(1);
        videoView.start();
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

    private void queryFireBase(){
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
                    nextVideo((String) videos.get(count).get("path"));
                }else {
                    Log.d("READ", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
