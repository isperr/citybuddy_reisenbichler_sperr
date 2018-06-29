package com.example.citybuddy;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomepageActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final String TAG = "NameCountry";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        createUserLayout();

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

    public void signOut(View v) {
        mAuth.signOut();
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        makeToast("You are logged out now!");

    }

    public void startSearch(View v){
        Intent searchIntent = new Intent(this, SearchActivity.class);
        startActivity(searchIntent);
    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public void createUserLayout(){

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.get("first_name") + document.get("country"));

                                final String fullName = document.get("first_name").toString() + " " +document.get("last_name").toString();
                                final String homeCountry = document.get("country").toString();
                                final String birthday = document.get("birthday").toString();

                                //find Layout
                                LinearLayout all_buddies = (LinearLayout) findViewById(R.id.buddy_layout);
                                createUsersView(all_buddies, fullName, homeCountry, birthday);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void createUsersView(LinearLayout baseLayout, final String fullName, final String homeCountry, final String birthday){
        //create View for Each User
        LinearLayout linearLayout = new LinearLayout(getBaseContext());
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);;

        //Add name TextViews for each user
        TextView userNameTextView = new TextView(getBaseContext());
        userNameTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        userNameTextView.setText(fullName);
        userNameTextView.setTextColor(getResources().getColor(R.color.blackColor));
        userNameTextView.setTextSize(16);
        linearLayout.addView(userNameTextView);

        //Add name TextViews for each user
        TextView userCountryTextView = new TextView(getBaseContext());
        userCountryTextView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        userCountryTextView.setText("Country: " + homeCountry);
        userCountryTextView.setTextColor(getResources().getColor(R.color.blackColor));
        userCountryTextView.setTextSize(16);
        linearLayout.addView(userCountryTextView);


        TextView showProfileTextView = new TextView(getBaseContext());
        showProfileTextView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        showProfileTextView.setText("Show profile");
        showProfileTextView.setTextColor(getResources().getColor(R.color.lightBlueColor));
        showProfileTextView.setTextSize(16);
        showProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfile(fullName, homeCountry, birthday, false);
            }
        });
        linearLayout.addView(showProfileTextView);


        //Add separating horizontal line
        View line = new View(getBaseContext());
        LayoutParams lineParams = new LayoutParams(LayoutParams.MATCH_PARENT, 5);
        lineParams.setMargins(0,60, 0, 60);
        line.setLayoutParams(lineParams);
        line.setBackgroundColor(getResources().getColor(R.color.darkBlueColor));
        linearLayout.addView(line);

        baseLayout.addView(linearLayout);
    }

    public void profile(View v){
        String email = "nochmal@email.com";

        //FirebaseUser user = mAuth.getCurrentUser();
        //String email = user.getEmail();

        makeToast(email);

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

                        showProfile(fullName, homeCountry, birthday, true);

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
}
