package com.example.citybuddy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomepageActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        signedInAs();
        mAuth = FirebaseAuth.getInstance();
    }

    public void signedInAs(){
        TextView singedInUser = findViewById(R.id.signed_in_as);
        String string = singedInUser.getText().toString();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle.isEmpty()){
            singedInUser.setText(String.valueOf(string));
        }else{
            String email = intent.getStringExtra("email");
            String resultString = string + " as: " + email;

            singedInUser.setText(String.valueOf(resultString));
        }
    }

    public void showProfile(View v){
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    public void signOut(View v) {
        mAuth.signOut();
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        makeToast("You are logged out now!");

    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }
}
