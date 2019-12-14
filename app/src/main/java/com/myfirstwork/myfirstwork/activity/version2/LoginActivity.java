package com.myfirstwork.myfirstwork.activity.version2;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.myfirstwork.myfirstwork.R;
import com.myfirstwork.myfirstwork.activity.version2.fragment.Dialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout signIn, signUp;
    DialogFragment dialogFragment;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_login);
        fragmentManager=getSupportFragmentManager();
        signIn = findViewById(R.id.sign_in);
        signUp = findViewById(R.id.sign_up);
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Dialog dialog;
        switch (v.getId()) {
            case R.id.sign_in:
                dialog= new Dialog(getApplicationContext());
                dialog.setParametr("sign_in");
                dialogFragment=dialog;
                dialogFragment.show(fragmentManager,"sign in");
                break;
            case R.id.sign_up:
                dialog= new Dialog(getApplicationContext());
                dialog.setParametr("sign_up");
                dialogFragment=dialog;
                dialogFragment.show(fragmentManager,"sign up");
                break;
        }
    }
}
