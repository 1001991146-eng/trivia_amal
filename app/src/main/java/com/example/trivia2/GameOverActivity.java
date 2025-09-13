package com.example.trivia2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameOverActivity extends AppCompatActivity {
    //properties dbs
    public HelperDB helperDB;
    public SQLiteDatabase db;

    public TextView tvPointsGO, tvUserScore, tvAllScores;
    /**
     * init
     *      פעולה שמאתחלת את אקטיביטי  הכי טובים
     * עדכון קישורים לרכיבי המסך
     * קישור למסד נתונים
     */
    public void init()
    {
        tvPointsGO=findViewById(R.id.tvPointsGO);
        tvUserScore=findViewById(R.id.tvUserScore);
        tvAllScores=findViewById(R.id.tvAllScores);
        helperDB=new HelperDB(this);
    }
    /**
     * showAllTopScores
     *      פעולה שמביאה מהמסד נתונים את פרטי הכי טובים ומציגה אותם על המשך
     */
    public void showAllTopScores()
    {
        String[] projection = {
                helperDB.USER_EMAIL_COL,
                helperDB.POINTS_COL
        };
        db = helperDB.getReadableDatabase();

        Cursor cursor = db.query(
                helperDB.TOP_SCORES_TABLE,
                projection,
                null,
                null,
                null,
                null,
                "CAST(points AS INTEGER) DESC"
        );
        if (cursor.getCount() == 0) {
            db.close();
            return;
        }
        cursor.moveToFirst();
        TopScores topScores;

        String all = "Points email \n";
        while (!cursor.isAfterLast()) {

            String column = helperDB.USER_EMAIL_COL;
            int indexColumn = cursor.getColumnIndex(column);
            String email = cursor.getString(indexColumn);

            column = helperDB.POINTS_COL;
            indexColumn = cursor.getColumnIndex(column);
            String score = cursor.getString(indexColumn);
            topScores = new TopScores(email,score);

            all+= topScores.getMaxScore()+" "+ topScores.getEmail()+"\n";
            cursor.moveToNext();
        }
        db.close();
        tvAllScores.setText(all);
    }
    /**
     * onCreate
     *      פעולה שמאתחלת את אקטיביטי ניקוד הכי טובים
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_over);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        Intent intent=getIntent();
        int points = intent.getIntExtra("points",0);
        String email=intent.getStringExtra("email");

        tvPointsGO.setText(Integer.toString(points));
        tvUserScore.setText(email);
        showAllTopScores();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
}

