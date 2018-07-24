package com.level500.ub.malypo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class About_Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_help);


        Button btn = (Button) findViewById(R.id.email_malypo_button);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[] { "test@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Malypo Android");

                startActivity(Intent.createChooser(intent, "Email via..."));
            }
        });


        Button btn2 = (Button) findViewById(R.id.phone_malypo_button);
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String phone = "+237 663 60 60 60";
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                        "tel", phone, null));
                startActivity(phoneIntent);
            }
        });

        Button btn3 = (Button) findViewById(R.id.malypo_website_help);
        btn3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = "https://www.google.com/";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        Button btn4 = (Button) findViewById(R.id.malypo_website_about);
        btn4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = "https://www.google.com/";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

}
