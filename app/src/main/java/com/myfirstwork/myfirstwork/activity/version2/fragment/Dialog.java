package com.myfirstwork.myfirstwork.activity.version2.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.myfirstwork.myfirstwork.R;
import com.myfirstwork.myfirstwork.activity.main.MainActivity;
import com.myfirstwork.myfirstwork.activity.version2.CreateUser;

public class Dialog extends DialogFragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    EditText editLogin, editPass;
    String dialogParametr;
    Context context;
    Button sign;
    public Dialog(Context context){
        this.context=context;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mAuth=FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2_create_user,null);
        editLogin = view.findViewById(R.id.login);
        editPass = view.findViewById(R.id.email_pass);
        sign = view.findViewById(R.id.sign_btn);
        sign.setOnClickListener(this::onClick);
        switch (dialogParametr) {
            case "sign_in":

                break;
            case "sign_up":
                break;
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_btn:
                switch (dialogParametr){
                    case "sign_in":
                        signIn(String.valueOf(editLogin.getText()),String.valueOf(editPass.getText()));
                        break;
                    case "sign_up":
                        signUp(String.valueOf(editLogin.getText()),String.valueOf(editPass.getText()));
                }
                break;
        }
    }

    public void setParametr(String s){
        this.dialogParametr=s;
    }

    private void signIn(String login,String password){//вход
        mAuth.signInWithEmailAndPassword(login,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void signUp(String login,String password){ ////регистрируем нового пользователя
        mAuth.createUserWithEmailAndPassword(login,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    mAuth.getCurrentUser().sendEmailVerification();//отправка письма с ссылкой верификации
                }
                    Intent intent = new Intent(context, CreateUser.class);
                    startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("addAuth",e.getMessage());
            }
        });
    }


}
