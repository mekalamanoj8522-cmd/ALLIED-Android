package com.allied.music;

import android.app.Activity;
import android.os.Bundle;
import android.content.ComponentName;
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

    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {

            if (controller != null) {

                long position = controller.getCurrentPosition();
                long duration = controller.getDuration();

                if (duration > 0) {
                    progressBar.setMax((int) duration);
                    progressBar.setProgress((int) position);
                    totalTime.setText(formatTime(duration));
                }

                currentTime.setText(formatTime(position));

                updatePlayButton();
            }

            if (progressBar != null) {
                progressBar.postDelayed(this, 500);
            }
        }
    };

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
        currentTime.setText("0:00");
        totalTime.setText("0:00");

        SessionToken sessionToken =
                new SessionToken(
                        this,
                        new ComponentName(this, PlayerService.class)
                );

        controllerFuture =
                new MediaController.Builder(
                        this,
                        sessionToken
                ).buildAsync();

        controllerFuture.addListener(
                () -> {

                    try {

                        controller = controllerFuture.get();

                        setupPlayerControls();

                        progressBar.post(progressRunnable);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                Runnable::run
        );
    }

    private void setupPlayerControls() {

        playButton.setOnClickListener(v -> {

            if (controller == null) {
                return;
            }

            if (controller.isPlaying()) {
                controller.pause();
            } else {
                controller.play();
            }

            updatePlayButton();
        });

        previousButton.setOnClickListener(v -> {

            if (controller != null &&
                    controller.hasPreviousMediaItem()) {

                controller.seekToPrevious();
            }
        });

        nextButton.setOnClickListener(v -> {

            if (controller != null &&
                    controller.hasNextMediaItem()) {

                controller.seekToNext();
            }
        });

        progressBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (fromUser) {
                            currentTime.setText(
                                    formatTime(progress)
                            );
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar) {

                        if (controller != null) {
                            controller.seekTo(
                                    seekBar.getProgress()
                            );
                        }
                    }
                }
        );

        updatePlayButton();
    }

    private void updatePlayButton() {

        if (playButton == null) {
            return;
        }

        if (controller != null && controller.isPlaying()) {
            playButton.setText("⏸");
        } else {
            playButton.setText("▶");
        }
    }

    private String formatTime(long milliseconds) {

        if (milliseconds < 0) {
            milliseconds = 0;
        }

        long totalSeconds = milliseconds / 1000;

        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        return String.format(
                Locale.getDefault(),
                "%d:%02d",
                minutes,
                seconds
        );
    }

    @Override
    protected void onDestroy() {

        if (progressBar != null) {
            progressBar.removeCallbacks(progressRunnable);
        }

        if (controller != null) {
            controller.release();
            controller = null;
        }

        if (controllerFuture != null) {
            controllerFuture.cancel(false);
        }

        super.onDestroy();
    }
}
