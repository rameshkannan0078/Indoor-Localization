package com.example.acceleratorpro;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class Instruction extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private WebView gifWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instruction_page);

        gifWebView = findViewById(R.id.gifWebView);
        gifWebView.setWebViewClient(new WebViewClient());

        // Load the GIF file from the res/raw folder
        String gifPath = "file:///android_res/raw/voice2.gif";
        gifWebView.loadUrl(gifPath);

        // Initialize MediaPlayer with the audio file
        mediaPlayer = MediaPlayer.create(this, R.raw.audio_file);
        mediaPlayer.setOnCompletionListener(this);

        // Start playing the audio automatically
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // Release MediaPlayer resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        // Move to the next activity or page
        Intent intent = new Intent(Instruction.this, ShowArrowDestination.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer resources
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
