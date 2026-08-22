package com.allied.music;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.google.common.util.concurrent.ListenableFuture;

public class MainActivity extends Activity {

    private MediaController controller;
    private ListenableFuture<MediaController> controllerFuture;

    private Button playButton;
    private Button previousButton;
    private Button nextButton;
    private SeekBar progressBar;

    private TextView songTitle;
    private TextView songArtist;
    private TextView currentTime;
    private TextView totalTime;

    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        playButton = findViewById(R.id.playButton);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        progressBar = findViewById(R.id.progressBar);

        songTitle = findViewById(R.id.songTitle);
        songArtist = findViewById(R.id.songArtist);
        currentTime = findViewById(R.id.currentTime);
        totalTime = findViewById(R.id.totalTime);

        SessionToken sessionToken =
                new SessionToken(this, PlayerService.class);

        controllerFuture =
                new MediaController.Builder(this, sessionToken)
                        .buildAsync();

        controllerFuture.addListener(() -> {
            try {
                controller = controllerFuture.get();

                setupPlayer();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, getMainExecutor());

        playButton.setOnClickListener(v -> {
            if (controller != null) {
                if (controller.isPlaying()) {
                    controller.pause();
                } else {
                    controller.play();
                }
            }
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

        progressBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (fromUser && controller != null) {
                            controller.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );
    }

    private void setupPlayer() {

        updatePlayerUI();

        controller.addListener(new Player.Listener() {

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                updatePlayButton();
            }

            @Override
            public void onMediaMetadataChanged(
                    androidx.media3.common.MediaMetadata metadata) {

                updatePlayerUI();
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                updatePlayerUI();
            }
        });

        updateProgress();
    }

    private void updatePlayerUI() {

        if (controller == null) {
            return;
        }

        if (controller.getMediaMetadata().title != null) {
            songTitle.setText(
                    controller.getMediaMetadata().title.toString()
            );
        }

        if (controller.getMediaMetadata().artist != null) {
            songArtist.setText(
                    controller.getMediaMetadata().artist.toString()
            );
        }

        updatePlayButton();
    }

    private void updatePlayButton() {

        if (controller != null && controller.isPlaying()) {
            playButton.setText("⏸");
        } else {
            playButton.setText("▶");
        }
    }

    private void updateProgress() {

        if (controller != null) {

            long duration = controller.getDuration();
            long position = controller.getCurrentPosition();

            if (duration > 0) {

                progressBar.setMax((int) duration);
                progressBar.setProgress((int) position);

                currentTime.setText(formatTime(position));
                totalTime.setText(formatTime(duration));
            }
        }

        handler.postDelayed(this::updateProgress, 500);
    }

    private String formatTime(long milliseconds) {

        long seconds = milliseconds / 1000;

        long minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format(
                "%d:%02d",
                minutes,
                seconds
        );
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);

        if (controllerFuture != null) {
            MediaController.releaseFuture(controllerFuture);
        }

        super.onDestroy();
    }
}
