package com.example.citybuddy;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class JourneyActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);
    }


    public void submitJourney(){

        //TextView destination = findViewById(R.id.destination);
        StringBuilder arrival = new StringBuilder();
        StringBuilder departure = new StringBuilder();

        //ADD USER TO FIREBASE - FIRESTORE
        // Create a new journey
        Map<String, Object> journey = new HashMap<>();
        //journey.put("user", user);
        //journey.put("destination", destination);
        journey.put("arrival", arrival.toString());
        journey.put("departure", departure.toString());

       /* db.collection("journeys")
                .document(user)
                .set(journey)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DocSnippets", "DocumentSnapshot added");
                        makeToast("Congrats! You just created a new journey.");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("DocSnippets", "Error adding document", e);
            }
        });*/

    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

}
