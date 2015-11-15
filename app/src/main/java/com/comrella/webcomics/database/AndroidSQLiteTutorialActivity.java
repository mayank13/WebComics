/*package com.comrella.webcomics.database;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.comrella.webcomics.R;


public class AndroidSQLiteTutorialActivity extends Activity {
    *//** Called when the activity is first created. *//*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        DatabaseHandler db = new DatabaseHandler(this);
        
        *//**
         * CRUD Operations
         * *//*
        // Inserting Contacts
        Log.d("Insert: ", "Inserting ..");
        db.addFavorite(new Favorite("Ravi"));
        db.addFavorite(new Favorite("Srinivas"));
        db.addFavorite(new Favorite("Tommy"));
        db.addFavorite(new Favorite("Karthik"));
 
        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Favorite> contacts = db.getAllFavorites();       
 
        for (Favorite cn : contacts) {
            String log = "Id: "+cn.getID()+" ,URL: " + cn.getUrl() ;
                // Writing Contacts to log
        Log.d("URL: ", log);
        
        }
    }
}*/