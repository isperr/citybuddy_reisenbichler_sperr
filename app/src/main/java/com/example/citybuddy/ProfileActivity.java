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

import org.w3c.dom.Text;

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
}
