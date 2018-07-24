package com.level500.ub.malypo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PaymentFragment extends Fragment {

    GridLayout gridLayout;
    DatabaseReference mDatabase;
    FirebaseUser mAuth;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard


        final View rootView = inflater.inflate(R.layout.fragment_payment, container, false);

        gridLayout=(GridLayout)rootView.findViewById(R.id.mainGrid);
            for(int i = 1; i<gridLayout.getChildCount();i++){
                CardView cardView=(CardView)gridLayout.getChildAt(i);
                cardView.setVisibility(View.INVISIBLE);
            }

        setSingleEvent(gridLayout);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        assert mAuth != null;
        String uid = mAuth.getUid();


        mDatabase.child("Payment").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String provider = (String) snapshot.getValue();
                        int result = Integer.parseInt(provider);
                        loadCardView(gridLayout, result);
                    }

                }
                else {

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


        return rootView;
    }



    // we are setting onClickListener for each element
    private void setSingleEvent(GridLayout gridLayout) {
        for(int i = 0; i<gridLayout.getChildCount();i++){
            CardView cardView=(CardView)gridLayout.getChildAt(i);
            final int finalI= i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(finalI == 0){
                        ShowPaymentMethods nextFrag = new ShowPaymentMethods();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, nextFrag)
                                .addToBackStack(null)
                                .commit();
                    }
                    else {
                        Toast.makeText(getActivity(), "Clicked at index " + finalI,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private  void loadCardView(GridLayout gridLayout, int position){
        CardView cardView=(CardView)gridLayout.getChildAt(position);
        cardView.setVisibility(View.VISIBLE);
    }
}
