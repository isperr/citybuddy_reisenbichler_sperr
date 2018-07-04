package com.example.citybuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import de.cketti.mailto.EmailIntentBuilder;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity
       implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    ImageView profilePic;

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        setProfile();

        profilePic = findViewById(R.id.profile_pic);
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
    //PART FOR PROFILE
    //*****************

    public void setProfile(){
        TextView fullName = findViewById(R.id.profile_name);
        TextView country = findViewById(R.id.country);
        TextView birthday = findViewById(R.id.birthday);
        TextView motherTongue = findViewById(R.id.mothertongue);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle.isEmpty()){
           makeToast("No data loaded for this user.");
        }else{
            String profileName = firstLetterUpper(intent.getStringExtra("full_name"));
            String profileCountry = firstLetterUpper(intent.getStringExtra("country"));
            String profileBirthday = intent.getStringExtra("birthday");
            String profileMothertongue = firstLetterUpper(intent.getStringExtra("mothertongue"));
            Boolean personal = intent.getBooleanExtra("personal", false);
            Button editButton = findViewById(R.id.edit_button);
            Button contactButton = findViewById(R.id.contact_button);

            if(!personal){
                editButton.setVisibility(View.GONE);
            }
            else{
                contactButton.setVisibility(View.GONE);
            }

            fullName.setText(String.valueOf(profileName));
            country.setText(String.valueOf(profileCountry));
            birthday.setText(String.valueOf(profileBirthday));
            motherTongue.setText(String.valueOf(profileMothertongue));


            downloadPic();
        }
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

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public void profileEdit(View v){
        TextView fullName = findViewById(R.id.profile_name);
        TextView country = findViewById(R.id.country);
        TextView birthday = findViewById(R.id.birthday);
        TextView motherTongue = findViewById(R.id.mothertongue);

        String editName = fullName.getText().toString();
        String editCountry = country.getText().toString();
        String editBirthday = birthday.getText().toString();
        String editMothertongue = motherTongue.getText().toString();

        showProfile(editName, editCountry, editBirthday, editMothertongue, true);
    }

    public void showProfile(String fullName, String homeCountry, String birthday, String mothertongue, Boolean personal){
        Intent editIntent = new Intent(this, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("full_name", fullName);
        bundle.putString("country", homeCountry);
        bundle.putString("birthday", birthday);
        bundle.putString("mothertongue", mothertongue);
        bundle.putBoolean("personal", personal);
        editIntent.putExtras(bundle);
        startActivity(editIntent);
    }

    public void downloadPic(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();

        StorageReference imgRef = storageRef.child("profile_images/" + email);

        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                setProfilePic(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Picture", "Failure => " + exception.getMessage());
            }
        });
    }

    public void setProfilePic(Bitmap bitmap){
        try {
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            profilePic.setBackgroundDrawable(drawable);

            int h = bitmap.getHeight();
            int w = bitmap.getWidth();
            if(h > w){
                profilePic.getLayoutParams().height = 700;
                profilePic.getLayoutParams().width = 500;
            }
            if(h < w){
                profilePic.getLayoutParams().height = 500;
                profilePic.getLayoutParams().width = 700;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void showJourneys(View v){

        Intent intent = getIntent();
        Bundle profileBundle = intent.getExtras();

        if(profileBundle.isEmpty()){
            makeToast("No journeys available.");
        }else{

            Boolean personal = intent.getBooleanExtra("personal", false);
            String email = intent.getStringExtra("user_email");
            String profileName = firstLetterUpper(intent.getStringExtra("full_name"));

            Intent journeyIntent = new Intent(this, JourneyActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("user_email", email);
            bundle.putString("full_name", profileName);
            bundle.putBoolean("personal", personal);
            journeyIntent.putExtras(bundle);
            startActivity(journeyIntent);
        }
    }

    public void contactFunction(View v){

        Intent intent = getIntent();
        String mail = intent.getStringExtra("user_email");

        boolean success = EmailIntentBuilder.from(this)
                .to(mail)
                .subject("Request from CityBuddy")
                .start();

        if (!success) {
            makeToast("Error");
        }
    }
}
