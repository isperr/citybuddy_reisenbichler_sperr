package com.example.citybuddy;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    DatePicker dpDate;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "EmailPassword";

    public void submitLogin(View v){
        //Views & Stringbuilder
        TextView emailEdit = findViewById(R.id.email_Edit);
        TextView passwordEdit = findViewById(R.id.password_Edit);
        TextView passwordEdit2 = findViewById(R.id.password_conf_Edit);
        TextView first_n_Edit = findViewById(R.id.first_n_Edit);
        TextView last_n_Edit = findViewById(R.id.last_n_Edit);
        TextView countryEdit = findViewById(R.id.city_autocomplete);
        StringBuilder birthday = new StringBuilder();

        //Strings
        String email, password, password2, first_n, last_n, country ="";
        email = emailEdit.getText().toString();
        password = passwordEdit.getText().toString();
        password2 = passwordEdit2.getText().toString();
        first_n = first_n_Edit.getText().toString();
        last_n = last_n_Edit.getText().toString();
        country = countryEdit.getText().toString();

        birthday.append(dpDate.getDayOfMonth()+". ");
        birthday.append((dpDate.getMonth() + 1)+". ");//month is 0 based
        birthday.append(dpDate.getYear());

        Intent loggedInIntent = new Intent(this, HomepageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        loggedInIntent.putExtras(bundle);

        boolean passwordMatch = false;
        if(password.equals(password2) && (!"".equals(password) && !"".equals(password2))){
            passwordMatch = true;
        }

        if("".equals(email)){
            makeToast("Enter an email to complete the registration");
        }else if(!passwordMatch){
            makeToast("Your passwords need to match to complete registration.");
        }else{//email & password are correct

            //ADD USER TO FIREBASE - FIRESTORE
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("first_name", first_n);
            user.put("last_name", last_n);
            user.put("email", email);
            user.put("password", password);
            user.put("country", country);
            user.put("birthday", birthday.toString());

            db.collection("users")
                    .document(email)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("DocSnippets", "DocumentSnapshot added");
                            makeToast("Congrats! You are now registered.");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("DocSnippets", "Error adding document", e);
                }
            });


            //ADD USER TO FIREBASE - AUTHENTICATION
            Log.d(TAG, "createAccount:" + email);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                finish();
                            }
                        }
                    });


            //start new intent, change to Homepage
            startActivity(loggedInIntent);
        }
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            makeToast("You are now registered");
        } else {
            makeToast("Enter at least email and password to register.");
        }
    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.city_autocomplete);
        textView.setAdapter(adapter);

        // init
        dpDate = (DatePicker)findViewById(R.id.calender);
        dpDate.init(2000, 0, 1, null);

        mAuth = FirebaseAuth.getInstance();
    }

    //Small country autocomplete
    private static final String[] COUNTRIES = new String[] {
            "Austria", "Australia", "Belgium", "France", "Germany", "Italy", "Spain"
    };

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void signOut(View v) {
        mAuth.signOut();
        updateUI(null);
    }

}
