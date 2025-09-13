package com.example.trivia2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Question> questions;

    public FloatingActionButton fabStart;
    public void init()
    {
        fabStart=findViewById(R.id.fabStart);
    }
    public void loadQuestions()
    {
        questions=new ArrayList<>();

        //String info_url="https://raw.githubusercontent.com/ms0157/questions.json/main/questi\ons.json";
        //https://github.com/ms0157/questions.xml/blob/main/questions.xml

        String info_url="https://raw.githubusercontent.com/ms0157/questions.json/refs/heads/main/questions.json";
        new FromNet().execute(info_url);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Log.d("MARIELA", "here");
        init();
        loadQuestions();

        fabStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
