package com.example.citybuddy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class EditActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private static final String TAG = "NameCountry";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    TextView fullName;
    EditText country;
    EditText birthday;
    EditText mothertongue;

    //Views & other vars for imageupload
    ImageView btnChoose;
    Button btnUpload;
    Uri filePath;
    final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setProfile();
        mAuth = FirebaseAuth.getInstance();

        //findviews
        fullName = findViewById(R.id.profile_name);
        country = findViewById(R.id.country);
        birthday = findViewById(R.id.birthday);
        mothertongue = findViewById(R.id.mothertongue);
        btnChoose = findViewById(R.id.profile_pic);
        btnUpload = findViewById(R.id.upload);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(v);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(v);
            }
        });
    }

    private void chooseImage(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //imageView.setImageBitmap(bitmap);
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                btnChoose.setBackgroundDrawable(drawable);

                int h = bitmap.getHeight();
                int w = bitmap.getWidth();
                if(h > w){
                    btnChoose.getLayoutParams().height = 700;
                    btnChoose.getLayoutParams().width = 500;
                }
                if(h < w){
                    btnChoose.getLayoutParams().height = 500;
                    btnChoose.getLayoutParams().width = 700;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(View v){
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            FirebaseUser user = mAuth.getCurrentUser();
            String email = user.getEmail();

            StorageReference ref = storageRef.child("profile_images/" + email);
            ref.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        makeToast("Uploaded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        makeToast("Failed " + e.getMessage());
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
        }
    }

    public void setProfile(){
        TextView fullName = findViewById(R.id.profile_name);
        EditText country = findViewById(R.id.country);
        EditText birthday = findViewById(R.id.birthday);
        EditText mothertongue = findViewById(R.id.mothertongue);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle.isEmpty()){
            makeToast("No data loaded for this user.");
        }else{
            String profileName = intent.getStringExtra("full_name");
            String profileCountry = intent.getStringExtra("country");
            String profileBirthday = intent.getStringExtra("birthday");
            String profileMothertongue = intent.getStringExtra("mothertongue");
            Boolean personal = intent.getBooleanExtra("personal", false);
            if(personal){
                makeToast("Edit your profile here!");
            }else{
                Button editButton = findViewById(R.id.edit_button);
                editButton.setVisibility(View.GONE);
            }

            fullName.setText(String.valueOf(profileName));
            country.setText(String.valueOf(profileCountry));
            birthday.setText(String.valueOf(profileBirthday));
            mothertongue.setText(String.valueOf(profileMothertongue));
        }
    }

    public void showProfile(String fullName, String homeCountry, String birthday, String mothertongue, Boolean personal){
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("full_name", fullName);
        bundle.putString("country", homeCountry);
        bundle.putString("birthday", birthday);
        bundle.putString("mothertongue", mothertongue);
        bundle.putBoolean("personal", personal);
        profileIntent.putExtras(bundle);
        startActivity(profileIntent);
    }


    public void saveEdit(final View v){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = user.getEmail();

        DocumentReference currentUser = db.collection("users").document(userEmail);

        final String editName = fullName.getText().toString().toLowerCase();
        final String editCountry = country.getText().toString().toLowerCase();
        final String editBirthday = birthday.getText().toString();
        final String editMothertongue = mothertongue.getText().toString().toLowerCase();

        currentUser
            .update(
                    "country", editCountry,
                    "birthday", editBirthday,
                    "mothertongue", editMothertongue)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                    chooseImage(v);
                    showProfile(editName, editCountry, editBirthday, editMothertongue, true);
                    makeToast("Your profile was successfully updated!");
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error updating document", e);
                }
            });
    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

}
