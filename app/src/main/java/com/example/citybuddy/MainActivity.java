package com.example.citybuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void register(View v){
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    public void signIn(View v){
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
    }

    public void test(View v){
        String yourString = "geheim";

        try{
            /*byte[] bytesOfMessage = yourString.getBytes("UTF-8");

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);

            String value = new String(thedigest, "UTF-8");*/

            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(yourString.getBytes(), 0, yourString.length());
            BigInteger i = new BigInteger(1,m.digest());


            Toast toast = Toast.makeText(getApplicationContext(), "GEHT: " + String.format("%1$032x", i), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
            toast.show();
        }catch(Exception e){
            Toast toast = Toast.makeText(getApplicationContext(), "Fehler: " + e.getMessage(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
            toast.show();
        }

    }
}
