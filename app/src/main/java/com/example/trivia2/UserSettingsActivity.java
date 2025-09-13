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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class UserSettingsActivity extends AppCompatActivity {

    public TextToSpeech textToSpeech;
    public Locale locale;
    public int lang = 0;
    public int speech = 0;



    public HelperDB helperDB;
    public SQLiteDatabase db;

    public ExtendedFloatingActionButton fabDeleteEdit, fabOkEdit;
    public TextInputEditText etFirstEdit,etLastEdit, etEmailEdit,etPhoneEdit,etPasswordEdit;
    String email;

    public  void initLanguage(Context context, String language) {
        locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

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
        helperDB=new HelperDB(this);
    }
    public void loadCurrentData()
    {
        SQLiteDatabase db = helperDB.getReadableDatabase();

        String[] projection = {helperDB.FIRST_NAME_COL, helperDB.LAST_NAME_COL ,helperDB.EMAIL_COL,helperDB.PHONE_COL ,helperDB.PASSWORD_COL};
        String selection = helperDB.EMAIL_COL + " = ?";
        String[] selectionArgs = new String [] {email};

        Cursor cursor = db.query(helperDB.USERS_TABLE, projection, selection, selectionArgs, null, null, null);

        if (cursor.getCount()==0)
        {
            cursor.close();
            db.close();// no user by that name
            Log.d("MARIELA","Failed to load user data");
        }
        else {
            boolean result = cursor.moveToFirst();

            String col = helperDB.FIRST_NAME_COL;
            int index = cursor.getColumnIndexOrThrow(col);
            String value = cursor.getString(index);
            etFirstEdit.setText(value);

            col = helperDB.LAST_NAME_COL;
            index = cursor.getColumnIndexOrThrow(col);
            value = cursor.getString(index);
            etLastEdit.setText(value);

            col = helperDB.EMAIL_COL;
            index = cursor.getColumnIndexOrThrow(col);
            value = cursor.getString(index);
            etEmailEdit.setText(value);

            col = helperDB.PHONE_COL;
            index = cursor.getColumnIndexOrThrow(col);
            value = cursor.getString(index);
            etPhoneEdit.setText(value);

            col = helperDB.PASSWORD_COL;
            index = cursor.getColumnIndexOrThrow(col);
            value = cursor.getString(index);
            etPasswordEdit.setText(value);

            cursor.close();
            db.close();
        }
    }
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
    public void updateData()
    {
        SQLiteDatabase db=helperDB.getWritableDatabase();

        String [] oldData={email};
        String infield=helperDB.EMAIL_COL+"=?";
        ContentValues cv=new ContentValues();

        cv.put(helperDB.FIRST_NAME_COL,etFirstEdit.getText().toString());
        cv.put(helperDB.LAST_NAME_COL,etLastEdit.getText().toString());
        cv.put(helperDB.PHONE_COL,etPhoneEdit.getText().toString());
        cv.put(helperDB.PASSWORD_COL,etPasswordEdit.getText().toString());


        int rowsChanged= db.update(helperDB.USERS_TABLE, cv, infield,oldData);
        if (rowsChanged>0)
        {
            Log.d("MARIELA","Updated user");
            AlertDialog.Builder adbCorrectResponse;
            adbCorrectResponse = new AlertDialog.Builder(this);
            adbCorrectResponse.setTitle("User Settings");
            adbCorrectResponse.setMessage("User updated");
            adbCorrectResponse.setCancelable(false);
            adbCorrectResponse.setIcon(R.drawable.info_24);
            adbCorrectResponse.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            adbCorrectResponse.create().show();
        }
    }
    public void deleteUser()
    {
        db=helperDB.getWritableDatabase();
        String where=helperDB.EMAIL_COL+"=?";
        String [] args=new String [] {email};
        db.delete(helperDB.USERS_TABLE,where, args);

        where=helperDB.USER_EMAIL_COL+"=?";
        args=new String [] {email};
        db.delete(helperDB.TOP_SCORES_TABLE,where, args);
        db.close();

    }
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
        loadCurrentData();
        fabOkEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyData())
                {
                    updateData();
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
                        deleteUser();

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

