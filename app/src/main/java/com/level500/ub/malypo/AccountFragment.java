package com.level500.ub.malypo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class AccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
        assert mAuth != null;
        String uid = mAuth.getUid();

        final View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        final TextView fNameText = (TextView)rootView.findViewById(R.id.user_profile_name);
        final TextView emailText = (TextView)rootView.findViewById(R.id.user_profile_email);

        final TextView invite  = (TextView)rootView.findViewById(R.id.invite_tab);
        final TextView help  = (TextView)rootView.findViewById(R.id.help_tab);
        final TextView transac  = (TextView)rootView.findViewById(R.id.transaction_tab);
        final ImageView img = (ImageView)rootView.findViewById(R.id.user_profile_photo);

        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    String fullname = user.firstname + " " + user.lastname;
                    fNameText.setText(fullname);
                    emailText.setText(user.email);
                    if(user.profilePic.equals("false")) {
                    }else {
                        Picasso.with(getActivity()).load(user.profilePic).into(img);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                int code = databaseError.getCode();
                if(code == -3){

                }else {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), LoginDisplay.class);
                    startActivity(intent);
                }

            }
        });

        invite.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[] { "test@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Malypo Android");
                intent.putExtra(Intent.EXTRA_TEXT, Objects.requireNonNull(getActivity()).getString(R.string.invite_info));
                startActivity(Intent.createChooser(intent, "Email via..."));
            }
        });



        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getActivity(), About_Help.class);
                startActivity(intent2);
            }
        });

        transac.setOnClickListener(new View.OnClickListener() {
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


class User {


    public String firstname;
    public String lastname;
    public String email;
    public String country;
    public String phone;
    public String phoneVerified;
    public String accountActivated;
    public String photoID;
    public String profilePic;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public User(String firstname, String lastname, String email, String country, String phone, String phoneVerified ,String accountActivated, String photoID, String profilePic) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.country = country;
        this.phone = phone;
        this.phoneVerified = phoneVerified;
        this.accountActivated = accountActivated;
        this.photoID = photoID;
        this.profilePic = profilePic;
    }


}
