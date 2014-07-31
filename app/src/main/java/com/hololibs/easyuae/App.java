package com.hololibs.easyuae;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import se.emilsjolander.sprinkles.Migration;
import se.emilsjolander.sprinkles.Sprinkles;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Sprinkles sprinkles = Sprinkles.init(getApplicationContext());

        sprinkles.addMigration(new Migration() {
            @Override
            protected void doMigration(SQLiteDatabase sqLiteDatabase) {

                sqLiteDatabase.execSQL("CREATE TABLE hotlines (" +
                        "hotline_id INTEGER PRIMARY KEY," +
                        "active INTEGER," +
                        "group_id INTEGER," +
                        "emirate_id INTEGER," +
                        "hotline_name TEXT," +
                        "hotline_number TEXT" +
                        ")");

                sqLiteDatabase.execSQL("CREATE TABLE emirates (" +
                        "emirate_id INTEGER PRIMARY KEY," +
                        "name TEXT," +
                        "shortform TEXT" +
                        ")");

                sqLiteDatabase.execSQL("CREATE TABLE groups (" +
                        "group_id INTEGER PRIMARY KEY," +
                        "active INTEGER," +
                        "group_name TEXT" +
                        ")");


            }
        });


    }

}