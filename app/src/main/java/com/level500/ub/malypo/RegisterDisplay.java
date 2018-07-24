package com.level500.ub.malypo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

/**
 * Created by jamestakor on 7/17/18.
 */

public class RegisterDisplay extends AppCompatActivity {
    // strings to get all the extras
    String country, ph, pass, fn, ln, em, imgPath, signature;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_display);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        Spinner staticSpinner = (Spinner) findViewById(R.id.register_country);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.country_list,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
           signature = bundle.getString("signature");
           imgPath = bundle.getString("imagePath");
           country = bundle.getString("coun");
           ph = bundle.getString("ph");
           em = bundle.getString("em");
           ln = bundle.getString("ln");
           fn = bundle.getString("fn");
           pass = bundle.getString("pass");

            final EditText edtpass = (EditText) findViewById(R.id.register_password);
            final EditText edtemail = (EditText) findViewById(R.id.register_email);
            final EditText edtphone = (EditText) findViewById(R.id.register_phone);
            final EditText edtfName = (EditText) findViewById(R.id.register_firstName);
            final EditText edtlName = (EditText) findViewById(R.id.register_lastName);
            final EditText edtrSign = (EditText) findViewById(R.id.register_signature);

            edtpass.setText(pass);
            edtemail.setText(em);
            edtphone.setText(ph);
            edtfName.setText(fn);
            edtlName.setText(ln);
            edtrSign.setText(signature);
        }


    }

    public void loginScreen(View view){
        Intent intent = new Intent(RegisterDisplay.this, LoginActivity.class);
        startActivity(intent);
    }

    public void signatureScreen(View view){
        final EditText password = (EditText) findViewById(R.id.register_password);
        final EditText email = (EditText) findViewById(R.id.register_email);
        final EditText phone = (EditText) findViewById(R.id.register_phone);
        final EditText fName = (EditText) findViewById(R.id.register_firstName);
        final EditText lName = (EditText) findViewById(R.id.register_lastName);
        final EditText rSign = (EditText) findViewById(R.id.register_signature);

        final Spinner spinner2 = (Spinner) findViewById(R.id.register_country);

        int countyId  = spinner2.getId();
        String strEmail     = email.getText().toString().trim();
        String strPassword  = password.getText().toString().trim();
        String strPhone     = phone.getText().toString().trim();
        String strFname     = fName.getText().toString().trim();
        String strLname     = lName.getText().toString().trim();

        Intent intent = new Intent(RegisterDisplay.this, SignatureActivity.class);
        intent.putExtra("coun", countyId);
        intent.putExtra("em", strEmail);
        intent.putExtra("ps", strPassword);
        intent.putExtra("ph", strPhone);
        intent.putExtra("fn", strFname);
        intent.putExtra("ln", strLname);

        startActivity(intent);
    }


    public void registerAccount(View view){

        final EditText password = (EditText) findViewById(R.id.register_password);
        final EditText email = (EditText) findViewById(R.id.register_email);
        final EditText phone = (EditText) findViewById(R.id.register_phone);
        final EditText fName = (EditText) findViewById(R.id.register_firstName);
        final EditText lName = (EditText) findViewById(R.id.register_lastName);
        final EditText rSign = (EditText) findViewById(R.id.register_signature);

        final Spinner spinner2 = (Spinner) findViewById(R.id.register_country);


        final String strCountry   = spinner2.getSelectedItem().toString().trim();
        final String strEmail     = email.getText().toString().trim();
        String strPassword  = password.getText().toString().trim();
        final String strPhone     = phone.getText().toString().trim();
        final String strFname     = fName.getText().toString().trim();
        final String strLname     = lName.getText().toString().trim();
        String strSignature = rSign.getText().toString().trim();


        if (TextUtils.isEmpty(strEmail)) {
            Toast.makeText(RegisterDisplay.this, "Please enter an email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strFname)) {
            Toast.makeText(RegisterDisplay.this, "Please enter your first name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strLname)) {
            Toast.makeText(RegisterDisplay.this, "Please enter your last name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strPassword)) {
            Toast.makeText(RegisterDisplay.this, "Please enter a password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strPhone)) {
            Toast.makeText(RegisterDisplay.this, "Please enter a phone number!", Toast.LENGTH_SHORT).show();
        }else {
            if(!digitsOnly(strPhone)){
                Toast.makeText(RegisterDisplay.this, "Please enter a valid phone number!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if(!isValidEmaillId(strEmail)){
            Toast.makeText(RegisterDisplay.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(strPassword.length() < 6){
            Toast.makeText(RegisterDisplay.this, "Password must be at least 6 characters! try again!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strSignature)) {
            Toast.makeText(RegisterDisplay.this, "Please enter a signature", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!strSignature.equals("Signature - ok")) {
            Toast.makeText(RegisterDisplay.this, "Signature is invalid please sign your signature", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(RegisterDisplay.this);
        progressDialog.setTitle("Creating account...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            String uid = user.getUid();

                            mDatabase.child("users").child(uid).child("country").setValue(strCountry);
                            mDatabase.child("users").child(uid).child("firstname").setValue(strFname);
                            mDatabase.child("users").child(uid).child("lastname").setValue(strLname);
                            mDatabase.child("users").child(uid).child("phone").setValue(strPhone);
                            mDatabase.child("users").child(uid).child("email").setValue(strEmail);
                            mDatabase.child("users").child(uid).child("phoneVerified").setValue("false");
                            mDatabase.child("users").child(uid).child("accountActivated").setValue("false");
                            mDatabase.child("users").child(uid).child("photoID").setValue("false");
                            mDatabase.child("users").child(uid).child("profilePic").setValue("false");

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegisterDisplay.this, "An email has been sent to "+ strEmail + ". Please ensure to verify this.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            Intent intent = new Intent(RegisterDisplay.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterDisplay.this, "Oops error occured couldn't create account."+ task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private boolean digitsOnly(String phone){

        return TextUtils.isDigitsOnly(phone);
    }

    private boolean isValidEmaillId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }



}
