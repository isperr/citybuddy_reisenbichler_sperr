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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    DatePicker dpDate;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "EmailPassword";

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    ImageView imageView;
    ImageView btnChoose;
    Button btnUpload;
    Uri filePath;
    final int PICK_IMAGE_REQUEST = 71;

    public void submitLogin(View v){
        //Views & Stringbuilder
        TextView emailEdit = findViewById(R.id.email_Edit);
        TextView passwordEdit = findViewById(R.id.password_Edit);
        TextView passwordEdit2 = findViewById(R.id.password_conf_Edit);
        TextView first_n_Edit = findViewById(R.id.first_n_Edit);
        TextView last_n_Edit = findViewById(R.id.last_n_Edit);
        TextView countryEdit = findViewById(R.id.city_autocomplete);
        TextView languageEdit = findViewById(R.id.language_autocomplete);

        StringBuilder birthday = new StringBuilder();

        //Strings
        String email, password, password2, first_n, last_n, country, language ="";
        email = emailEdit.getText().toString();
        password = passwordEdit.getText().toString();
        password2 = passwordEdit2.getText().toString();
        first_n = first_n_Edit.getText().toString().toLowerCase();
        last_n = last_n_Edit.getText().toString().toLowerCase();
        country = countryEdit.getText().toString().toLowerCase();
        language = languageEdit.getText().toString().toLowerCase();


        birthday.append(dpDate.getDayOfMonth()+". ");
        birthday.append((dpDate.getMonth() + 1)+". ");//month is 0 based
        birthday.append(dpDate.getYear());

        Intent loggedInIntent = new Intent(this, HomepageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        loggedInIntent.putExtras(bundle);

        boolean passwordMatch = false;
        if(password.equals(password2) && (!"".equals(password) && !"".equals(password2))){
            passwordMatch = true;
        }

        if("".equals(email)){
            makeToast("Enter an email to complete the registration");
        }else if(!passwordMatch){
            makeToast("Your passwords need to match to complete registration.");
        }else{//email & password are correct

            //ADD USER TO FIREBASE - FIRESTORE
            // Create a new user with a first and last name
            Map<String, Object> user = new HashMap<>();
            user.put("first_name", first_n);
            user.put("last_name", last_n);
            user.put("email", email);
            user.put("password", password);
            user.put("country", country);
            user.put("mothertongue", language);
            user.put("birthday", birthday.toString());

            db.collection("users")
                    .document(email)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("DocSnippets", "DocumentSnapshot added");
                            makeToast("Congrats! You are now registered.");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("DocSnippets", "Error adding document", e);
                }
            });


            //ADD USER TO FIREBASE - AUTHENTICATION
            Log.d(TAG, "createAccount:" + email);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                makeToast("Authentication failed.");
                            } else {
                                finish();
                            }
                        }
                    });

            //uploadImage(v);

            //start new intent, change to Homepage
            startActivity(loggedInIntent);
        }
    }

    public void makeToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
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
                imageView.setBackgroundDrawable(drawable);

                int h = bitmap.getHeight();
                int w = bitmap.getWidth();
                if(h > w){
                    imageView.getLayoutParams().height = 700;
                    imageView.getLayoutParams().width = 500;
                }
                if(h < w){
                    imageView.getLayoutParams().height = 500;
                    imageView.getLayoutParams().width = 700;
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

            EditText emailEdit = findViewById(R.id.email_Edit);
            String email = emailEdit.getText().toString();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        makeToast("Enter at least email and password to register.");

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        AutoCompleteTextView countryTextView = (AutoCompleteTextView)
                findViewById(R.id.city_autocomplete);
        countryTextView.setAdapter(countryAdapter);

        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, LANGUAGES);
        AutoCompleteTextView languageTextView = (AutoCompleteTextView)
                findViewById(R.id.language_autocomplete);
        languageTextView.setAdapter(languageAdapter);

        // init
        dpDate = (DatePicker)findViewById(R.id.calender);
        dpDate.init(2000, 0, 1, null);

        mAuth = FirebaseAuth.getInstance();

        //imageView = findViewById(R.id.image_upload);
        //btnChoose = findViewById(R.id.image_upload);
        //btnUpload = findViewById(R.id.upload_me);

        /*btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(v);
            }
        });*/

        /*btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(v);
            }
        });*/
    }

    //Small country autocomplete
    private static final String[] COUNTRIES = new String[] {
            "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan, Province of China", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe"
    };

    //Small language autocomplete
    private static final String[] LANGUAGES = new String[]{
            "Afrikaans", "Arabic", "Catalan", "Czech", "Welsh", "Danish", "German", "Greek", "Spanish", "Persian", "Finnish", "French", "Irish", "Scots/Gaelic", "Hindi", "Croatian", "Hungarian", "Armenian", "Indonesian", "Italian", "Hebrew", "Japanese", "Yiddish", "Georgian", "Cambodian", "Korean", "Latin", "Lithuanian", "Maori", "Macedonian", "Mongolian", "Moldavian", "Maltese", "Burmese", "Nepali", "Dutch", "Norwegian", "Punjabi", "Polish", "Portuguese", "Quechua", "Romanian", "Russian", "Serbo-Croatian", "Slovak", "Slovenian", "Somali", "Albanian", "Serbian", "Siswati", "Swedish", "Swahili", "Tamil", "Tegulu", "Thai", "Turkmen", "Turkish", "Tatar", "Ukrainian", "Uzbek", "Vietnamese", "Wolof", "Xhosa", "Yoruba", "Chinese", "Zulu", "Mandarin"
    };

    @Override
    public void onStart() {
        super.onStart();
    }

}
