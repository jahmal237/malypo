package com.level500.ub.malypo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ActivateFragment extends Fragment {

    boolean isIdTaken = false;
    boolean isAcctivated = false;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private LinearLayout linearLayoutCard;
    private ImageView myImg;
    int whichPicture;
    String childnode = "photoID";

    //firebase init
    FirebaseUser mAuth;
    FirebaseStorage storage;
    DatabaseReference mDatabase;
    StorageReference storageReference;
    Bitmap finalImage;

    Uri filepath;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is HomeFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
        final View rootView = inflater.inflate(R.layout.activate_fragment, container, false);

        final Button takeId = (Button) rootView.findViewById(R.id.photo_id_button);
        final Button activataAcc = (Button) rootView.findViewById(R.id.self_pictures_button);
        linearLayoutCard = (LinearLayout) rootView.findViewById(R.id.show_result_scan);
        final Button uploadBtn = (Button) rootView.findViewById(R.id.save_my_id);
        myImg = (ImageView) rootView.findViewById(R.id.my_id_result);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        assert mAuth != null;
        String uid = mAuth.getUid();


        linearLayoutCard.setVisibility(View.INVISIBLE);



        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(filepath, whichPicture);
            }
        });

        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    assert user != null;
                    String photoVerified = user.photoID;
                    String accountActivated = user.accountActivated;
                    if(photoVerified.equals("false")){
                        Toast.makeText(getActivity(), "Please verify your phone number", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        isIdTaken = true;
                        whichPicture = 2;
                        takeId.setBackgroundColor(Color.GREEN);
                        takeId.setTextColor(Color.WHITE);
                        takeId.setText("Photo ID verified!");
                        Toast.makeText(getActivity(), "Photo ID verified Successfully", Toast.LENGTH_SHORT).show();

                        if(accountActivated.equals("false")){
                            Toast.makeText(getActivity(), "Please take a photo of you", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            isAcctivated = true;
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
                int code = databaseError.getCode();
                if(code == -3){

                }else {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        takeId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isIdTaken){
                    Toast.makeText(getActivity(), "Photo ID is already verified", Toast.LENGTH_SHORT).show();
                    takeId.setBackgroundColor(Color.GREEN);
                    takeId.setTextColor(Color.WHITE);
                    takeId.setText("Photo ID verified!");
                }
                else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }

            }
        });



        activataAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isIdTaken){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setCancelable(false);
                    dialog.setTitle("Malypo info");
                    dialog.setMessage("Please take a picture of your beautiful self to confirm identity." );
                    dialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                        }
                    })
                            .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(), "Please help us complete the verification process", Toast.LENGTH_SHORT).show();
                                }
                            });

                    final AlertDialog alert = dialog.create();
                    alert.show();
                }
                else {
                    Toast.makeText(getActivity(), "Please first upload a valid photo of your ID above", Toast.LENGTH_SHORT).show();
                }
            }
        });

       // activateRedirect.setOnClickListener(new View.OnClickListener()));
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                filepath = getImageUri(photo);
                linearLayoutCard.setVisibility(View.VISIBLE);
                finalImage = photo;
                myImg.setImageBitmap(photo);

            }else {
                Toast.makeText(getActivity(), "Did not take picture please take a picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getImageUri(Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }


    private void uploadImage(Uri filepath, final int flag) {

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        assert mAuth != null;
        final String uid = mAuth.getUid();

        if(filepath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String pic_name = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());


            final StorageReference ref = storageReference.child("userIdCards/"+ uid +"/"+ pic_name);

            StorageTask<UploadTask.TaskSnapshot> mUploadTask = ref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getMetadata();
                            Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploading " + (int) progress + "%");
                        }
                    });


            Task<Uri> urlTask = mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        progressDialog.dismiss();
                        if(flag == 2){
                            childnode = "profilePic";
                            mDatabase.child("users").child(uid).
                                    child("accountActivated").setValue("true");
                            mDatabase.child("users").child(uid).
                                    child(childnode).setValue(downloadUri.toString());

                        }else{
                            mDatabase.child("users").child(uid).
                                    child(childnode).setValue(downloadUri.toString());
                        }

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }else {
            Toast.makeText(getActivity(), "Did not take picture please take a picture", Toast.LENGTH_SHORT).show();
        }
    }



}
