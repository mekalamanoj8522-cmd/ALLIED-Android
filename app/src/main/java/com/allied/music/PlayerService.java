package com.allied.music;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

public class PlayerService extends MediaSessionService {

    private ExoPlayer player;
    private MediaSession mediaSession;

    @Override
    public void onCreate() {
        super.onCreate();

        player = new ExoPlayer.Builder(this)
                .build();

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
        

        mediaSession =
                new MediaSession.Builder(this, player)
                        .setCallback(
                                new MediaSession.Callback() {
                                }
                        )
                        .build();

        player.prepare();

        // Start playing automatically
        player.play();
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
