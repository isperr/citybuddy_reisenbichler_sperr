package com.example.citybuddy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final String TAG = "NameCountry";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView fullName;
    EditText country;
    EditText birthday;
    EditText mothertongue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setProfile();

        fullName = findViewById(R.id.profile_name);
        country = findViewById(R.id.country);
        birthday = findViewById(R.id.birthday);
        mothertongue = findViewById(R.id.mothertongue);
    }

    public void setProfile(){


        TextView fullName = findViewById(R.id.profile_name);
        EditText country = findViewById(R.id.country);
        EditText birthday = findViewById(R.id.birthday);
        EditText mothertongue = findViewById(R.id.mothertongue);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle.isEmpty()){
            makeToast("No data loaded for this user.");
        }else{
            String profileName = intent.getStringExtra("full_name");
            String profileCountry = intent.getStringExtra("country");
            String profileBirthday = intent.getStringExtra("birthday");
            String profileMothertongue = intent.getStringExtra("mothertongue");
            Boolean personal = intent.getBooleanExtra("personal", false);
            if(personal){
                makeToast("Edit your profile here!");
            }else{
                Button editButton = findViewById(R.id.edit_button);
                editButton.setVisibility(View.GONE);
            }

            fullName.setText(String.valueOf(profileName));
            country.setText(String.valueOf(profileCountry));
            birthday.setText(String.valueOf(profileBirthday));
            mothertongue.setText(String.valueOf(profileMothertongue));
        }

    }

    public void showProfile(String fullName, String homeCountry, String birthday, String mothertongue, Boolean personal){
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("full_name", fullName);
        bundle.putString("country", homeCountry);
        bundle.putString("birthday", birthday);
        bundle.putString("mothertongue", mothertongue);
        bundle.putBoolean("personal", personal);
        profileIntent.putExtras(bundle);
        startActivity(profileIntent);
    }

    public void saveEdit(View v){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();

        DocumentReference currentUser = db.collection("users").document(userEmail);

        final String editName = fullName.getText().toString();
        final String editCountry = country.getText().toString();
        final String editBirthday = birthday.getText().toString();
        final String editMothertongue = mothertongue.getText().toString();

        currentUser
                .update(
                        "country", editCountry,
                        "birthday", editBirthday,
                        "mothertongue", editMothertongue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        showProfile(editName, editCountry, editBirthday, editMothertongue, true);
                        makeToast("Your profile was successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

}
