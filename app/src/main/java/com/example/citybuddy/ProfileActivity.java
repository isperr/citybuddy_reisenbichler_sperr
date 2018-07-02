package com.example.citybuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setProfile();

        profilePic = findViewById(R.id.profile_pic);
    }

    public void setProfile(){
        TextView fullName = findViewById(R.id.profile_name);
        TextView country = findViewById(R.id.country);
        TextView birthday = findViewById(R.id.birthday);
        TextView mothertongue = findViewById(R.id.mothertongue);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle.isEmpty()){
           makeToast("No data loaded for this user.");
        }else{
            String profileName = firstLetterUpper(intent.getStringExtra("full_name"));
            String profileCountry = firstLetterUpper(intent.getStringExtra("country"));
            String profileBirthday = intent.getStringExtra("birthday");
            String profileMothertongue = firstLetterUpper(intent.getStringExtra("mothertongue"));
            Boolean personal = intent.getBooleanExtra("personal", false);
            if(!personal){
                Button editButton = findViewById(R.id.edit_button);
                editButton.setVisibility(View.GONE);
            }

            fullName.setText(String.valueOf(profileName));
            country.setText(String.valueOf(profileCountry));
            birthday.setText(String.valueOf(profileBirthday));
            mothertongue.setText(String.valueOf(profileMothertongue));


            downloadPic();
        }
    }

    private String firstLetterUpper(String input){
        String s = "";
        String current = "";
        String[] words = input.split("\\s+");
        for(int i = 0; i < words.length; i++){
            current = words[i].substring(0, 1).toUpperCase() + words[i].substring(1);
            s += current + " ";
        }
        return s;
    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }


    public void profileEdit(View v){
        TextView fullName = findViewById(R.id.profile_name);
        TextView country = findViewById(R.id.country);
        TextView birthday = findViewById(R.id.birthday);
        TextView mothertongue = findViewById(R.id.mothertongue);

        String editName = fullName.getText().toString();
        String editCountry = country.getText().toString();
        String editBirthday = birthday.getText().toString();
        String editMothertongue = mothertongue.getText().toString();

        showProfile(editName, editCountry, editBirthday, editMothertongue, true);
    }

    public void showProfile(String fullName, String homeCountry, String birthday, String mothertongue, Boolean personal){
        Intent editIntent = new Intent(this, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("full_name", fullName);
        bundle.putString("country", homeCountry);
        bundle.putString("birthday", birthday);
        bundle.putString("mothertongue", mothertongue);
        bundle.putBoolean("personal", personal);
        editIntent.putExtras(bundle);
        startActivity(editIntent);
    }

    public void downloadPic(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        StorageReference imgRef = storageRef.child("profile_images/" + email);

        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                setProfilePic(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Picture", "Failure => " + exception.getMessage());
            }
        });
    }

    public void setProfilePic(Bitmap bitmap){
        try {
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            profilePic.setBackgroundDrawable(drawable);

            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            if(h > w){
                profilePic.getLayoutParams().height = 700;
                profilePic.getLayoutParams().width = 500;
            }
            if(h < w){
                profilePic.getLayoutParams().height = 500;
                profilePic.getLayoutParams().width = 700;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
