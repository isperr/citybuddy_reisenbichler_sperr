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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setUserParams();
    }

    public void edit(View v){
        EditText fullName = findViewById(R.id.profile_name);
        fullName.setText(String.valueOf("Oh yeeees!"));
    }

    public void setUserParams(){
        //FirebaseUser user = mAuth.getCurrentUser();
        //String email = user.getEmail();
        String email = "nochmal@email.com";

        final DocumentReference docRef = db.collection("users").document(email);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        final String fullName = document.get("first_name").toString() + " " + document.get("last_name").toString();
                        final String homeCountry = document.get("country").toString();
                        final String birthday = document.get("birthday").toString();

                        Log.d(TAG, fullName + " " + homeCountry + " " + birthday);

                        setProfile(fullName, homeCountry, birthday);
                        //showProfile(fullName, homeCountry, birthday, true);

                    } else {
                        Log.d(TAG, "No such document");
                        makeToast("Geht ned");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void setProfile(String n, String c, String b){
        EditText fullName = findViewById(R.id.profile_name);
        EditText country = findViewById(R.id.country);
        EditText birthday = findViewById(R.id.birthday);

        fullName.setText(String.valueOf(n));
        country.setText(String.valueOf(c));
        birthday.setText(String.valueOf(b));
    }

    public void showProfile(String fullName, String homeCountry, String birthday, Boolean personal){
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("full_name", fullName);
        bundle.putString("country", homeCountry);
        bundle.putString("birthday", birthday);
        bundle.putBoolean("personal", personal);
        profileIntent.putExtras(bundle);
        startActivity(profileIntent);
    }

    public void saveEdit(View v){
        makeToast("cooolio");
    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

}
