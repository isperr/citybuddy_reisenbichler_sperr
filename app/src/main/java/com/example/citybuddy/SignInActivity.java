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

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
    }

    public void logIn(View v){

        EditText emailEdit = (EditText) findViewById(R.id.emailEdit);
        String email = emailEdit.getText().toString();

        TextView passwordEdit = findViewById(R.id.passwordEdit);
        String password = passwordEdit.getText().toString();


        Intent loggedInIntent = new Intent(this, HomepageActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("email", email);

        loggedInIntent.putExtras(bundle);

        if("".equals(email) || "".equals(password)){
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, "Enter email and password to log in", duration);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
            toast.show();
        }else{
            startActivity(loggedInIntent);
        }
    }

    public void importantFunc(View v){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, "Will be implemented later...", duration);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    //ALREADY SIGNED UP -- LOGIN ACTIVITY
    public void alreadySignedIn(View v){

        TextView emailEdit = findViewById(R.id.emailEdit);
        String email = emailEdit.getText().toString();

        TextView passwordEdit = findViewById(R.id.passwordEdit);
        String password = passwordEdit.getText().toString();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);

                            View v = findViewById(R.id.button_login);
                            startLoggedInActivity(v);
                            makeToast("You are now signed in again");

                        }

                        // ...
                    }
                });
    }

    public void startLoggedInActivity(View v){
        Intent loggedIn = new Intent(this, HomepageActivity.class);
        startActivity(loggedIn);
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

}
