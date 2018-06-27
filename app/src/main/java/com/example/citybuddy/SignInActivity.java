package com.example.citybuddy;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
    }

    public void logIn(View v){
        EditText emailEdit = (EditText) findViewById(R.id.emailEdit);
        final String email = emailEdit.getText().toString();

        TextView passwordEdit = findViewById(R.id.passwordEdit);
        String password = passwordEdit.getText().toString();


        final Intent loggedInIntent = new Intent(this, HomepageActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("email", email);

        loggedInIntent.putExtras(bundle);

        if("".equals(email) || "".equals(password)){
            makeToast("Enter email and password to log in");
        }else{
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);

                                homepageActivity(loggedInIntent);
                                checkDoc(email);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                makeToast("Authentication failed.");
                                updateUI(null);
                            }

                            // ...
                        }
                    });


        }
    }

    public void homepageActivity(Intent loggedInIntent){
        startActivity(loggedInIntent);
    }

    public void forgotPassword(View v){
        EditText emailEdit = (EditText) findViewById(R.id.emailEdit);
        String email = emailEdit.getText().toString();

        if("".equals(email)){
            makeToast("You need to enter your email address above to reset your password");
        }else{
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                makeToast("An email to reset your password was sent to your email address.");
                            }
                        }
                    });
        }


    }

    //TEST IF USER WITH THAT EMAIL EXISTS
    public void checkDoc(String email){

        final DocumentReference docRef = db.collection("users").document(email);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    private void updateUI(FirebaseUser user) {
        if (user != null) {
            makeToast("You are logged in now!");
        } else {
            makeToast("Enter your information to login.");
        }
    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public void register(View v){
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}
