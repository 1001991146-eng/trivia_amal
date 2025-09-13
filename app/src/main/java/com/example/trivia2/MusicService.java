package com.example.trivia2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {

    public MediaPlayer player;
    private int resourceId;
    private boolean isPlaying = false; // Track the playback state
    /**
     * MusicService
     *      פעולה שבונה שירות חדש
     */
    public MusicService() {
    }

    /**
     * onBind
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    /**
     * onStartCommand
     * פעולה אשר מתחילה שירות
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isPlaying) {
            // if starting service then start  playing
            player = MediaPlayer.create(this, R.raw.tahat);
            player.setLooping(true);
            player.start();
            isPlaying = true;
            return START_STICKY;//להפעיל מחדש?
        }
        else {
            // if service already running-check is currently playing
            // if playing - then stop
            if (player.isPlaying()) {
                player.pause(); // Pause the music
            } else {
                // if not playing - then start
                player.start(); // Resume the music
            }
        }
        return START_STICKY;
    }
    /**
     * pauseMusic
     * פעולה אשר מתחילה זמנית את המוזיקה בשירות
     */
    private void pauseMusic() {
        if (player != null && player.isPlaying()) {
            player.pause();
            isPlaying = false;
        }
    }
    /**
     * onDestroy
     * פעולה אשר מסיימת שירות
     * ומשתיקה את המוזיקה
     */
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