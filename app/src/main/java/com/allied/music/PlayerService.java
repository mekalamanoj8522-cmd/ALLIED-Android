package com.allied.music;

import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

public class PlayerService extends MediaSessionService {

    private ExoPlayer player;
    private MediaSession mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create player
        player = new ExoPlayer.Builder(this)
                .build();

        // Audio configuration
        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(C.USAGE_MEDIA)
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .build();

        player.setAudioAttributes(
                audioAttributes,
                true
        );

        // Your online music
        MediaItem mediaItem =
                new MediaItem.Builder()
                        .setUri(
                                "https://mekalamanoj8522-cmd.github.io/sample.mp3"
                        )
                        .setMediaMetadata(
                                new MediaMetadata.Builder()
                                        .setTitle("ALLIED Music")
                                        .setArtist("ALLIED")
                                        .setAlbumTitle("ALLIED")
                                        .build()
                        )
                        .build();

        player.setMediaItem(mediaItem);

        // Create MediaSession
        mediaSession =
                new MediaSession.Builder(
                        this,
                        player
                )
                .build();

        // Prepare the song
        player.prepare();

        // Do NOT automatically play here.
        // The Play button in MainActivity will start it.
    }

    @Override
    public MediaSession onGetSession(
            MediaSession.ControllerInfo controllerInfo) {

        return mediaSession;
    }

    @Override
    public void onDestroy() {

        if (mediaSession != null) {
            mediaSession.release();
            mediaSession = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }

        super.onDestroy();
    }
}