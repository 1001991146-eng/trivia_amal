package com.example.trivia2;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InstructionsActivity extends AppCompatActivity {
    public TextView tvInstructions;
    /**
     * init
     *      פעולה שמאתחלת את אקטיביטי מדריך למשתמש
     * עדכון קישורים לרכיבי המסך
     */
    public void init()
    {
        tvInstructions=findViewById(R.id.tvInstructions);
    }
    /**
     * readIntroductionsFromFile
     *      פעולה שפותחת קובץ לקריאה אשר מכיל מדריך למשתמש ויוצר ממנו מחרוזת
     */
    public String readIntroductionsFromFile()
    {
        InputStream is;
        InputStreamReader isr;
        BufferedReader br;
        is=getResources().openRawResource(R.raw.instructions);
        isr=new InputStreamReader(is);
        br=new BufferedReader(isr);
        String all="";
        String st;
        try {
            st=br.readLine();
            while (st!=null) {
                all+=st+"\n";
                st=br.readLine();
            }
            br.close();
        }
        catch (IOException e) {
            Toast.makeText(this,"could not open", Toast.LENGTH_SHORT).show();
        }
        return all;
    }
    /**
     * onCreate
     *      פעולה שמאתחלת את אקטיביטי מדריך למשתמש
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_instructions);
        init();
        // read all instructions from file
        String all=readIntroductionsFromFile();
        // show instructions
        tvInstructions.setText(all);
        Log.d("MARIELA",all);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


}

