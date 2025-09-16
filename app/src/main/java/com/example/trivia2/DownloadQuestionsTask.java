package com.example.trivia2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class DownloadQuestionsTask extends AsyncTask {
    public GameActivity activity;
    public DownloadQuestionsTask(GameActivity activity)
    {
        Log.d("MARIELA","Download constructor..."+Boolean.toString(MainActivity.isGameReady));

        this.activity=activity;
    }
    @Override
    protected Object doInBackground(Object[] objects) {

        Log.d("MARIELA","Download doInBackground..."+Boolean.toString(MainActivity.isGameReady));

        try {
        while (!MainActivity.isGameReady) {
            Log.d("MARIELA","Waiting..."+Boolean.toString(MainActivity.isGameReady));
            Thread.sleep(100);
        }

        }
        catch(Exception e)
        {
            Log.d("MARIELA","Error waiting");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.d("MARIELA","Download onPostExecute..."+Boolean.toString(MainActivity.isGameReady));

        Log.d("MARIELA","Download Start game");
        activity.llQuestions.setVisibility(View.VISIBLE);
       // activity.pbReady.setVisibility(View.INVISIBLE);
       // activity.tvLoading.setVisibility(View.INVISIBLE);
        activity.pbReady.setVisibility(View.GONE);
        activity.tvLoading.setVisibility(View.GONE);
        activity.getRandomQuestion();

    }
}
