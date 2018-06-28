package com.example.citybuddy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        TextView fullName = findViewById(R.id.profile_name);
        TextView country = findViewById(R.id.country);
        TextView birthday = findViewById(R.id.birthday);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle.isEmpty()){
           makeToast("No data loaded for this user.");
        }else{
            String profileName = intent.getStringExtra("full_name");
            String profileCountry = intent.getStringExtra("country");
            String profileBirthday = intent.getStringExtra("birthday");
            Boolean personal = intent.getBooleanExtra("personal", false);
            if(personal){
                makeToast("This is your profile! Feel free to edit any of your data here!");
            }else{
                Button editButton = findViewById(R.id.edit_button);
                editButton.setVisibility(View.GONE);
            }

            fullName.setText(String.valueOf(profileName));
            country.setText(String.valueOf(profileCountry));
            birthday.setText(String.valueOf(profileBirthday));
        }

    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public void edit(View v){
        Intent editIntent = new Intent(this, EditActivity.class);

        TextView fullName = findViewById(R.id.profile_name);
        TextView country = findViewById(R.id.country);
        TextView birthday = findViewById(R.id.birthday);

        Bundle bundle = new Bundle();
        bundle.putString("full_name", "Isabella Sperr");
        bundle.putString("country", "Austria");
        bundle.putString("birthday", "1. 1. 2000");
        editIntent.putExtras(bundle);
        startActivity(editIntent);

        startActivity(editIntent);
    }
}
