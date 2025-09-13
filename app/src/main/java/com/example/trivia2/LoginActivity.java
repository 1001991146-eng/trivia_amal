package com.example.trivia2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    public HelperDB helperDB;
    public SQLiteDatabase db;

    public ExtendedFloatingActionButton fabRegister, fabLogin;
    public CheckBox cbPersonal;
    public TextInputEditText etEmail,etPassword;


    public void init()
    {
        fabRegister=findViewById(R.id.fabRegister);
        fabLogin=findViewById(R.id.fabLogin);
        cbPersonal=findViewById(R.id.cbPersonal);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        helperDB=new HelperDB(this);
    }
    public boolean verifyUserExist()
    {
        SQLiteDatabase db = helperDB.getReadableDatabase();

        String[] projection = {helperDB.EMAIL_COL,helperDB.PASSWORD_COL};
        String selection = helperDB.EMAIL_COL + " = ?";
        String[] selectionArgs = new String [] {etEmail.getText().toString()};

        Cursor cursor = db.query(helperDB.USERS_TABLE, projection, selection, selectionArgs, null, null, null);

        if (cursor.getCount()==0)
        {
            db.close();// no user by that name
            etEmail.setError("No user by that name");
            return false;
        }
        boolean result = cursor.moveToFirst();
        String col=helperDB.PASSWORD_COL;
        int index = cursor.getColumnIndexOrThrow(col);
        String passwordDB= cursor.getString(index);

        if (passwordDB.equals(etPassword.getText().toString()))
        {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        etPassword.setError("Wrong password");
        return false;
    }
    public void loadLastLoggedInUserData()
    {
        SharedPreferences sp=
                getSharedPreferences("trivia",MODE_PRIVATE);
        String email = sp.getString("email", ""); // Provide a default value in case the key is not found
        String password = sp.getString("password", ""); // Provide a default value in case the key is not found
        etEmail.setText(email);
        etPassword.setText(password);


    }
    public void saveLogedInUserInSharedPreferences()
    {
        SharedPreferences sp=
                getSharedPreferences("trivia",MODE_PRIVATE);
        SharedPreferences.Editor editor= sp.edit();
        editor.putString("email",etEmail.getText().toString());
        editor.putString("password",etPassword.getText().toString());
        editor.commit();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        init();
        loadLastLoggedInUserData();
        fabRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
            });
        fabLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //perform login
                    if (verifyUserExist()) {
                        if (cbPersonal.isChecked())
                        {
                            // save email and password in shared prefferences
                            saveLogedInUserInSharedPreferences();
                        }
                        Intent intent = new Intent(LoginActivity.this, GameActivity.class);
                        intent.putExtra("email",etEmail.getText().toString());
                        startActivity(intent);
                    }
                }
            });
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
    }
}