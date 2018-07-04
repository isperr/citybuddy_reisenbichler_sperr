package com.example.citybuddy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchResults";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void showProfile(View v, String email, String fullName, String homeCountry, String birthday, Boolean personal){
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("user_email", email);
        bundle.putString("full_name", fullName);
        bundle.putString("country", homeCountry);
        bundle.putString("birthday", birthday);
        bundle.putBoolean("personal", personal);
        profileIntent.putExtras(bundle);
        startActivity(profileIntent);
    }

    public void searchFunction(View v){

        LinearLayout all_buddies = (LinearLayout) findViewById(R.id.search_results);
        all_buddies.removeAllViews();

        String param = "";

        RadioButton country = (RadioButton) findViewById(R.id.country_param);
        RadioButton language = (RadioButton) findViewById(R.id.lang_param);

        EditText searchInput = (EditText) findViewById(R.id.search_field);
        String input = searchInput.getText().toString();
        String searchValue = input.toLowerCase();

        if(input.length() == 0 || (!country.isChecked() && !language.isChecked())){
            makeToast("Enter a query to search or check a parameter!");
            //Add no results found information
            TextView noResultsTextView = new TextView(getBaseContext());
            noResultsTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            noResultsTextView.setText(R.string.nothing);
            noResultsTextView.setTextColor(getResources().getColor(R.color.blackColor));
            noResultsTextView.setTextSize(16);
            all_buddies.addView(noResultsTextView);
        }
        else{

            if(country.isChecked()){
                param = "country";
            }
            else if(language.isChecked()){
                param = "mothertongue";
            }

            db.collection("users")
                    .whereEqualTo(param, searchValue)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                int counter = 0;

                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    counter = counter + 1;

                                    Log.d(TAG, document.getId() + " => " + document.get("first_name") + document.get("country"));
                                    final String email = document.get("email").toString();
                                    final String fullName = document.get("first_name").toString() + " " +document.get("last_name").toString();
                                    final String homeCountry = document.get("country").toString();
                                    final String birthday = document.get("birthday").toString();

                                    //find Layout
                                    LinearLayout all_buddies = (LinearLayout) findViewById(R.id.search_results);
                                    createResultViews(all_buddies, email, fullName, homeCountry, birthday);
                                }

                                if(counter == 0){
                                    //find Layout
                                    LinearLayout all_buddies = (LinearLayout) findViewById(R.id.search_results);
                                    createNoResultView(all_buddies);

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());

                            }
                        }
                    });
        }


    }

    public void createResultViews(LinearLayout baseLayout, final String email, final String fullName, final String homeCountry, final String birthday){
        //create View for Each User
        LinearLayout linearLayout = new LinearLayout(getBaseContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        //Add name TextViews for each user
        TextView userNameTextView = new TextView(getBaseContext());
        userNameTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        userNameTextView.setText(firstLetterUpper(fullName));
        userNameTextView.setTextColor(getResources().getColor(R.color.blackColor));
        userNameTextView.setTextSize(16);
        linearLayout.addView(userNameTextView);

        //Add name TextViews for each user
        TextView userCountryTextView = new TextView(getBaseContext());
        userCountryTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        userCountryTextView.setText("Country: " + firstLetterUpper(homeCountry));
        userCountryTextView.setTextColor(getResources().getColor(R.color.blackColor));
        userCountryTextView.setTextSize(16);
        linearLayout.addView(userCountryTextView);


        TextView showProfileTextView = new TextView(getBaseContext());
        showProfileTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        showProfileTextView.setText("Show profile");
        showProfileTextView.setTextColor(getResources().getColor(R.color.lightBlueColor));
        showProfileTextView.setTextSize(16);
        showProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfile(view, email, fullName, homeCountry, birthday, false);
            }
        });
        linearLayout.addView(showProfileTextView);


        //Add separating horizontal line
        View line = new View(getBaseContext());
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 5);
        lineParams.setMargins(0,60, 0, 60);
        line.setLayoutParams(lineParams);
        line.setBackgroundColor(getResources().getColor(R.color.darkBlueColor));
        linearLayout.addView(line);

        baseLayout.addView(linearLayout);
    }

    public void createNoResultView(LinearLayout baseLayout){

        //Add no results found information
        TextView noResultsTextView = new TextView(getBaseContext());
        noResultsTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        noResultsTextView.setText(R.string.no_res);
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
