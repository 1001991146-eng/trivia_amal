package com.example.trivia2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public Handler verifyQuestions;
    public final int MSG_QUESTIONS_LOADED=1;

    public static ArrayList<Question> questions;
    public static boolean isGameReady;

    public FloatingActionButton fabStart;
    /**
     * init
     * הפעולה מטרתה להתחל את כל קישור לכל הרכיבים על מסך
     */
    public void init()
    {

        fabStart=findViewById(R.id.fabStart);
        isGameReady=false;
        verifyQuestions=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                if (message.what==MSG_QUESTIONS_LOADED)
                {
                    Log.d("MARIELA","Game is ready!");
                    isGameReady=true;
                }
                return false;
            }
        });
    }
    /**
     *  loadQuestions
     *      פעולה שמטרתה להביא מקובץ חיצוני את השאלות עבור המשחק.
     *          נגשת למחלקת FromNet
     *              פונה באופן אסינכרוני להורדת קובץ חיצוני
     */
    public void loadQuestions()
    {
        questions=new ArrayList<>();

        //String info_url="https://raw.githubusercontent.com/ms0157/questions.json/main/questi\ons.json";
        //https://github.com/ms0157/questions.xml/blob/main/questions.xml

        new FromNet(verifyQuestions).execute();
        Log.d("MARIELA","Gemini Main Activity "+Integer.toString(MainActivity.questions.size()));

    }

    /**
    * onCreate
     *      פעולה שמאתחלת את אקטיביטי הראשי
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Log.d("MARIELA", "here");
        init();
        loadQuestions();
        Log.d("MARIELA","Gemini After load "+Integer.toString(MainActivity.questions.size()));

        fabStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // עבור למסך התחברות
                    Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
