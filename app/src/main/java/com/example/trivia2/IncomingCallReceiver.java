package com.example.trivia2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class IncomingCallReceiver extends BroadcastReceiver {

    private AudioManager audioManager;
    /**
     * onReceive
     *      פעולה שמקבלת התראות
     *      broadcast reciever
     *      כאשר נכנסת שיחה נבקש להפסיק זמנית את המשחק
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            // if changes status of phone
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state))
            {
                // if ringing - stop the game
                GameActivity.isTimerPaused=true;
                pauseMusic(context);
                Bundle extras= intent.getExtras();
                String phone="";
                if (extras!=null) {
                    if (extras != null) {
                        for (String key : extras.keySet()) {
                            Object value = extras.get(key);
                        }
                    }
                    // check number calling
                    phone = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    //phoneNumber=intent.getStringExtra( "incoming_number"); תלוי במערכת ההפעלה
                    // notify user about the call with phone number
                    if (phone != null) {
                        Toast.makeText(context, "Incoming call from " + phone, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Incoming call , number not defined ", Toast.LENGTH_LONG).show();
                    }
                }
            }
            else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                // The call has ended, you can now resume your game.
                // Implement your resume game logic here.
                // if stoped phone call - go back to game
                GameActivity.isTimerPaused=false;
                resumeMusic(context);
            }
        }
    }
    /**
     * pauseMusic
     * הפעולה משתיקה את המוזיקה
     */
    private void pauseMusic(Context context) {
        GameActivity.play=false;
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        // Pause the music stream (e.g., MUSIC_STREAM or whatever is appropriate)
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
    }
    /**
     * resumeMusic
     * הפעולה מחזירה את המוזיקה
     */
    private void resumeMusic(Context context) {
        GameActivity.play=true;
        if (audioManager == null) {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        // Resume the music stream
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }
}


