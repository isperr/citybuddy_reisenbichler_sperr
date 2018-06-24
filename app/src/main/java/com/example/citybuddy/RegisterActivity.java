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
        TextView emailEdit = findViewById(R.id.email_Edit);
        TextView passwordEdit = findViewById(R.id.password_Edit);
        TextView passwordEdit2 = findViewById(R.id.password_conf_Edit);
        TextView first_n_Edit = findViewById(R.id.first_n_Edit);
        TextView last_n_Edit = findViewById(R.id.last_n_Edit);
        TextView countryEdit = findViewById(R.id.city_autocomplete);
        StringBuilder birthday = new StringBuilder();

        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String password2 = passwordEdit2.getText().toString();
        String first_n = first_n_Edit.getText().toString();
        String last_n = last_n_Edit.getText().toString();
        String country = countryEdit.getText().toString();

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

        if("".equals(email) || !passwordMatch){
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "Enter at least email, password and password conformation", duration);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
            toast.show();
        }else{
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("first_name", first_n);
            user.put("last_name", last_n);
            user.put("email", email);
            user.put("password", password);
            user.put("country", country);
            user.put("birthday", birthday.toString());

            // Add a new document with a generated ID
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("DocSnippets", "DocumentSnapshot added with ID: " + documentReference.getId());
                            makeToast("Congrats! You are now registered.");
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("DocSnippets", "Error adding document", e);
                        }
                    });

            authenticateMePlease(v);
            startActivity(loggedInIntent);
        }
    }

    public void authenticateMePlease(View v){
        TextView emailEdit = findViewById(R.id.email_Edit);
        TextView passwordEdit = findViewById(R.id.password_Edit);

        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        Log.d(TAG, "createAccount:" + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        //View v = findViewById(R.id.mybutton2);
                        //startLoggedInActivity(v);
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
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            makeToast("logged in/registered");
        } else {
            makeToast("not logged in/registered");
        }
    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public void startLoggedInActivity(View v){
        Intent loggedIn = new Intent(this, HomepageActivity.class);
        startActivity(loggedIn);
    }

    public void signOut(View v) {
        mAuth.signOut();
        updateUI(null);
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

        dpDate = (DatePicker)findViewById(R.id.calender);
        // init
        dpDate.init(2000, 0, 1, null);

        mAuth = FirebaseAuth.getInstance();
    }

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

}
