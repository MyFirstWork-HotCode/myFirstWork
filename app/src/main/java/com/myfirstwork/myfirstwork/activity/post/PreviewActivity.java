package com.myfirstwork.myfirstwork.activity.post;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.myfirstwork.myfirstwork.R;
import com.myfirstwork.myfirstwork.activity.post.adapter.AdapterGallery;
import com.myfirstwork.myfirstwork.activity.post.adapter.AdapterList;
import com.myfirstwork.myfirstwork.data.Query;
import com.myfirstwork.myfirstwork.data.source.Tag;
import com.myfirstwork.myfirstwork.databinding.ActivityPreviewVideoBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOG_TAG =" PreviewActivity" ;
    AdapterGallery adapterGallery;
    DisplayMetrics displayMetrics;
    RadioButton[] radioButtons = new RadioButton[3];
    SearchView searchView;
    ListView listView;
    ActivityPreviewVideoBinding activityPreviewVideoBinding;
    AdapterList adapterList;
    ArrayList<Tag> tags = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPreviewVideoBinding = DataBindingUtil.setContentView(this,R.layout.activity_preview_video);
        Bundle bundle = getIntent().getExtras();
        displayMetrics=getResources().getDisplayMetrics();
        Query query = new Query(this);
        tags= (ArrayList<Tag>) query.getTags();
        adapterList = new AdapterList(getString(tags));
        Log.i(LOG_TAG,query.getTags().toString());
        radioButtons[0]=findViewById(R.id.item0);
        radioButtons[1]=findViewById(R.id.item1);
        radioButtons[2]=findViewById(R.id.item2);
        searchView=findViewById(R.id.edit_tag);
        listView = findViewById(R.id.list);
        listView.setAdapter(adapterList);

        activityPreviewVideoBinding.item0.setOnClickListener(this);
        activityPreviewVideoBinding.item1.setOnClickListener(this);
        activityPreviewVideoBinding.item2.setOnClickListener(this);

        //searchView.setActivated(true);
        searchView.setQueryHint("Теги");
        listView.setVisibility(View.GONE);
        //searchView.onActionViewExpanded();
        //searchView.setIconified(false);
        //searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listView.setVisibility(View.VISIBLE);
                adapterList.getFilter().filter(newText);
                return false;
            }
        });

        adapterGallery = new AdapterGallery(this);
        activityPreviewVideoBinding.gallery.setAdapter(adapterGallery);
        activityPreviewVideoBinding.gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("Range")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(LOG_TAG, String.valueOf(position));
                activityPreviewVideoBinding.group.check(radioButtons[position].getId());
            }


            @SuppressLint("Range")
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        assert bundle != null;
        File file = new File(Objects.requireNonNull(bundle.getString("video")));
        setVideoView(file.getAbsolutePath());
        activityPreviewVideoBinding.post.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gallery:

                break;
            case R.id.item0:
                activityPreviewVideoBinding.gallery.setSelection(0,true);
                break;
            case R.id.item1:
                activityPreviewVideoBinding.gallery.setSelection(1,true);
                break;
            case  R.id.item2:
                activityPreviewVideoBinding.gallery.setSelection(2,true);
                break;

        }
    }

    private void setVideoView(String path){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int)(displayMetrics.heightPixels-(180*displayMetrics.scaledDensity)));
        layoutParams.gravity= Gravity.CENTER;
        activityPreviewVideoBinding.videoPreview.setLayoutParams(layoutParams);
        activityPreviewVideoBinding.videoPreview.setVideoPath(path);
        activityPreviewVideoBinding.videoPreview.seekTo(1);
        activityPreviewVideoBinding.videoPreview.start();
    }

    private List<String> getString(ArrayList<Tag> tags){
        ArrayList<String> strings = new ArrayList<>();
        for(Tag tag : tags){
            strings.add(tag.getName());
        }
        return strings;
    }
}
