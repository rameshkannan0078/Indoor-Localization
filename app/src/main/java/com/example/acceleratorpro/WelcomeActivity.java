package com.example.acceleratorpro;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_welcome);

        Button getStartedButton = findViewById(R.id.get_started_button);
        ImageView imageView = findViewById(R.id.my_image_view);
        TextView textView = findViewById(R.id.text_view);
        textView.setText("Find your way inside with ease using Indoor Localization technology.");
        textView.setTypeface(null, Typeface.BOLD);
        textView.setTextSize(20);


        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(imageView, View.ROTATION, 0f, 360f);
        rotateAnimation.setDuration(86400 * 1000); // 24 hours in milliseconds
        rotateAnimation.setRepeatCount(ObjectAnimator.INFINITE); // repeat indefinitely
        rotateAnimation.setInterpolator(new LinearInterpolator()); // ensure a constant rotation speed
        rotateAnimation.start();



        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the MainActivity activity
                Intent intent = new Intent(WelcomeActivity.this, SelectSourceToDestination.class);
                startActivity(intent);
                // Finish the current activity
                finish();
            }
        });
    }
}
