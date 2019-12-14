package com.myfirstwork.myfirstwork.activity.version2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.myfirstwork.myfirstwork.R;
import com.myfirstwork.myfirstwork.data.source.User;

import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateUser extends AppCompatActivity implements View.OnClickListener {

    static final int GALLERY_REQUEST = 1;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    CircleImageView imageView;
    ImageButton imageButton;
    EditText name, lastName, year, mounth, day;
    RadioGroup radioGroup;
    RadioButton man, woman;
    Bundle bundle;
    Button button;
    String gender;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_create_user);
        bundle = getIntent().getExtras();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        imageView = findViewById(R.id.profile_image);
        imageButton = findViewById(R.id.edit_profile_image);
        button = findViewById(R.id.nextbtn);
        name = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        year = findViewById(R.id.year);
        mounth = findViewById(R.id.mouth);
        day = findViewById(R.id.day);
        man = findViewById(R.id.male_btn);
        woman = findViewById(R.id.female_btn);
        man.setOnClickListener(this::onClick);
        woman.setOnClickListener(this::onClick);
        imageButton.setOnClickListener(this::onClick);
        button.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.female_btn:
                gender="Женский";
            case R.id.male_btn:
                gender="Мужской";
            case R.id.edit_profile_image:
                    Intent intentGallery = new Intent(Intent.ACTION_PICK);
                    intentGallery.setType("image/*");
                    startActivityForResult(intentGallery,GALLERY_REQUEST);
                break;
            case R.id.nextbtn:///обработка ыыеденной инфы, отправка в базу данных firebase storage
                auth.getCurrentUser().getIdToken(true);
                auth.getCurrentUser().reload();
                if(firebaseUser.isEmailVerified()){
                    User user = new User();
                    user.setId(auth.getCurrentUser().getUid());
                    user.setName(String.valueOf(name.getText()));
                    user.setLastName(String.valueOf((lastName)));
                    user.setSex(gender);
                    user.setDataBirth(String.valueOf(day.getText())+'.'+String.valueOf(mounth.getText())+'.'+String.valueOf(year.getText()));
                    firestore.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(CreateUser.this, "create new user", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("create user",e.getMessage());
                            Toast.makeText(CreateUser.this, "error in create user", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                else
                    Toast.makeText(this, "На вашу почту выслано письмо для авторизации", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        switch (requestCode){
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri imagePath = data.getData();
                    try {
                        bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagePath);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(bitmap);
                }
        }
    }
}
