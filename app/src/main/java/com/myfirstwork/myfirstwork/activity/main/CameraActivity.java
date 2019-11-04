package com.myfirstwork.myfirstwork.activity.main;

import android.app.Activity;
import android.os.Bundle;

import com.myfirstwork.myfirstwork.R;
import com.myfirstwork.myfirstwork.activity.fragment.Camera2VideoFragment;

public class CameraActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2VideoFragment.newInstance())
                    .commit();
        }
    }

}