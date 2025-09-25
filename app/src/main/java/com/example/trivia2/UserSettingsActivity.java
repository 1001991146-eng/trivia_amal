package com.example.trivia2;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class UserSettingsActivity extends AppCompatActivity {

    // language properties
    public Locale locale;
    public int lang = 0;
    public int speech = 0;

    public TextToSpeech textToSpeech;
    public ExtendedFloatingActionButton fabDeleteEdit, fabOkEdit;
    public TextInputEditText etFirstEdit,etLastEdit, etEmailEdit,etPhoneEdit,etPasswordEdit;
    String email;

    private FirebaseDatabase database;
    private DatabaseReference userPropertiesRef; // A reference to the root or a specific path
    /**
     * init
     * הפעולה מטרתה להתחל את כל קישור לכל הרכיבים על מסך
     * ולייצר קשר למסד הנתונים על ידי יצירת עצם מסוג
     * HelperDb
     */
    public void init()
    {
        etFirstEdit=findViewById(R.id.etFirstEdit);
        etLastEdit=findViewById(R.id.etLastEdit);
        etEmailEdit=findViewById(R.id.etEmailEdit);
        etPhoneEdit=findViewById(R.id.etPhoneEdit);
        etPasswordEdit=findViewById(R.id.etPasswordEdit);
        fabOkEdit=findViewById(R.id.fabOkEdit);
        fabDeleteEdit=findViewById(R.id.fabDeleteEdit);
        email="";
        database = FirebaseDatabase.getInstance();
        userPropertiesRef = database.getReference("UserProperties");
    }
    /**
     * initLanguage
     * הפעולה מטרתה להתחל את השפה של האפליקציה לצורך TTS
     * */
    public  void initLanguage(Context context, String language) {
        locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
    /**
     * read
     * הפעולה מטרתה להקריא הודעה למשתמש באמצעות TTS
     * */
    public void read(String message)
    {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    lang = textToSpeech.setLanguage(locale);
                    speech = textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, null);
                }
            }
        });

    }
    /**
     * loadCurrentDataFB
     * הפעולה שמטרתה להביא ממסד הנצונים את פרטי המשתמש המחובר להצגה במסך עריכת פרטי משתמש
     * */
    public  void loadCurrentDataFB()
    {
        Log.d("MARIELA","loadCurrentDataFB");
        // הוספת מאזין לקריאת הנתונים
        userPropertiesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MARIELA","onDataChange "+dataSnapshot.getKey());

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("MARIELA", "Child snapshot key: " + snapshot.getKey());
                    UserProperties user = snapshot.getValue(UserProperties.class);
                    if (user != null && user.getEmail().equals(email)) {
                        Log.d("MARIELA", "Retrieved user: " + user.toString());
                        etFirstEdit.setText(user.getFirstName());
                        etLastEdit.setText(user.getLastName());
                        etEmailEdit.setText(user.getEmail());
                        etPhoneEdit.setText(user.getPhone());
                        etPasswordEdit.setText(user.getPassword());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // טיפול בשגיאה
                Log.e("FirebaseRead", "Failed to read value.", databaseError.toException());
                etFirstEdit.setText("");
                etLastEdit.setText("");
                etEmailEdit.setText(email);
                etPhoneEdit.setText("");
                etPasswordEdit.setText("");
            }
        });

    }
    /**
     * verifyData
     * הפעולה שמטרתה לבדוק שפרטי הנתונים שנערכו תקינים
     * */
    public boolean verifyData()
    {
        String first=etFirstEdit.getText().toString();
        String last=etLastEdit.getText().toString();
        String email=etEmailEdit.getText().toString();
        String phone=etPhoneEdit.getText().toString();
        String password=etPasswordEdit.getText().toString();
        if (first.isEmpty())
        {
            etFirstEdit.setError("First name is empty");
            return false;
        }
        if (last.isEmpty())
        {
            etLastEdit.setError("Last name is empty");
            return false;
        }
        if (phone.isEmpty())
        {
            etPhoneEdit.setError("Phone is empty");
            return false;
        }
        // Check if any of the fields are empty
        if ( password.isEmpty()) {
            etPasswordEdit.setError("Pasword is empty");
            return false; // At least one field is empty
        }
        // Validate phone number format (you can customize the format validation as needed)
        String phonePattern = "^[0-9]{10}$";
        if (!phone.matches(phonePattern)) {
            etPhoneEdit.setError("Wrong phone format (Must have only 10 digits)");
            return false; // Invalid phone number format (assuming a 10-digit format)
        }
        // Add more password validation rules as needed
        if (password.length() < 6) {
            etPasswordEdit.setError("Password must be at least 6 characters long");
            return false; // Password is too short
        }
        return  true;
    }
    /**
     * updateDataFB
     * הפעולה שמטרתה לעדכן את מסד הנתונים עם פרטי המשתמש המעודכן
     * */
    public  void updateDataFB()
    {
        UserProperties userProperties=new UserProperties(etFirstEdit.getText().toString(),
                etLastEdit.getText().toString(),
                etEmailEdit.getText().toString(),
                etPhoneEdit.getText().toString(),
                etPasswordEdit.getText().toString());
        // שלב 1: איתור המשתמש במסד הנתונים לפי אימייל
        userPropertiesRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // נמצא משתמש עם האימייל הנתון
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                // שלב 2: עדכון הנתונים של המשתמש
                                snapshot.getRef().setValue(userProperties)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("MARIELA", "User properties for " + email + " updated successfully.");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("MARIELA", "Failed to update user properties for " + email, e);
                                        });
                            }
                        } else {
                            // לא נמצא משתמש עם האימייל הנתון
                            Log.d("MARIELA", "User with email " + email + " not found for update.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // טיפול בשגיאות
                        Log.e("MARIELA", "Database query cancelled: " + databaseError.getMessage());
                    }
                });

    }

    /**
     * deleteUserFB
     * הפעולה שמטרתה למחוק משתמש מחובר
     * */
    public void deleteUserFB()
    {
        // השתמש ב-addListenerForSingleValueEvent כדי לקרוא את הנתונים פעם אחת
        userPropertiesRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // נמצא משתמש עם האימייל הנתון
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                // ה-snapshot הנוכחי הוא הצומת של המשתמש
                                // כעת יש לנו התייחסות ישירה לצומת

                                // שלב 2: מחיקת הנתונים
                                snapshot.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("MARIELA", "User with email " + email + " deleted successfully.");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("MARIELA", "Failed to delete user with email " + email, e);
                                        });
                            }
                        } else {
                            // לא נמצא משתמש עם האימייל הנתון
                            Log.d("MARIELA", "User with email " + email + " not found.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // טיפול בשגיאות
                        Log.e("MARIELA", "Database query cancelled: " + databaseError.getMessage());
                    }
                });
    }
    /**
     * onCreate
     *      פעולה שמאתחלת את אקטיביטי עריכת נתוני משתמש
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        Intent intent=getIntent();
        email=intent.getStringExtra("email");
        //loadCurrentData();
        loadCurrentDataFB();
        fabOkEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // verify correct data
                if (verifyData())
                {
                    // save data to dbs
                    updateDataFB();
                }

            }
        });
        fabDeleteEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message="Are you sure you want to delete user?";
                read(message);

                AlertDialog.Builder adbCorrectResponse;
                adbCorrectResponse = new AlertDialog.Builder(UserSettingsActivity.this);
                adbCorrectResponse.setTitle("Delete User");
                adbCorrectResponse.setMessage(message);
                adbCorrectResponse.setCancelable(false);
                adbCorrectResponse.setIcon(R.drawable.question_mark_24);
                adbCorrectResponse.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // delete user
                        deleteUserFB();
                        // back to login
                        Intent intent=new Intent(UserSettingsActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
                adbCorrectResponse.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                adbCorrectResponse.create().show();
            }
        });
    }
}

