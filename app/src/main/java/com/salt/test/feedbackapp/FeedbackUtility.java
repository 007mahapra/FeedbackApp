package com.salt.test.feedbackapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Created by 007ma on 1/1/2018.
 */

public class FeedbackUtility {


    public static void getCSVValuesInList(String valuesInCSV){
        List list = new ArrayList();


        StringTokenizer token = new StringTokenizer( valuesInCSV,",");
        while (token.hasMoreTokens()){
            list.add(token.nextToken());
        }
        System.out.println("List is "+list);

    }



    public static void writeFileToDisk(String columnValue){

        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String currentDate = sdf.format(new Date()).toString();
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = currentDate+"_FeedbackData.csv";
        String headerColumn = "TIMESTAMP,NAME,AGE,GENDER,PLACE,CONTACT,EMAIL,Q3,Q4,Q5,Q6,Q7,COMMENTS";
        String filePath = baseDir + File.separator + fileName;
        Log.d("LOG","Path to write values.."+filePath);
        File file = new File(filePath);
        FileWriter fileWriter = null;
        try {
             fileWriter = new FileWriter(file,true);
            if(!file.exists()){

                try {
                    file.createNewFile();
                    System.out.println("Writing with header..");
                    fileWriter.append(headerColumn);
                    fileWriter.append("\n");
                    fileWriter.append(columnValue).append("\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Log.d("INFO","Writing without header..");
                fileWriter.append(columnValue).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try{
                if(fileWriter!=null)
                fileWriter.close();
            }catch (IOException ioEx){
                ioEx.printStackTrace();
            }

        }

    }

    public static String getfeedbackIdentifier(int key){

        Map map = new HashMap();
        map.put(1,"1: Needs Improvement");
        map.put(2,"2: Good");
        map.put(3,"3: Very Good");
        map.put(4,"4: Excellent");
        map.put(5,"5: Outstanding");

        return map.get(key).toString();
    }



    public  static boolean hasPermissions(Context context, String... permissions)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                Log.d("Info","Requesting Permission.. in hasPermission() -->"+permission);
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("hasPermissions() log","Permission "+permission+" is not granted");
                    return false;
                }
            }
        }
        return true;
    }


    public static SortedMap setKeyValuePairFeedback(FeedbackDTO dto){

        TreeMap map= new TreeMap<String,String>();
        map.put(R.string.TimeStamp,dto.getTimestamp());
        map.put(R.string.Name,dto.getName());
        map.put(R.string.Age,String.valueOf(dto.getAge()));
        map.put(R.string.Gender,dto.getGender());
        map.put(R.string.Place,dto.getPlace());
        map.put(R.string.Contact,dto.getContact());
        map.put(R.string.Email,dto.getEmail());
        map.put(R.string.q3,getfeedbackIdentifier(dto.getQ3()));
        map.put(R.string.q4,getfeedbackIdentifier(dto.getQ4()));
        map.put(R.string.q5,getfeedbackIdentifier(dto.getQ5()));
        map.put(R.string.q6,getfeedbackIdentifier(dto.getQ6()));
        map.put(R.string.q7,getfeedbackIdentifier(dto.getQ7()));
        map.put(R.string.Comments,dto.getComments());

        Log.d("Log","Values in map "+map);

        return map;
    }
    public static void main(String[] args) {
        FeedbackUtility.getCSVValuesInList("abc,bbc,def,gef,ight");
    }

}
