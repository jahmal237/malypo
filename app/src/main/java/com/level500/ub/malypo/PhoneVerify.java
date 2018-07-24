package com.level500.ub.malypo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneVerify extends Fragment {
    private FirebaseAuth mAuth;
    PhoneAuthProvider.ForceResendingToken mResendToken;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        assert mAuth != null;
        final String uid = mAuth.getUid();

        final View rootView = inflater.inflate(R.layout.phone_verify_fragment, container, false);
        final Button verify = (Button) rootView.findViewById(R.id.verify_actual_phone);
        final Button activateRedirect = (Button) rootView.findViewById(R.id.activate_redirect);
        final EditText smsCode = (EditText) rootView.findViewById(R.id.sms_code);
        final TextView smsCodeResend = (TextView) rootView.findViewById(R.id.sms_code_resend);
        final LinearLayout holdSms =  (LinearLayout) rootView.findViewById(R.id.show_sms_code);

        smsCodeResend.setVisibility(View.INVISIBLE);
        holdSms.setVisibility(View.INVISIBLE);
        activateRedirect.setVisibility(View.INVISIBLE);

        final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Toast.makeText(getActivity(), "Cool verified" + credential.getSmsCode(), Toast.LENGTH_SHORT).show();


                assert uid != null;
                mDatabase.child("users").child(uid).child("phoneVerified").setValue(credential.getSmsCode());

                holdSms.setVisibility(View.VISIBLE);
                smsCode.setEnabled(true); smsCode.setFocusable(true);
                smsCode.setClickable(true);
                verify.setVisibility(View.INVISIBLE);
                activateRedirect.setVisibility(View.VISIBLE);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getActivity(), "Failed sending code", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(getActivity(), "Failed sending code too many attempts", Toast.LENGTH_SHORT).show();
                }

                smsCodeResend.setVisibility(View.VISIBLE);
                verify.setVisibility(View.VISIBLE);
                activateRedirect.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Toast.makeText(getActivity(), "Code sent successfully. Check your messages to fill in the form", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                String mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        smsCodeResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText phone = (EditText) rootView.findViewById(R.id.phone_number_verify);
                final EditText smsCode = (EditText) rootView.findViewById(R.id.sms_code);

                final String strph     =  phone.getText().toString().trim();
                final String newph     =  "+237" + strph;
                final String strcode     = smsCode.getText().toString().trim();
                activateRedirect.setVisibility(View.INVISIBLE);
                smsCodeResend.setVisibility(View.INVISIBLE);

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        newph,        // Phone number to verify
                        1  ,               // Timeout duration
                        TimeUnit.MINUTES,   // Unit of timeout
                        Objects.requireNonNull(getActivity()),               // Activity (for callback binding)
                        mCallbacks,         // OnVerificationStateChangedCallbacks
                        mResendToken);
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText phone = (EditText) rootView.findViewById(R.id.phone_number_verify);
                final EditText smsCode = (EditText) rootView.findViewById(R.id.sms_code);

                final String strph     =  phone.getText().toString().trim();
                final String newph     =  "+237" + strph;
                final String strcode     = smsCode.getText().toString().trim();

                if (TextUtils.isEmpty(strph)) {
                    Toast.makeText(getActivity(), "Please enter a phone number!", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    if(!digitsOnly(strph)){
                        Toast.makeText(getActivity(), "Please enter a valid phone number!", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                newph,                                  // Phone number to verify
                                1,                                    // Timeout duration
                                TimeUnit.MINUTES,                       // Unit of timeout
                                Objects.requireNonNull(getActivity()),  // Activity (for callback binding)
                                mCallbacks);                            // OnVerificationStateChangedCallbacks

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(strcode)) {
                                    Toast.makeText(getActivity(), "Please enter a sms code", Toast.LENGTH_SHORT).show();
                                }else {
                                    if(!digitsOnly(strcode)){
                                        Toast.makeText(getActivity(), "Please enter a valid sms code!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }, 1000);

                    }
                }

            }
        });

        activateRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText phone = (EditText) rootView.findViewById(R.id.phone_number_verify);
                final EditText smsCode = (EditText) rootView.findViewById(R.id.sms_code);

                final String strph     =  phone.getText().toString().trim();
                final String strcode     = smsCode.getText().toString().trim();

                if (TextUtils.isEmpty(strph)) {
                    Toast.makeText(getActivity(), "Please enter a phone number!", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    if(!digitsOnly(strph)){
                        Toast.makeText(getActivity(), "Please enter a valid phone number!", Toast.LENGTH_SHORT).show();
                        return;
                    }else {

                        if (TextUtils.isEmpty(strcode)) {
                            Toast.makeText(getActivity(), "Please enter the sms code", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            if(!digitsOnly(strcode)){
                                Toast.makeText(getActivity(), "Please enter a valid phone number!", Toast.LENGTH_SHORT).show();
                                return;
                            }else {
                                mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists())
                                        {
                                            User user = dataSnapshot.getValue(User.class);
                                            assert user != null;
                                            if(user.phoneVerified.equals(strcode)) {
                                            }else {
                                                smsCodeResend.setVisibility(View.VISIBLE);
                                                PhoneVerifyActivationFragment nextFrag = new PhoneVerifyActivationFragment();
                                                getActivity().getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.fragment_container, nextFrag)
                                                        .addToBackStack(null)
                                                        .commit();
                                            }
                                            Toast.makeText(getActivity(), "Wrong code try again ", Toast.LENGTH_SHORT).show();

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




                            }
                            }

                        }

                    }
                }


        });


        return rootView;
    }


    private boolean digitsOnly(String phone){

        return TextUtils.isDigitsOnly(phone);
    }
}
