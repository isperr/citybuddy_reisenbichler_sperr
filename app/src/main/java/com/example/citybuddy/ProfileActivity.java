package com.example.citybuddy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setProfile();
    }

    public void setProfile(){

        TextView fullname = findViewById(R.id.profile_name);
        TextView country = findViewById(R.id.country);
        TextView birthday = findViewById(R.id.birthday);
        TextView mothertongue = findViewById(R.id.mothertongue);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle.isEmpty()){
           makeToast("No data loaded for this user.");
        }else{
            String profileName = intent.getStringExtra("full_name");
            String profileCountry = intent.getStringExtra("country");
            String profileBirthday = intent.getStringExtra("birthday");
            String profileMothertongue = intent.getStringExtra("mothertongue");

            fullname.setText(String.valueOf(profileName));
            country.setText(String.valueOf(profileCountry));
            birthday.setText(String.valueOf(profileBirthday));
            mothertongue.setText(String.valueOf(profileMothertongue));
        }

    }

    public void importantFunc(View v){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, "Will be implemented later...", duration);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }
}
