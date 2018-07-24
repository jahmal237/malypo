package com.level500.ub.malypo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {
    boolean isAccountActive = false;
    FirebaseAuth mAuth;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_transactions:
                    fragment = new TransactionFragment();
                    break;

                case R.id.navigation_accounts:
                    fragment = new AccountFragment();
                    break;

                case R.id.navigation_payments:
                    fragment = new PaymentFragment();
                    break;

                case R.id.navigation_settings:
                    fragment = new SettingFragment();
                    break;
            }

            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getting instance of firebase
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(Profile.this, LoginActivity.class));
            finish();
        }

        setContentView(R.layout.activity_profile);

        final ProgressDialog progressDialog = new ProgressDialog(Profile.this);
        final BottomNavigationView navigation = findViewById(R.id.navigation);

        progressDialog.setTitle("Verifying account details...");
        progressDialog.setCancelable(false);
        progressDialog.show();



        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        assert mAuth != null;
        String uid = mAuth.getUid();

        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    String phoneVerified = user.phoneVerified;
                    String accountVerified = user.accountActivated;
                    if(accountVerified.equals("false") || phoneVerified.equals("false")){
                        loadFragment(new PhoneVerifyActivationFragment());
                        progressDialog.dismiss();
                        View trans = navigation.findViewById(R.id.navigation_transactions);
                        View payment = navigation.findViewById(R.id.navigation_payments);
                        View acc = navigation.findViewById(R.id.navigation_accounts);
                        View setting = navigation.findViewById(R.id.navigation_settings);
                       trans.setEnabled(false);     setting.setEnabled(false);
                       payment.setEnabled(false);     acc.setEnabled(false);

                    }
                    else {
                        isAccountActive = true;
                        //getting bottom navigation view and attaching the listener
                        progressDialog.dismiss();
                        View view = navigation.findViewById(R.id.navigation_accounts);
                        view.performClick();
                    }
                }else {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Profile.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                int code = databaseError.getCode();
                if(code == -3){

                }else {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Profile.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });


        //getting bottom navigation view and attaching the listener
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.menu_new_settings:
                if(isAccountActive){
                    SettingFragment nextFrag = new SettingFragment();
                    Profile.this.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, nextFrag)
                            .addToBackStack(null)
                            .commit();
                }else {
                    Toast.makeText(Profile.this, "Please activate your account", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Profile.this, LoginDisplay.class);
                startActivity(intent);
                finish();
                break;

            case R.id.menu_help:
                Intent intent2 = new Intent(Profile.this, About_Help.class);
                startActivity(intent2);
                break;

            case R.id.menu_more:
                Intent intent3 = new Intent(Profile.this, MoreActivity.class);
                startActivity(intent3);
                break;

                }
        return super.onOptionsItemSelected(item);

    }


    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


}


