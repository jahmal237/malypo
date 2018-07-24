package com.level500.ub.malypo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created by jamestakor on 7/17/18.
 */

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getting instance of firebase
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, Profile.class));
            finish();
        }else {
            setContentView(R.layout.login);
        }

    }

    public void gotoRegister(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterDisplay.class);
        startActivity(intent);
    }

    public void loginScreen(View view){

        final EditText password = (EditText) findViewById(R.id.password_real);
        final EditText email = (EditText) findViewById(R.id.email_real);

        String strEmail = email.getText().toString().trim();
        String strPassword = password.getText().toString().trim();

        if ((TextUtils.isEmpty(strEmail)) && (TextUtils.isEmpty(strPassword))){
            Toast.makeText(LoginActivity.this, "Invalid credentials please fill in!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strEmail)) {
            Toast.makeText(LoginActivity.this, "Please enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strPassword)) {
            Toast.makeText(LoginActivity.this, "Please enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isValidEmaillId(email.getText().toString().trim())){
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle("Login into account...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                progressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, Profile.class);
                                startActivity(intent);
                            } else {

                                // If sign in fails, display a message to the user.
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Authentication failed." + Objects.requireNonNull(task.getException()).getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });

        }else{
            Toast.makeText(getApplicationContext(), "InValid Email Address.", Toast.LENGTH_SHORT).show();
        }


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
