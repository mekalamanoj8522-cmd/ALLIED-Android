package com.allied.music;

import android.app.Activity;
import android.os.Bundle;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Locale;

public class MainActivity extends Activity {

    private MediaController controller;
    private ListenableFuture<MediaController> controllerFuture;

    private TextView songTitle;
    private TextView songArtist;
    private TextView currentTime;
    private TextView totalTime;
    private SeekBar progressBar;

    private Button playButton;
    private Button previousButton;
    private Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        songTitle = findViewById(R.id.songTitle);
        songArtist = findViewById(R.id.songArtist);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);
        progressBar = findViewById(R.id.progressBar);

        playButton = findViewById(R.id.playButton);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);

        songTitle.setText("ALLIED Music");
        songArtist.setText("ALLIED");

        // Start the music service
        Intent serviceIntent =
                new Intent(this, PlayerService.class);

        startForegroundService(serviceIntent);

        // Connect to MediaSession
        SessionToken token =
                new SessionToken(
                        this,
                        new ComponentName(
                                this,
                                PlayerService.class
                        )
                );

        controllerFuture =
                new MediaController.Builder(
                        this,
                        token
                ).buildAsync();

        controllerFuture.addListener(() -> {

            try {

                controller = controllerFuture.get();

                setupControls();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }, Runnable::run);
    }

    private void setupControls() {

        playButton.setOnClickListener(v -> {

            if (controller == null) {
                return;
            }

            if (controller.isPlaying()) {
                controller.pause();
            } else {
                controller.play();
            }

            updateButton();
        });

        previousButton.setOnClickListener(v -> {

            if (controller != null) {
                controller.seekToPrevious();
            }
        });

        nextButton.setOnClickListener(v -> {

            if (controller != null) {
                controller.seekToNext();
            }
        });

        updateButton();
    }

    private void updateButton() {

        if (controller != null && controller.isPlaying()) {
            playButton.setText("⏸");
        } else {
            playButton.setText("▶");
        }
    }

    private String formatTime(long milliseconds) {

        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format(
                Locale.getDefault(),
                "%d:%02d",
                minutes,
                seconds
        );
    }

    @Override
    protected void onDestroy() {

        if (controller != null) {
            controller.release();
            controller = null;
        }

        super.onDestroy();
    }
}