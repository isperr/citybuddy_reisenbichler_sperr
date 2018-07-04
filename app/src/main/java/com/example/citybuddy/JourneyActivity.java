package com.example.citybuddy;

import android.app.Activity;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class JourneyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Journeys";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);
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

        showJourneys();
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
    //PART FOR JOURNEYS
    //*****************

    public void newJourney(View v){
        Intent createJourneyIntent = new Intent(this, CreateJourneyActivity.class);
        startActivity(createJourneyIntent);
    }

    public void showJourneys(){

        TextView heading = (TextView) findViewById(R.id.all_journeys);
        String journeyHeading = "";

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle.isEmpty()){
            makeToast("No journey could be loaded.");
        }
        else{
            Boolean personal = intent.getBooleanExtra("personal", false);
            String email = intent.getStringExtra("user_email");

            if(!personal){
                String name = firstLetterUpper(intent.getStringExtra("full_name"));
                journeyHeading = " created by " + name;
                Button journeyButton = findViewById(R.id.journey_button);
                journeyButton.setVisibility(View.GONE);
            }
            else{
                journeyHeading = " created by you";
            }

            heading.append(journeyHeading);

            getJourneys(email);
        }
    }

    public void getJourneys(String email){
        db.collection("journeys")
                .whereEqualTo("user_email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int counter = 0;
                            LinearLayout all_journeys = (LinearLayout) findViewById(R.id.journey_layout);


                            for (QueryDocumentSnapshot document : task.getResult()) {

                                counter = counter + 1;

                                Log.d(TAG, document.getId() + " => " + document.get("user_email") + document.get("destination"));

                                final String destination = document.get("destination").toString();
                                final String arrival = document.get("arrival").toString();
                                final String departure = document.get("departure").toString();

                                //find Layout
                                createResultViews(all_journeys, destination, arrival, departure);
                            }

                            if(counter == 0){
                                //find Layout
                                LinearLayout all_buddies = (LinearLayout) findViewById(R.id.search_results);
                                createNoJourneyView(all_journeys);

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());

                        }
                    }
                });
    }

    public void createResultViews(LinearLayout baseLayout, final String destination, final String arrival, final String departure){
        //create View for Each User
        LinearLayout linearLayout = new LinearLayout(getBaseContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //Add name TextViews for each user
        TextView journeyDestTextView = new TextView(getBaseContext());
        journeyDestTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        journeyDestTextView.setText("Destination: " + firstLetterUpper(destination));
        journeyDestTextView.setTextColor(getResources().getColor(R.color.blackColor));
        journeyDestTextView.setTextSize(16);
        linearLayout.addView(journeyDestTextView);

        //Add name TextViews for each user
        TextView arrDateTextView = new TextView(getBaseContext());
        arrDateTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        arrDateTextView.setText("Arrival Date: " + arrival);
        arrDateTextView.setTextColor(getResources().getColor(R.color.blackColor));
        arrDateTextView.setTextSize(16);
        linearLayout.addView(arrDateTextView);


        TextView depDateTextView = new TextView(getBaseContext());
        depDateTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        depDateTextView.setText("Departure Date: " + departure);
        depDateTextView.setTextColor(getResources().getColor(R.color.blackColor));
        depDateTextView.setTextSize(16);
        linearLayout.addView(depDateTextView);


        //Add separating horizontal line
        View line = new View(getBaseContext());
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5);
        lineParams.setMargins(0,60, 0, 60);
        line.setLayoutParams(lineParams);
        line.setBackgroundColor(getResources().getColor(R.color.darkBlueColor));
        linearLayout.addView(line);

        baseLayout.addView(linearLayout);
    }

    public void createNoJourneyView(LinearLayout baseLayout){

        //Add no results found information
        TextView noResultsTextView = new TextView(getBaseContext());
        noResultsTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        noResultsTextView.setText(R.string.no_journeys);
        noResultsTextView.setTextColor(getResources().getColor(R.color.blackColor));
        noResultsTextView.setTextSize(16);
        baseLayout.addView(noResultsTextView);
    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
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
