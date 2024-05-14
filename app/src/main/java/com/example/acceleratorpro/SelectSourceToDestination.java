package com.example.acceleratorpro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SelectSourceToDestination extends AppCompatActivity {

    private Spinner colorSpinner;
    private Spinner sizeSpinner;
    private Button submitButton;
    private Button show_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectsource_to_destination);

        colorSpinner = findViewById(R.id.color_spinner);
        sizeSpinner = findViewById(R.id.size_spinner);
        submitButton = findViewById(R.id.submit_button);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectSourceToDestination.this, AddorShowPage.class);
                startActivity(intent);
                // Finish the current activity
                finish();
            }
        });


    }
}
