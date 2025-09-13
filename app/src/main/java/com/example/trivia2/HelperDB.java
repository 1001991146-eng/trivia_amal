package com.example.trivia2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class HelperDB  extends SQLiteOpenHelper {
    // name of dbs file
    public static final String DB_FILE="trivia.db";

    // name of table
    public static final String USERS_TABLE="UsersTbl";
    // name of fields in table
    public static final String FIRST_NAME_COL="first";
    public static final String LAST_NAME_COL="last";
    public static final String EMAIL_COL="email";
    public static final String PHONE_COL="phone";
    public static final String PASSWORD_COL ="password";

    // name of table
    public static final String TOP_SCORES_TABLE="TopScoresTbl";
    // name of fields in table
    public static final String USER_EMAIL_COL ="user";
    public static final String POINTS_COL="points";

    /**
     * HelperDB
     *      פעולה שעצם מסוג קישןר ךמסד נתונים
     *      בשם DB_FILE
     */
    public HelperDB(@Nullable Context context) {
        super(context, DB_FILE, null, 1);
    }

    /**
     * onCreate
     *      פעולה שיוצרת את טבלאות במידה וטרם נבנו
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table
        String st="CREATE TABLE IF NOT EXISTS "+USERS_TABLE;
        st+=" ( "+FIRST_NAME_COL+" TEXT, ";
        st+= LAST_NAME_COL+" TEXT, ";
        st+= EMAIL_COL+" TEXT, ";
        st+= PHONE_COL+" TEXT, ";
        st+= PASSWORD_COL+" TEXT); ";
        db.execSQL(st);

        // create table
        st="CREATE TABLE IF NOT EXISTS "+TOP_SCORES_TABLE;
        st+=" ( "+USER_EMAIL_COL+" TEXT, ";
        st+= POINTS_COL+" TEXT); ";
        db.execSQL(st);
    }
    /**
     * onUpgrade
     *      פעולה שמעדכנת טבלאות קיימות
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

