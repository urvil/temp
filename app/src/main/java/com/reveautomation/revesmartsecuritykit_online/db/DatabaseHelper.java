package com.reveautomation.revesmartsecuritykit_online.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.reveautomation.revesmartsecuritykit_online.model.MyLocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * Created by Durgesh Patel on 15-4-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase myDataBase;
    private final Context myContext;
    public static String DATABASE_PATH;

    public final static String DATABASE_NAME = "sensorkit.sqlite";

    //Number and Alphabet Table
    public final static String tbl_LOCKS = "devices";

    private final static int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        DATABASE_PATH = myContext.getDatabasePath(DATABASE_NAME).toString();
    }

    public String currentDatabaseFilePath() {
        return DATABASE_PATH;
    }

    public void createDatabase() throws IOException {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            this.getReadableDatabase();
            try {
                this.close();
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            String myPath = DATABASE_PATH;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return checkDB;
    }

    private void copyDataBase() throws IOException {

        String outFileName = DATABASE_PATH;// + DATABASE_NAME;


        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    public void openDatabase() throws SQLException {

        String myPath = DATABASE_PATH;// + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    public void closeDataBase() throws SQLException {
        //Log.i("DataBase", "Close");
        myDataBase.close();
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //delete database
    public void db_delete() {
        Log.e("Database delete", "DATABASE_NAME : " + DATABASE_NAME);
        File file = new File(DATABASE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    public ArrayList<MyLocks> getMyLocks() {
        ArrayList<MyLocks> arrayList = new ArrayList<>();
        openDatabase();
        try {
            String qry = "Select * from " + tbl_LOCKS;

            Cursor c = myDataBase.rawQuery(qry, null);
            if (c != null && c.getCount() > 0) {
                int n = c.getCount();
                for (int i = 0; i < n; i++) {
                    c.moveToPosition(i);
                    MyLocks aa = new MyLocks();
                    aa.id = c.getInt(0);
                    aa.lockName = c.getString(1);
                    aa.lockMacAddress = c.getString(2);
                    aa.type = c.getInt(3);
                    aa.Zone_list = c.getString(4);
                    arrayList.add(aa);
                }
                c.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        closeDataBase();
        SQLiteDatabase.releaseMemory();
        return arrayList;
    }

    public int insertLockData(MyLocks myLocks) {
        int id = -1;
        openDatabase();
        try {
//            CREATE TABLE "locks" ("id" INTEGER PRIMARY KEY  NOT NULL , "name" TEXT, "mac_address" TEXT)
            ContentValues values = new ContentValues();
            values.put("name", myLocks.lockName);
            values.put("mac_address", myLocks.lockMacAddress);
            values.put("type",myLocks.type);
            values.put("zone_list",myLocks.Zone_list);
            id =  (int) myDataBase.insert(tbl_LOCKS, null, values);
            Log.i("Database", "insertLock id : "+ id);
        } catch (Exception e) {
            e.printStackTrace();
            id = -1;
        }
        closeDataBase();
        SQLiteDatabase.releaseMemory();
        return id;
    }

    public String selectLockAddressFromName(String lockName)
    {
        String macAddress = "null";
        openDatabase();
        Cursor c = myDataBase.rawQuery("SELECT mac_address FROM "+tbl_LOCKS + " WHERE name = \"" + lockName + "\"",null);
        if(c!= null)
        {
            if(c.moveToFirst())
            {
                do
                {
                    macAddress = c.getString(c.getColumnIndex("mac_address"));
                }
                while(c.moveToNext());
            }
        }
        c.close();
        closeDataBase();
        SQLiteDatabase.releaseMemory();
        return macAddress;
    }


    public boolean deleteSensorFromId(int ids)
    {
        openDatabase();
        try {
            myDataBase.execSQL("delete from "+tbl_LOCKS+" where id ="+ids);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        closeDataBase();
        SQLiteDatabase.releaseMemory();
        return true;
    }

}

