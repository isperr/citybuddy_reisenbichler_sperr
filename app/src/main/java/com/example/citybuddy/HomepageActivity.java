package com.example.citybuddy;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
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

public class HomepageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;

    private static final String TAG = "NameCountry";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        createUserLayout();

        signedInAs();
        mAuth = FirebaseAuth.getInstance();

    }

    //*****************
    //PART FOR NAVIGATION
    //*****************

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //here is the main place where we need to work on.
        int id=item.getItemId();
        switch (id){

            case R.id.nav_homepage:
                Intent h= new Intent(this,HomepageActivity.class);
                startActivity(h);
                break;
            case R.id.nav_profile:
                Intent p= new Intent(this,ProfileActivity.class);
                startActivity(p);
                break;
            case R.id.nav_journeys:
                Intent j= new Intent(this,JourneyActivity.class);
                startActivity(j);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //*****************
    //PART FOR HOMEPAGE
    //*****************

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

    public void showProfile(String email, String fullName, String homeCountry, String birthday, String motherTongue, Boolean personal){
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user_email", email);
        bundle.putString("full_name", fullName);
        bundle.putString("country", homeCountry);
        bundle.putString("birthday", birthday);
        bundle.putString("mothertongue", motherTongue);
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
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userEmail = user.getEmail();
        
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.get("first_name") + document.get("country"));

                                String email = document.get("email").toString();


                                if(!email.equals(userEmail)){
                                    final String fullName = document.get("first_name").toString() + " " +document.get("last_name").toString();
                                    final String homeCountry = document.get("country").toString();
                                    final String birthday = document.get("birthday").toString();
                                    final String mothertongue = document.get("mothertongue").toString();

                                    //find Layout
                                    LinearLayout all_buddies = (LinearLayout) findViewById(R.id.buddy_layout);
                                    createUsersView(all_buddies, email, fullName, homeCountry, birthday, mothertongue);
                                }


                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void createUsersView(LinearLayout baseLayout, final String email, final String fullName, final String homeCountry, final String birthday, final String mothertongue){
        //create View for Each User
        LinearLayout linearLayout = new LinearLayout(getBaseContext());
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //Add name TextViews for each user
        TextView userNameTextView = new TextView(getBaseContext());
        userNameTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        userNameTextView.setText(firstLetterUpper(fullName));
        userNameTextView.setTextColor(getResources().getColor(R.color.blackColor));
        userNameTextView.setTextSize(16);
        linearLayout.addView(userNameTextView);

        //Add name TextViews for each user
        TextView userCountryTextView = new TextView(getBaseContext());
        userCountryTextView.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        userCountryTextView.setText("Country: " + firstLetterUpper(homeCountry));
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
                showProfile(email, fullName, homeCountry, birthday, mothertongue, false);
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userEmail = user.getEmail();

        final DocumentReference docRef = db.collection("users").document(userEmail);

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
                        final String mothertongue = document.get("mothertongue").toString();

                        Log.d(TAG, fullName + " " + homeCountry + " " + birthday);

                        showProfile(userEmail, fullName, homeCountry, birthday, mothertongue, true);

                    } else {
                        Log.d(TAG, "No such document");
                        makeToast("Sorry, there was a mistake!");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
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
}
