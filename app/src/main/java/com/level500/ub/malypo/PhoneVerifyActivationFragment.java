package com.level500.ub.malypo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PhoneVerifyActivationFragment extends Fragment {

    boolean isphoneVerified = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        assert mAuth != null;
        String uid = mAuth.getUid();

        View rootView = inflater.inflate(R.layout.phone_verification_activation, container, false);
        final Button activataAcc = (Button) rootView.findViewById(R.id.activate_account);
        final Button verifyPhone = (Button) rootView.findViewById(R.id.verify_phone);

        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    String accountVerified = user.phoneVerified;
                    String accountActivated = user.accountActivated;
                    if(accountVerified.equals("false")){
                        Toast.makeText(getActivity(), "Please verify your phone number", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        isphoneVerified = true;
                        verifyPhone.setBackgroundColor(Color.GREEN);
                        verifyPhone.setTextColor(Color.WHITE);
                        verifyPhone.setText("Phone number verified!");
                        if(accountActivated.equals("false")){
                            Toast.makeText(getActivity(), "Photo number verified Successfully", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), "Please complete the verification process", Toast.LENGTH_SHORT).show();

                        }else {
                            Intent intent = new Intent(getActivity(), Profile.class);
                            startActivity(intent);
                        }
                    }
                }else {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //handle databaseError
                Toast.makeText(getActivity(), "The read failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        verifyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isphoneVerified){
                    Toast.makeText(getActivity(), "Phone number is already verified", Toast.LENGTH_SHORT).show();
                    verifyPhone.setBackgroundColor(Color.GREEN);
                    verifyPhone.setTextColor(Color.WHITE);
                    verifyPhone.setText("Phone number verified!");
                }
                else {
                    PhoneVerify nextFrag = new PhoneVerify();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, nextFrag)
                            .addToBackStack(null)
                            .commit();
                }

            }
        });


        activataAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isphoneVerified){
                    Toast.makeText(getActivity(), "Please verify your phone number", Toast.LENGTH_SHORT).show();
                }
                else {
                    ActivateFragment nextFrag2 = new ActivateFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, nextFrag2)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });


        return rootView;
    }


}
