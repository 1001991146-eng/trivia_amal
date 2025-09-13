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
    /**
     *    onPreExecute
     *    פעולה שרצה לפני השליחה של הבקשה הא-סינכרונית
     */
    protected void onPreExecute() {

        super.onPreExecute();
        Log.d("MARIELA","onPreExecute");

    }
    /**
     *    doInBackground
     *    פעולה שרצה תוך כדי ביצוע    הבקשה הא-סינכרונית
     */
    @Override
    protected String doInBackground(String... strings) {
        Log.d("MARIELA","doInBackground");

        URL url = null;
        String temp = "";
        all="";
        try {
            Log.d("MARIELA",strings[0]);
            // create connection
            url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream is;
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                // if data recieved - open as input stream
                is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                // convert stream to buffer
                BufferedReader br = new BufferedReader(isr);
                // go over each line in buffer
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
    /**
     *    onPostExecute
     *    פעולה שרצה אחרי סיום  ביצוע    הבקשה הא-סינכרונית
     */
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.d("MARIELA","onPostExecute"+all);
        // init questions
        MainActivity.questions=new ArrayList<Question>();
        try{
            // covert from string to JSON
            JSONArray jsonArray=new JSONArray(all);
            String question;
            int number=0, correct_answer=0;
            JSONObject answer;
            // parse json
            for(int i=0;i<jsonArray.length();i++)
            {
                /*
                example entry in json:
                {
 		        "question": "How many whiskers does the average cat have?",
 		        "answers": [
 		                	"8",
 		                	"12",
 		                	"16",
 		                	"24"
                    		],
 	        	"correct_answer": 2
 	            },
                 */
                JSONObject arrElem=jsonArray.getJSONObject(i);
                // question
                question=arrElem.getString("question");
                Log.d("MARIELA","found question:"+question);
                // array answers
                JSONArray answersArray = arrElem.getJSONArray("answers");
                String[] answers = new String[answersArray.length()];
                for (int j = 0; j < answersArray.length(); j++) {
                    answers[j] = answersArray.getString(j);
                }
                // correct answer
                correct_answer=arrElem.getInt("correct_answer");
                String correct="";
                correct=answers[correct_answer];
                Log.d("MARIELA","correct:"+correct);
                //create question
                Question q=new Question(question,answers[0],answers[1],answers[2],answers[3],correct);
                Log.d("MARIELA","q:"+q.toString());
                // add question to arraylist
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
