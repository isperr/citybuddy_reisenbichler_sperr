package com.example.citybuddy;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class JourneyActivity extends AppCompatActivity {

    private static final String TAG = "Journeys";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        showJourneys();

    }

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
