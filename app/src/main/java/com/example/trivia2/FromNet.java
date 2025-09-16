package com.example.trivia2;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.ListenableFuture;

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
import java.util.concurrent.ExecutionException;

public class FromNet extends AsyncTask<String , Void, String> {
    public Handler verifyQuestions;
    public final int MSG_QUESTIONS_LOADED=1;

    public String all="";

    public FromNet(Handler verifyQuestions)
    {
        this.verifyQuestions=verifyQuestions;
    }
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
       // all= getMeExternalInternetQuestions(strings[0]);
       all = getMeGeminiQuestions();

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
                Log.d("MARIELA","Gemini found question:"+question);
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
                Log.d("MARIELA","Gemini correct:"+correct);
                //create question
                Question q=new Question(question,answers[0],answers[1],answers[2],answers[3],correct);
                Log.d("MARIELA","Gemini q:"+q.toString());
                // add question to arraylist
                MainActivity.questions.add(q);
                Log.d("MARIELA","Gemini done:"+MainActivity.questions.toString());
                // handler send message can start game ?!
                Log.d("MARIELA","Gemini num "+Integer.toString(MainActivity.questions.size()));
            }
            verifyQuestions.sendEmptyMessage(MSG_QUESTIONS_LOADED);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public  String getMeExternalInternetQuestions()
    {
        String info_url="https://raw.githubusercontent.com/ms0157/questions.json/refs/heads/main/questions.json";

        URL url = null;
        String temp = "";
        all="";
        try {
            Log.d("MARIELA",info_url);
            // create connection
            url = new URL(info_url);
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
    public String getMeGeminiQuestions()
    {
        // --- הגדרות Gemini ---
        // יש להחליף את "GEMINI_API_KEY" במפתח ה-API שלך
        // וודאי שאתה מגדיר אותו ב-buildConfigField כפי שהוצג למעלה
        // אחרת, תצטרך להגדיר אותו כאן ישירות (פחות מומלץ)
        String apiKey = BuildConfig.GEMINI_API_KEY; // נניח ששמרת אותו ב-BuildConfig

        all="";
       apiKey = "AIzaSyBdQe8oJv7O7BONRHbaU27a8sjTktWhHBY";
        Log.d("MARIELA","Gemini Key: "+apiKey);
        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash", // או מודל אחר כמו "gemini-1.5-flash"
                apiKey
        );
        Log.d("MARIELA","Gemini Questions");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        // --- בניית הפרומפט (ההוראה ל-Gemini) ---
        String prompt = "Generate 10 trivia questions in JSON format. Each question should have a 'question' string, an 'answers' array of 4 strings, and a 'correct_answer' integer (0-3 indicating the index of the correct answer). Ensure the output is a valid JSON array of questions. Example: [{\"question\": \"What is 2+2?\", \"answers\": [\"3\", \"4\", \"5\", \"6\"], \"correct_answer\": 1}]";
        Log.d("MARIELA","Gemini Prompt:"+prompt);
        Content content = new Content.Builder().addText(prompt).build();

        // --- שליחת הבקשה ---
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        try {
            String geminiResponseJson = response.get().getText();
            Log.d("MARIELA", "Gemini Raw Response: " + geminiResponseJson);

            // וודא שהתגובה היא JSON תקין (Gemini יכול לפעמים להוסיף טקסט לפני או אחרי ה-JSON)
            // נחפש את ה-JSON הראשון ונוציא אותו
            int startIndex = geminiResponseJson.indexOf("[");
            int endIndex = geminiResponseJson.lastIndexOf("]");

            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                all = geminiResponseJson.substring(startIndex, endIndex + 1);
            } else {
                Log.e("MARIELA", "Failed to extract valid JSON from Gemini response: " + geminiResponseJson);
                all = "[]"; // ריק כדי למנוע קריסה
            }

        } catch (InterruptedException | ExecutionException e) {
            Log.e("MARIELA", "Error communicating with Gemini: " + e.getMessage());
            e.printStackTrace();
            all = "[]"; // במקרה של שגיאה, נחזיר JSON ריק
        }

        return all;
    }
}
