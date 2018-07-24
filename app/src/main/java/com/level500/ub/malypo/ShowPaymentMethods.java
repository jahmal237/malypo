package com.level500.ub.malypo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ShowPaymentMethods extends Fragment {

    DatabaseReference mDatabase;
    FirebaseUser mAuth;
    String phoneNum = null;
    boolean isMTN = false;
    boolean isORANGE = false;
    boolean isBANK = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        final View rootView = inflater.inflate(R.layout.show_payment, container, false);

        final TextView momo  = (TextView)rootView.findViewById(R.id.mobile_money_tab);
        final TextView omo  = (TextView)rootView.findViewById(R.id.orange_money_tab);
        final TextView bank  = (TextView)rootView.findViewById(R.id.bank_tab);
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        assert mAuth != null;
        final String uid = mAuth.getUid();



        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    phoneNum = user.phone;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //handle databaseError
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginDisplay.class);
                startActivity(intent);
            }
        });

        mDatabase.child("Payment").child(uid).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                   if(dataSnapshot.child("mtn").exists()){
                        isMTN = true;
                        momo.setText("MTN MOBILE MONEY ACCOUNT ACTIVATED");
                        momo.setBackgroundColor(Color.GREEN);
                        momo.setTextColor(Color.WHITE);
                   }
                    if(dataSnapshot.child("orange").exists()){
                        isORANGE = true;
                        omo.setText("ORANGE MONEY ACCOUNT ACTIVATED");
                        omo.setBackgroundColor(Color.GREEN);
                        omo.setTextColor(Color.WHITE);
                    }
                    if(dataSnapshot.child("bank").exists()){
                        isBANK = true;
                        bank.setText("BANK ACCOUNT ACTIVATED");
                        bank.setBackgroundColor(Color.GREEN);
                        bank.setTextColor(Color.WHITE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                int code = databaseError.getCode();
                if(code == -3){

                }else {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });


        momo.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (isMTN) {
                    Toast.makeText(getActivity(), "Mtn mobile money account currently active", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setCancelable(false);
                    dialog.setTitle("Confirm Phone Number");
                    final EditText phoneNumber = new EditText(getActivity());
                    phoneNumber.setText(phoneNum);
                    phoneNumber.setPadding(5, 2, 2, 5);
                    dialog.setView(phoneNumber);
                    dialog.setMessage("Please verify your account number below.");
                    dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setCancelable(false);
                            progressDialog.setTitle("Adding payment account...");
                            progressDialog.show();
                            mDatabase.child("Payment").child(uid).child("mtn").setValue("1");
                            mDatabase.child("users").child(uid).child("mtnphone").setValue(phoneNumber.getText().toString().trim());

                            progressDialog.dismiss();
                        }
                    })
                            .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    final AlertDialog alert = dialog.create();
                    alert.show();
                }
            }
        });


        omo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                if (isORANGE) {
                    Toast.makeText(getActivity(), "Orange mobile money account currently active", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setCancelable(false);
                    dialog.setTitle("Confirm Phone Number");
                    final EditText phoneNumber = new EditText(getActivity());
                    phoneNumber.setText(phoneNum);
                    phoneNumber.setPadding(5, 2, 2, 5);
                    dialog.setView(phoneNumber);
                    dialog.setMessage("Please verify your account number below.");
                    dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setCancelable(false);
                            progressDialog.setTitle("Adding payment account...");
                            progressDialog.show();
                            mDatabase.child("Payment").child(uid).child("orange").setValue("2");
                            mDatabase.child("users").child(uid).child("orangephone").setValue(phoneNumber.getText().toString().trim());

                            progressDialog.dismiss();
                        }
                    })
                            .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    final AlertDialog alert = dialog.create();
                    alert.show();
                }
            }
        });


        bank.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                final BottomNavigationView navigation = Objects.requireNonNull(getActivity()).findViewById(R.id.navigation);
                View transac = navigation.findViewById(R.id.navigation_transactions);
                transac.performClick();
            }
        });



        return rootView;
    }
}



