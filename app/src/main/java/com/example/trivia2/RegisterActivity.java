package com.example.trivia2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    public HelperDB helperDB;
    public SQLiteDatabase db;

    public ExtendedFloatingActionButton fabCancelReg, fabOKReg;
    public TextInputEditText etEmailReg,etLastReg,etFirstReg,etPhone,etPasswordReg;
    /**
     * init
     * הפעולה מטרתה להתחל את כל קישור לכל הרכיבים על מסך
     * ולייצר קשר למסד הנתונים על ידי יצירת עצם מסוג
     * HelperDb
     */
    public void init()
    {
        fabCancelReg=findViewById(R.id.fabCancelReg);
        fabOKReg=findViewById(R.id.fabOkReg);
        etLastReg=findViewById(R.id.etLastReg);
        etEmailReg=findViewById(R.id.etEmailReg);
        etFirstReg=findViewById(R.id.etFirstReg);
        etPhone=findViewById(R.id.etPhone);
        etPasswordReg=findViewById(R.id.etPasswordReg);
        helperDB=new HelperDB(this);
    }
    /**
     * verifyData
     * הפעולה בודקת אם פרטי ההרשמה תקינים
     */
    public boolean verifyData()
    {
        String first=etFirstReg.getText().toString();
        String last=etLastReg.getText().toString();
        String email=etEmailReg.getText().toString();
        String phone=etPhone.getText().toString();
        String password=etPasswordReg.getText().toString();
        if (first.isEmpty())
        {
            etFirstReg.setError("First name is empty");
            return false;
        }
        if (last.isEmpty())
        {
            etLastReg.setError("Last name is empty");
            return false;
        }
        if (phone.isEmpty())
        {
            etPhone.setError("Phone is empty");
            return false;
        }
        if (email.isEmpty())
        {
            etEmailReg.setError("Email is empty");
            return false;
        }
        // Check if any of the fields are empty
        if ( password.isEmpty()) {
            etPasswordReg.setError("Pasword is empty");
            return false; // At least one field is empty
        }
        // Validate email format using a simple regex pattern
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        if (!email.matches(emailPattern)) {
            etEmailReg.setError("Wrong email format");
            return false; // Invalid email format
        }
        // Validate phone number format (you can customize the format validation as needed)
        String phonePattern = "^[0-9]{10}$";
        if (!phone.matches(phonePattern)) {
            etPhone.setError("Wrong phone format (Must have only 10 digits)");
            return false; // Invalid phone number format (assuming a 10-digit format)
        }
        // Add more password validation rules as needed
        if (password.length() < 6) {
            etPasswordReg.setError("Password must be at least 6 characters long");
            return false; // Password is too short
        }
        return  true;
    }
    /**
     * insertNewUserToDBS
     * הפעולה שומרת את פרטי ההרשמה של המשתמש במסד נתונים
     */
    public void insertNewUserToDBS()
    {

        String first=etFirstReg.getText().toString();
        String last=etLastReg.getText().toString();
        String email=etEmailReg.getText().toString();
        String phone=etPhone.getText().toString();
        String password=etPasswordReg.getText().toString();
        UserProperties user=new UserProperties(first, last, email,  phone, password);

        ContentValues cv= new ContentValues();
        cv.put(helperDB.FIRST_NAME_COL,user.getFirstName());
        cv.put(helperDB.LAST_NAME_COL,user.getLastName());
        cv.put(helperDB.EMAIL_COL,user.getEmail());
        cv.put(helperDB.PHONE_COL,user.getPhone());
        cv.put(helperDB.PASSWORD_COL,user.getPassword());
        db=helperDB.getWritableDatabase();
        db.insert(helperDB.USERS_TABLE,null,cv);
        db.close();
    }
    /**
     * onCreate
     *      פעולה שמאתחלת את אקטיביטי ההרשמה
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        init();
        fabOKReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // verify data
                    if (verifyData()) {
                        // insert user to dbs
                        insertNewUserToDBS();
                        // goto game
                        Intent intent = new Intent(RegisterActivity.this, GameActivity.class);
                        intent.putExtra("email",etEmailReg.getText().toString());
                        startActivity(intent);
                    }
                }
            });
        fabCancelReg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // go back to login
                    Intent intent=new Intent( RegisterActivity.this, LoginActivity.class);
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