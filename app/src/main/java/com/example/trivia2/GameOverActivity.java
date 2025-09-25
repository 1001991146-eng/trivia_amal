package com.example.trivia2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GameOverActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference topScoresRef; // A reference to the root or a specific path

    public TextView tvPointsGO, tvUserScore, tvAllScores;
    /**
     * init
     *      פעולה שמאתחלת את אקטיביטי  הכי טובים
     * עדכון קישורים לרכיבי המסך
     * קישור למסד נתונים
     */
    public void init() {
        tvPointsGO = findViewById(R.id.tvPointsGO);
        tvUserScore = findViewById(R.id.tvUserScore);
        tvAllScores = findViewById(R.id.tvAllScores);
        database = FirebaseDatabase.getInstance();
        topScoresRef = database.getReference("TopScores");
    }

    /**
     * showAllTopScoresFB
     *      פעולה שמביאה מהמסד נתונים את פרטי הכי טובים ומציגה אותם על המשך
     */
    public void showAllTopScoresFB()
    {
        Log.d("MARIELA","showAllTopScoresFB");
        // הוספת מאזין לקריאת הנתונים
        topScoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MARIELA","onDataChange "+dataSnapshot.getKey());
                tvAllScores.setText("Points email \n");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("MARIELA", "Child snapshot key: " + snapshot.getKey());

                    TopScores score = snapshot.getValue(TopScores.class);
                    Log.d("MARIELA","onDataChange "+score.toString());

                    if (score != null) {
                        Log.d("MARIELA", "Retrieved Score: " + score.toString());
                        // כאן ניתן להציג את הנתונים למשתמש, למשל ב-RecyclerView או TextView
                        tvAllScores.setText(tvAllScores.getText()+score.getEmail()+":"+score.getMaxScore()+"\n");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // טיפול בשגיאה
                Log.e("FirebaseRead", "Failed to read value.", databaseError.toException());
                tvAllScores.setText("Failed to read scores \n");

            }
        });
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
        showAllTopScoresFB();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
}

