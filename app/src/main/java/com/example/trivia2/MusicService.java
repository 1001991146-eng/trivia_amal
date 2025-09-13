package com.example.trivia2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {

    public MediaPlayer player;
    private int resourceId;
    private boolean isPlaying = false; // Track the playback state

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isPlaying) {
            player = MediaPlayer.create(this, R.raw.tahat);
            player.setLooping(true);
            player.start();
            isPlaying = true;
            return START_STICKY;//להפעיל מחדש?
        }
        else {
            if (player.isPlaying()) {
                player.pause(); // Pause the music
            } else {
                player.start(); // Resume the music
            }

        }
        return START_STICKY;
    }
    private void pauseMusic() {
        if (player != null && player.isPlaying()) {
            player.pause();
            isPlaying = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}