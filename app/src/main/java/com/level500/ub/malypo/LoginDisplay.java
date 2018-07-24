package com.level500.ub.malypo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by jamestakor on 7/17/18.
 */

public class LoginDisplay extends AppCompatActivity {

    private FirebaseAuth mAuth;
    float x1,y1;
    float x2,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getting instance of firebase
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginDisplay.this, Profile.class));
            finish();
        }else {
            FirebaseAuth.getInstance().signOut();
            setContentView(R.layout.login_display);
        }
    }

    public void gotoRegister(View view){
        Intent intent = new Intent(LoginDisplay.this, RegisterDisplay.class);
        startActivity(intent);
    }

    public void loginScreen(View view){
        Intent intent = new Intent(LoginDisplay.this, LoginActivity.class);
        startActivity(intent);
    }

    public boolean onTouchEvent(MotionEvent touchevent)
    {
        switch (touchevent.getAction())
        {
// when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN:
            {
                x1 = touchevent.getX();
                y1 = touchevent.getY();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                x2 = touchevent.getX();
                y2 = touchevent.getY();
//if left to right sweep event on screen
                if (x1 < x2)
                {
                    Toast.makeText(this, "Left to Right Swap Performed", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(LoginDisplay.this,MainActivity.class);
                    startActivity(i);
                }
// if right to left sweep event on screen
                if (x1 > x2)
                {
                    Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_LONG).show();

                }
// if UP to Down sweep event on screen
                if (y1 < y2)
                {
// Toast.makeText(this, "UP to Down Swap Performed", Toast.LENGTH_LONG).show();
                }
//if Down to UP sweep event on screen
                if (y1 > y2)
                {
// Toast.makeText(this, "Down to UP Swap Performed", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
        return false;
    }

}
