package com.example.trivia2;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FromNet extends AsyncTask<String , Void, String> {
    public String all="";
    protected void onPreExecute() {

        super.onPreExecute();
        Log.d("MARIELA","onPreExecute");

    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d("MARIELA","doInBackground");

        URL url = null;
        String temp = "";
        all="";
        try {
            Log.d("MARIELA",strings[0]);

            url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream is;
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                while ((temp = br.readLine()) != null) {
                    all += temp + "\n";
                }
                br.close();
            }
        } catch (MalformedURLException e) {
            Log.d("MARIELA","Error getting internet file MalformedURLException:" +e.toString());
        } catch (IOException e) {
            Log.d("MARIELA","Error getting internet file IOException:" +e.toString());
        } catch (Exception e)
        {
            Log.d("MARIELA","Error getting internet file Exception:"+e.toString());
        }
        return all;
    }

    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.d("MARIELA","onPostExecute"+all);
        MainActivity.questions=new ArrayList<Question>();
        try{
            JSONArray jsonArray=new JSONArray(all);
            String question;
            int number=0, correct_answer=0;
            JSONObject answer;
            for(int i=0;i<jsonArray.length();i++)
            {
                JSONObject arrElem=jsonArray.getJSONObject(i);
                question=arrElem.getString("question");
                Log.d("MARIELA","found question:"+question);

                correct_answer=arrElem.getInt("correct_answer");
                JSONArray answersArray = arrElem.getJSONArray("answers");

                String[] answers = new String[answersArray.length()];
                for (int j = 0; j < answersArray.length(); j++) {
                    answers[j] = answersArray.getString(j);
                }
                String correct="";
                correct=answers[correct_answer];
                Log.d("MARIELA","correct:"+correct);

                Question q=new Question(question,answers[0],answers[1],answers[2],answers[3],correct);
                Log.d("MARIELA","q:"+q.toString());

                MainActivity.questions.add(q);
                Log.d("MARIELA","done:"+MainActivity.questions.toString());
                // handler send message can start game ?!


            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }


}
