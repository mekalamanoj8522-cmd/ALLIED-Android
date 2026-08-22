package com.allied.music;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Intent serviceIntent =
                new Intent(this, PlayerService.class);

        startForegroundService(serviceIntent);
    }
}
