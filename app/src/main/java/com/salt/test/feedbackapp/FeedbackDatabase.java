package com.salt.test.feedbackapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.TreeMap;


/**
 * Created by 007ma on 12/30/2017.
 */

public class FeedbackDatabase extends SQLiteOpenHelper {


    public FeedbackDatabase(Context p_context){

        super(p_context, "FEEDBACKDB2.db", null, 2);
        //System.out.println("Database created....");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String createQueryLocal = "CREATE TABLE IF NOT EXISTS FEEDBACKDATA(TIMESTAMP TEXT NOT NULL, NAME TEXT NOT NULL, AGE INTEGER NOT NULL, GENDER TEXT NOT NULL, PLACE TEXT NOT NULL, CONTACT TEXT NOT NULL, EMAIL TEXT , Q3 INTEGER NOT NULL, Q4 INTEGER NOT NULL, Q5 INTEGER NOT NULL, Q6 INTEGER NOT NULL, Q7 INTEGER NOT NULL, COMMENTS TEXT);";
        sqLiteDatabase.execSQL(createQueryLocal);


        final String createQueryFireBase = "CREATE TABLE IF NOT EXISTS FEEDBACKDATAFB(TIMESTAMP TEXT NOT NULL, NAME TEXT NOT NULL, AGE INTEGER NOT NULL, GENDER TEXT NOT NULL, PLACE TEXT NOT NULL, CONTACT TEXT NOT NULL, EMAIL TEXT , Q3 INTEGER NOT NULL, Q4 INTEGER NOT NULL, Q5 INTEGER NOT NULL, Q6 INTEGER NOT NULL, Q7 INTEGER NOT NULL, COMMENTS TEXT);";
        sqLiteDatabase.execSQL(createQueryFireBase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
       // sqLiteDatabase.execSQL("DROP TABLE FEEDBACKDATA");
      //  sqLiteDatabase.execSQL("DROP TABLE FEEDBACKDATAFB");
    }

    public boolean insertFeedback(String tableName,String timeStamp, String name, int age,String gender,String place,String contact,String email,int q3,int q4, int q5, int q6,int q7, String comments) {

        try{
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("TIMESTAMP", timeStamp);
            contentValues.put("NAME", name);
            contentValues.put("AGE", age);
            contentValues.put("GENDER", gender);
            contentValues.put("PLACE", place);
            contentValues.put("CONTACT",contact);
            contentValues.put("EMAIL",email);
            contentValues.put("Q3",q3);
            contentValues.put("Q4",q4);
            contentValues.put("Q5",q5);
            contentValues.put("Q6",q6);
            contentValues.put("Q7",q7);
            contentValues.put("COMMENTS",comments);
            long returnValue = db.insert(tableName, null, contentValues);
            if(returnValue !=-1){
               //Comments Log.d("FeedbackDatabase","Database insert success  at "+returnValue);
                return true;
            }else {
                //Comments Log.d("FeedbackDatabase","Database insert failed");
                db.close();
                return false;
            }

        }catch (Exception ex){
            ex.printStackTrace();
            Log.d("FeedbackDatabase","Database insert failed due to "+ex.toString());
            return false;
        }
     }

     public Cursor getFeedbackData(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from "+tableName,null);
        return  res;
     }

    public  long getDataCount(String tableName){
        SQLiteDatabase db = this.getWritableDatabase();

        //Running query to retrieve total records from database
        Cursor res = db.rawQuery("select count(TIMESTAMP) from "+tableName,null);
        if(res.getCount()<=0){
            return 0;
        }else{
            res.moveToFirst();
            return  res.getLong(0);
        }

    }

     public TreeMap getColumnCount(String tableName,String columnName) throws Exception{
         SQLiteDatabase db = this.getWritableDatabase();

         TreeMap map = new TreeMap();

         Cursor res=null;
         for(int i=1;i<=5;i++){
             res = db.rawQuery("select count("+columnName+") from "+tableName+" where "+columnName+"="+i,null);
             res.moveToFirst();
             Log.d("FeedbackDatabase","in getColumnCount()-----> For column "+i+" total count is "+res.getInt(0));
             map.put(i,res.getInt(0));
         }
         return map;
     }



    public  long getAgeRange(String tableName,int startAge, int endAge){
        SQLiteDatabase db = this.getWritableDatabase();

        //Running query to retrieve total records from database
        Cursor res = db.rawQuery("select count(AGE) from "+tableName+" where AGE >="+startAge +" AND AGE <="+endAge ,null);
        if(res.getCount()<=0){
            return 0;
        }else{
            res.moveToFirst();
            return  res.getLong(0);
        }

    }

    public  long getGenderCount(String tableName,String gender){
        SQLiteDatabase db = this.getWritableDatabase();

        //Running query to retrieve total records from database
        Cursor res = db.rawQuery("select count(TIMESTAMP) from "+tableName+" where Gender = '"+gender+"'" ,null);
        if(res.getCount()<=0){
            return 0;
        }else{
            res.moveToFirst();
            return  res.getLong(0);
        }

    }

    public void deleteTableData(String tablename){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ tablename);

    }

}
