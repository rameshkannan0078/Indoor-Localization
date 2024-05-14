package com.example.acceleratorpro;

import android.content.Intent;
import android.os.Bundle;
        import android.view.View;
        import android.widget.ImageButton;
        import androidx.appcompat.app.AppCompatActivity;

public class AddorShowPage extends AppCompatActivity {

    private ImageButton addButton;
    private ImageButton showButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_or_show);

        addButton = findViewById(R.id.addButton);
        showButton = findViewById(R.id.showButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddorShowPage.this, MainActivity.class);
                startActivity(intent);
                // Finish the current activity
                finish();
            }
        });

        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddorShowPage.this, Instruction.class);
                startActivity(intent);
                // Finish the current activity
                finish();
            }
        });
    }
}
