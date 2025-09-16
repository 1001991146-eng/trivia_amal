package com.example.trivia2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GameActivity extends AppCompatActivity {
    public Context context;

    // properties stop game when call arrives
    public IncomingCallReceiver incomingCallReceiver;
    public IntentFilter intentFilterCall;
    // properties dbs
    public HelperDB helperDB;
    public SQLiteDatabase db;
    // properties current question
    public Question current;
    public int points;
    public int rounds=0;
    public final int MAX_ROUNDS=20;
    // properties timer
    public static boolean play;
    public static boolean isTimerPaused = false; // Track if the timer is paused
    private CountDownTimer countDownTimer;
    public long missing;
    public final  long MAX_TIME=11000;

    public TextView tvQuestion;
    public TextView tvAnswer1, tvAnswer2, tvAnswer3, tvAnswer4, tvTimer;
    public FloatingActionButton fab1, fab2, fab3, fab4;
    public ProgressBar pbQuestion;
    public TextView tvProgress;
    public String email;
    public LinearLayout llQuestions;
    public ProgressBar pbReady;
    public TextView tvLoading;
    /**
     * init
     *      פעולה שמאתחלת את אקטיביטי הראשי של המשחק
     * עדכון קישורים לרכיבי המסך
     * קישור למסד נתונים
     * התחלה מוזיקה
     * איתחול ניקוד ופרטי מחובר
     */
    public void init()
    {
        this.context=context;
        tvTimer=findViewById(R.id.tvTimer);
        tvQuestion=findViewById(R.id.tvQuestion);
        tvAnswer1=findViewById(R.id.tvAnswer1);
        tvAnswer2=findViewById(R.id.tvAnswer2);
        tvAnswer3=findViewById(R.id.tvAnswer3);
        tvAnswer4=findViewById(R.id.tvAnswer4);
        fab1=findViewById(R.id.fab1);
        fab2=findViewById(R.id.fab2);
        fab3=findViewById(R.id.fab3);
        fab4=findViewById(R.id.fab4);
        pbQuestion=findViewById(R.id.pbQuestion);
        tvProgress=findViewById(R.id.tvProgress);
        llQuestions=findViewById(R.id.llQuestions);
        pbReady=findViewById(R.id.pbReady);
        tvLoading=findViewById(R.id.tvLoading);
        Log.d("MARIELA","Gemini Game Activity "+Integer.toString(MainActivity.questions.size()));


        points=0;
        email="";
        helperDB=new HelperDB(this);
        Intent it= new Intent(GameActivity.this, MusicService.class);
        startService(it);

    }
    /**
     * getRandomQuestion
     * בחירת השאלה הבאה והצגתה למשתמש
     * במידה ונגמרו השאלות מחזיר שקר
     */
    public boolean getRandomQuestion()
    {

        int numQuestions=MainActivity.questions.size();
        Log.d("MARIELA","getRandomQuestion"+Integer.toString(numQuestions));

        if (numQuestions>0&&rounds<MAX_ROUNDS) {
            rounds++;
            pbQuestion.setProgress(rounds);
            tvProgress.setText(Integer.toString(rounds)+"/"+Integer.toString(MAX_ROUNDS));

            int min = 0;
            int max = numQuestions - 1;
            int randomize = min + (int) (Math.random() * (max - min + 1));
            current = MainActivity.questions.get(randomize);
            MainActivity.questions.remove(randomize);

            tvQuestion.setText(current.getQuestion());
            tvAnswer1.setText(current.getPossibleAnswer1());
            tvAnswer2.setText(current.getPossibleAnswer2());
            tvAnswer3.setText(current.getPossibleAnswer3());
            tvAnswer4.setText(current.getPossibleAnswer4());
            startTimer();
            return true;
        }
        else {
            return  false;
        }
    }
    /**
     * playedBefore
     * בדיקה האם השחקן הנוכחי שיחק בעבר ושמור ניקוד קודם במסד נתונים
     */
    public boolean playedBefore()
    {
        SQLiteDatabase db = helperDB.getReadableDatabase();
        String[] projection = {helperDB.USER_EMAIL_COL,helperDB.POINTS_COL};
        String selection = helperDB.USER_EMAIL_COL + " = ?";
        String[] selectionArgs = new String [] {email};

        Cursor cursor = db.query(helperDB.TOP_SCORES_TABLE, projection, selection, selectionArgs, null, null, null);

        if (cursor.getCount()==0)
        {
            return false;
        }
        return true;
    }
    /**
     * updateScore
     * עדכון ניקוד המשתמש בנוכחי במסד נתונים
     */
    public void updateScore()
    {
        SQLiteDatabase db=helperDB.getWritableDatabase();
        String[] projection = {helperDB.USER_EMAIL_COL,helperDB.POINTS_COL};
        String selection = helperDB.USER_EMAIL_COL + " = ?";
        String[] selectionArgs = new String [] {email};

        Cursor cursor = db.query(helperDB.TOP_SCORES_TABLE, projection, selection, selectionArgs, null, null, null);

        boolean result = cursor.moveToFirst();
        String col=helperDB.POINTS_COL;
        int index = cursor.getColumnIndexOrThrow(col);
        String pointsDB= cursor.getString(index);

        int numPointsDB=Integer.parseInt(pointsDB);
        if (points>numPointsDB)
        {
            //update only is higher than previous score
            String [] oldData={email};
            String infield=helperDB.USER_EMAIL_COL+"=?";
            ContentValues cv=new ContentValues();
            cv.put(helperDB.POINTS_COL,Integer.toString(points));
            int rowsChanged= db.update(helperDB.TOP_SCORES_TABLE, cv, infield,oldData);
            if (rowsChanged>0)
            {
                Log.d("MARIELA","Updated score");
            }
        }
    }
    /*
    * insertScoreToDBS
     *  פעולה שמטרתה להכניס ניקוד המשחק במסד נתונים
     * במידה וזו פעם ראשונה שהשחקן משחק
     *  */
    public void insertScoreToDBS()
    {
        TopScores top=new TopScores(email, Integer.toString(points));

        ContentValues cv= new ContentValues();
        cv.put(helperDB.USER_EMAIL_COL,top.getEmail());
        cv.put(helperDB.POINTS_COL,top.getMaxScore());
        db=helperDB.getWritableDatabase();
        db.insert(helperDB.TOP_SCORES_TABLE,null,cv);
        db.close();
    }
    /**
     * goToNext
     * פעולה שמטרתה להתקדם לשאלה באה או לסיים את המשחק
     * אם הסתיים המשחק,  הפסקת טיימר, עדכון ניקוד משתמש במסד נתונים
     */
    public void goToNext()
    {
        if (!getRandomQuestion())
        {
            stopTimer();
            if (!playedBefore())
            {
                //game over
                insertScoreToDBS();
            }
            else {
                updateScore();
            }
            Intent intent=new Intent(GameActivity.this, GameOverActivity.class);
            intent.putExtra("points",points);
            intent.putExtra("email",email);
            startActivity(intent);
        }
    }
    /**
     * notifySuccess
     *  פעולה שמטרתה לעדכן את המשתמש בהצלחה בשאלה
     *  מעבר לשאלה הבאה
     *  */
    public void notifySuccess()
    {
        AlertDialog.Builder adbCorrectResponse;
        adbCorrectResponse = new AlertDialog.Builder(GameActivity.this);
        adbCorrectResponse.setTitle("Correct");
        adbCorrectResponse.setMessage("well done");
        adbCorrectResponse.setCancelable(false);
        adbCorrectResponse.setIcon(R.drawable.question_mark_24);
        adbCorrectResponse.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToNext();

            }
        });
        adbCorrectResponse.create().show();
    }
    /**
     * notifyFailure
     *  פעולה שמטרתה לעדכן את המשתמש בכשלון בשאלה
     *  מעבר לשאלה הבאה
     *  */
    public void notifyFailure()
    {
        AlertDialog.Builder adbCorrectResponse;
        adbCorrectResponse = new AlertDialog.Builder(GameActivity.this);
        adbCorrectResponse.setTitle("Incorrect");
        adbCorrectResponse.setMessage("Better luck next time");
        adbCorrectResponse.setCancelable(false);
        adbCorrectResponse.setIcon(R.drawable.question_mark_24);
        adbCorrectResponse.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToNext();
            }
        });
        adbCorrectResponse.create().show();
    }
    /**
     * SelectAnswer
     *  פעולה שמטרתה לטפל בבחירת השחקן
     * בדיקת נכונות התשובה ועדכון ניקוד
     *  */
    public void SelectAnswer (View v) {
        stopTimer();

        if (v.getId() == R.id.fab1) {
            if (current.getCorrectAnswer().equals(current.getPossibleAnswer1())) {
                // correct
                points++;
                notifySuccess();
            } else {
                notifyFailure();
            }

        } else if (v.getId() == R.id.fab2) {
            if (current.getCorrectAnswer().equals(current.getPossibleAnswer2()))
            {
                // correct
                points++;
                notifySuccess();
            }
            else {
                notifyFailure();
            }

        }
        else if (v.getId() == R.id.fab3) {
            if (current.getCorrectAnswer().equals(current.getPossibleAnswer3()))
            {
                // correct
                points++;
                notifySuccess();
            }
            else {
                notifyFailure();
            }

        }
        else if (v.getId() == R.id.fab4) {
            if (current.getCorrectAnswer().equals(current.getPossibleAnswer4()))
            {
                // correct
                points++;
                notifySuccess();
            }
            else {
                notifyFailure();
            }

        }
    }
    /**
     * onCreateOptionsMenu
     *  פעולה שמטרתה להגדיר את התפריטים המוצגים במשחק
     *  */
    @SuppressLint("RestrictedApi")
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        if (menu instanceof MenuBuilder)
        {
            MenuBuilder mb= (MenuBuilder) menu;
            mb.setOptionalIconsVisible(true);
        }
        //   menu.add(0, 100, 0 , "Manual");
        return super.onCreateOptionsMenu(menu);
    }
    /**
     * onOptionsItemSelected
     *  פעולה שמטרתה לקבוע מה יתרחש כאשר תפריטים ייבחרו
     *  */
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId=item.getItemId();
        if (itemId==R.id.mnuSettings)
        {

        }
        else if (itemId==R.id.mnuUserProperties)
        {
            Intent intent=new Intent(GameActivity.this, UserSettingsActivity.class);
            intent.putExtra("email",email);
            startActivity(intent);

        }
        else if (itemId==R.id.mnuMusic)
        {
            Log.d("MARIELA","Music"+play);

            Intent it=new Intent(this, MusicService.class);
            startService(it)  ;
            if (play) {

                Drawable newDrawable = getResources().getDrawable(R.drawable.music_note_24);

                // Set the drawable as the MenuItem's icon
                item.setIcon(newDrawable);

            }
            else {
                Drawable newDrawable = getResources().getDrawable(R.drawable.music_off_24);

                // Set the drawable as the MenuItem's icon
                item.setIcon(newDrawable);

            }
            play=!play;
        }
        else if (itemId==R.id.mnuInstructions)
        {
            Intent intent=new Intent(GameActivity.this, InstructionsActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * startTimer
     *  פעולה שמטרתה לאתחל טיימר למשחק
     *  */
    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Cancel the previous timer if it exists
        }
        missing=MAX_TIME;
        countDownTimer = new CountDownTimer(missing, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isTimerPaused) {
                    missing = millisUntilFinished;
                    tvTimer.setText(Long.toString(missing / 1000));
                }
                else {
                    missing+=1000;
                    tvTimer.setText(Long.toString(missing / 1000));
                }
            }
            @Override
            public void onFinish() {
                if (!isTimerPaused) {
                    tvTimer.setText("Time over!");
                    goToNext();
                }
                else
                {
                    //paused
                    missing=MAX_TIME;
                    countDownTimer.start();

                }

            }
        }.start();
    }
    /**
     * stopTimer
     *  פעולה שמטרתה להפסיק טיימר למשחק
     *  */
    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null; // Set the timer instance to null
        }
    }
    /**
     * pauseTimer
     *   פעולה שמטרתה להפסיק זמנית טיימר למשחק
     *  */
    // Call this method whenever you want to pause the timer (e.g., before opening a dialog)
    private void pauseTimer() {
        isTimerPaused = true;
    }
    /**
     * resumeTimer
     *   פעולה שמטרתה לחזיר  טיימר למשחק
     *  */
    // Call this method to resume the timer after pausing
    private void resumeTimer() {
        isTimerPaused = false;
        startTimer(); // Start or resume the timer
    }
    /**
     * onCreate
     *      פעולה שמאתחלת את אקטיביטי המשחק
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        Log.d("mariela","load questions");
        init();
        //  loadQuestions();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent=getIntent();
        email=intent.getStringExtra("email");
        try{
            ActivityCompat.requestPermissions(this,new String[] {
                    android.Manifest.permission.READ_PHONE_STATE,android.
                    Manifest.permission.READ_PHONE_NUMBERS,
                    android.Manifest.permission.READ_CALL_LOG},100);
            incomingCallReceiver=new IncomingCallReceiver();
            intentFilterCall = new IntentFilter("android.intent.action.PHONE_STATE");

            registerReceiver(incomingCallReceiver,intentFilterCall);
        }
        catch (Exception e)
        {
            Log.d("MARIELA","No permission for recieving call");
        }

            llQuestions.setVisibility(View.INVISIBLE);
            pbReady.setVisibility(View.VISIBLE);
            tvLoading.setVisibility(View.VISIBLE);


            if (MainActivity.isGameReady)
            {
                Log.d("MARIELA","Fast load of questions");
                llQuestions.setVisibility(View.VISIBLE);
                //pbReady.setVisibility(View.INVISIBLE);
                //tvLoading.setVisibility(View.INVISIBLE);
                pbReady.setVisibility(View.GONE);
                tvLoading.setVisibility(View.GONE);
                getRandomQuestion();
            }
            else {
                Log.d("MARIELA","Slow load of questions");
                new DownloadQuestionsTask(this).execute();
                Log.d("MARIELA","Slow load of questions2");

            }

    }
    /**
     * onDestroy
     *      פעולה שדואגת להפסיק לקבל הודעות על שיחות נכנסות בסגירה של המשחק
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(incomingCallReceiver);
    }

}