package com.salt.test.feedbackapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FireBaseGraph extends AppCompatActivity {


    private ScrollView scrollPie;
    private PieChart pieChart1,pieChart2,pieChart3,pieChart4,pieChart5;
    private DatabaseReference firebaseDbRef;
    private Button btnLoadFBData,btnAnalyze,btnDeleteData;

    private TextView tvLoadSuccess,tvGendernAgeGroup,tvPiechart1FB,tvPiechart2FB,tvPiechart3FB,tvPiechart4FB,tvPiechart5FB;
    private FeedbackDatabase feedbackDatabase;

    private final String firebaseSQLIteDBName= "FEEDBACKDATAFB";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base_graph);

        //comments  Log.d(getLocalClassName(),"Initialising database.... ");
        firebaseDbRef = FirebaseDatabase.getInstance().getReference("Feedback2019");
        feedbackDatabase = new FeedbackDatabase(this);
        //comments    Log.d(getLocalClassName(),"Database initialized .... ");

        resolveViews();
        runListeners();

    }

    public void resolveViews () {

        //ScrollViews
        scrollPie =(ScrollView) findViewById(R.id.fbScrollGraphicFB);

        //Buttons
        btnAnalyze = (Button)findViewById(R.id.btnGraphAnalyzeFB) ;
        btnLoadFBData = (Button)findViewById(R.id.loadFBData);
        btnDeleteData = (Button)findViewById(R.id.btnCleanFBData);


        //TextView
        tvLoadSuccess = (TextView)findViewById(R.id.tvLoadSuccess);
        tvGendernAgeGroup = (TextView)findViewById(R.id.tvGendernAgeGroupFB);
        tvPiechart1FB = (TextView)findViewById(R.id.tvPiechart1FB);
        tvPiechart2FB = (TextView)findViewById(R.id.tvPiechart2FB);
        tvPiechart3FB = (TextView)findViewById(R.id.tvPiechart3FB);
        tvPiechart4FB = (TextView)findViewById(R.id.tvPiechart4FB);
        tvPiechart5FB = (TextView)findViewById(R.id.tvPiechart5FB);

        //Pie Charts
        pieChart1 =(PieChart)findViewById(R.id.fbPieChart1);
        pieChart2 =(PieChart)findViewById(R.id.fbPieChart2);
        pieChart3 =(PieChart)findViewById(R.id.fbPieChart3);
        pieChart4 =(PieChart)findViewById(R.id.fbPieChart4);
        pieChart5 =(PieChart)findViewById(R.id.fbPieChart5);
    }

    @Override
    public void onBackPressed() {
        startInsightsActivity(1);
    }
    private void startInsightsActivity(int index) {
        Intent intent = new Intent(this, InsightsActivity.class);
        intent.putExtra("ACTIONCODE", index);
        startActivity(intent);
    }

    public void runListeners(){

        btnLoadFBData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseDbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        boolean successFlag = false;
                        // This method is called once with the initial value and again whenever data at this location is updated.
                        //https://github.com/mitchtabian/Firebase-Read-Database

                        if (retrieveDataFromFirebase(dataSnapshot)!=null){
                            //comments      Log.d(getLocalClassName(),"Retrieving Firebase data from Google Firebase..");

                            successFlag= writeFirebaseDataInSQLite(retrieveDataFromFirebase(dataSnapshot));
                        }

                        //comments  Log.d(getLocalClassName(),"Success flag is "+successFlag);

                        if(successFlag){
                            tvLoadSuccess.setText("Data Load Successful");
                            tvLoadSuccess.setTextColor(getResources().getColor(R.color.submitColor));

                        }else {

                            tvLoadSuccess.setText("Data Load Unsuccessful");
                            tvLoadSuccess.setTextColor(getResources().getColor(R.color.errorColor));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(feedbackDatabase.getDataCount(firebaseSQLIteDBName)>0){

                   try{
                        showGenderAndAgeGroup();
                        tvPiechart1FB.setText(getResources().getString(R.string.q3));
                        drawPieChartFromDB(firebaseSQLIteDBName,"Q3",pieChart1);

                        tvPiechart2FB.setText(getResources().getString(R.string.q4));
                        drawPieChartFromDB(firebaseSQLIteDBName,"Q4",pieChart2);

                        tvPiechart3FB.setText(getResources().getString(R.string.q5));
                        drawPieChartFromDB(firebaseSQLIteDBName,"Q5",pieChart3);

                        tvPiechart4FB.setText(getResources().getString(R.string.q6));
                        drawPieChartFromDB(firebaseSQLIteDBName,"Q6",pieChart4);

                        tvPiechart5FB.setText(getResources().getString(R.string.q7));
                        drawPieChartFromDB(firebaseSQLIteDBName,"Q7",pieChart5);

                    }catch (Exception ex){

                        ex.printStackTrace();
                    }

                }else {
                    //comments   Log.d("Piechart","Data not found in Table "+firebaseSQLIteDBName+" Nothing to show");
                    tvLoadSuccess.setText("No Pie Data found in Table !");
                    tvLoadSuccess.setTextColor(getResources().getColor(R.color.errorColor));
                }

            }
        });

        btnDeleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackDatabase.deleteTableData(firebaseSQLIteDBName);
            }
        });

    }



    private ArrayList retrieveDataFromFirebase(DataSnapshot dataSnapshot ) {

        ArrayList<String> list  = new ArrayList<>();
        String value;
        long count =0;

        if(dataSnapshot.exists()){
           // scrollText.setText("");
            for(DataSnapshot ds : dataSnapshot.getChildren()){
                value = ds.getValue(String.class);
                //comments     Log.d("Firebase Info","Firebase Values reading --> "+value);
                list.add(value);
                count++;
            }
            return list;

        }else {
            Toast.makeText(this,"Firebase Data Load failed",Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    public boolean writeFirebaseDataInSQLite (ArrayList list){


        //comments   Log.d("Firebase Loading","Inside firebase Sqlite method");

        try{
            Iterator itr = list.iterator();
            FeedbackDatabase database = new FeedbackDatabase(this);

            boolean dbStatus = false;
            while (itr.hasNext()){
                String str = itr.next().toString();
                String array[] = str.split(",",-1);

                 dbStatus= database.insertFeedback(firebaseSQLIteDBName,
                        array[0], //Timestamp
                        array[1], //Name
                        Integer.parseInt(array[2]),  //Age
                        array[3], // Gender
                        array[4], // Place
                        array[5], // Number
                        array[6], //email
                        Integer.parseInt(array[7]), //star 1
                        Integer.parseInt(array[8]), //star 2
                        Integer.parseInt(array[9]), //star 3
                        Integer.parseInt(array[10]), //star 4
                        Integer.parseInt(array[11]), // star 5
                        array[12]); //comments
            }

            return dbStatus;

        }catch (Exception  ex){

            ex.printStackTrace();
            Log.d("Load in SQLite","Exception occurred due to "+ex.toString());
            return false;
        }
    }

    public void showGenderAndAgeGroup(){

        long countofMale,countOfFemale,totalCount,kidsAgeRange,youthAgeRange,midAgeRange,oldAgeRange;
        countofMale = feedbackDatabase.getGenderCount(firebaseSQLIteDBName,"Male");
        countOfFemale =feedbackDatabase.getGenderCount(firebaseSQLIteDBName,"Female");
        totalCount = feedbackDatabase.getDataCount(firebaseSQLIteDBName);

        float percentageOfMale = (float) countofMale /totalCount;
        percentageOfMale = percentageOfMale*100;

        float percentageOfFemale = (float) countOfFemale /totalCount;
        percentageOfFemale = percentageOfFemale*100;

        kidsAgeRange = feedbackDatabase.getAgeRange(firebaseSQLIteDBName,0,16);
        youthAgeRange = feedbackDatabase.getAgeRange(firebaseSQLIteDBName,17,25);
        midAgeRange = feedbackDatabase.getAgeRange(firebaseSQLIteDBName,26,45);
        oldAgeRange = feedbackDatabase.getAgeRange(firebaseSQLIteDBName,46,99);

        String ageAndGenderInfo = " % of Male   : "+percentageOfMale+"\n"+
                " % of Female : "+percentageOfFemale+"\n\n"+
                " Saints between 01 - 16 Years  : "+kidsAgeRange+"\n"+
                " Saints between 17 - 25 Years  : "+youthAgeRange+"\n"+
                " Saints between 26 - 45 Years  : "+midAgeRange+"\n"+
                " Saints between 45 - 99 Years  : "+oldAgeRange+"\n"+
                "Total number of feedback from Saints    : "+totalCount+"\n";
        tvGendernAgeGroup.setText(ageAndGenderInfo);
        tvGendernAgeGroup.setTextColor(getResources().getColor(R.color.ratingLableColor));
        tvGendernAgeGroup.setTypeface(null, Typeface.BOLD);
    }

    public void drawPieChartFromDB(String tableName,String columnName,PieChart pieChart)throws Exception{

        //comments Log.d(getLocalClassName(),"Running for table "+tableName+" & column name" +columnName);

        //Defining ArrayList to store PieEntry values
        ArrayList <PieEntry> pieEntries= new ArrayList<>();

        //getting total count from database to calculate percentage
        long totalRecordinDB = feedbackDatabase.getDataCount(tableName);
        //comments Log.d(getLocalClassName(),"Total record in database = "+totalRecordinDB);
        if(!(totalRecordinDB<=0)) {

            //getting values of count of distinct feedback values from database
            TreeMap map = feedbackDatabase.getColumnCount(tableName,columnName);

            //Here I'm iterating hashmap for each dataset
            Set entrySet =  map.entrySet();
            Iterator itr = entrySet.iterator();


            while (itr.hasNext()) {
                Map.Entry entry = (Map.Entry) itr.next();
                int key = Integer.parseInt( entry.getKey().toString());
                long value = Integer.parseInt( entry.getValue().toString());
                //float percentage ;
                long countOfFeedback = value;

                //comments  System.out.println("Total feedback count for "+key+" is "+countOfFeedback);

                float percentage = (float) countOfFeedback /totalRecordinDB;
                //comments   System.out.println("Before multiplying to 100"+percentage);
                percentage = percentage*100;
                //comments   System.out.println("Calculation of percentage is "+percentage);
                pieEntries.add(new PieEntry(percentage, FeedbackUtility.getfeedbackIdentifier(key)));
            }
            PieDataSet dataSet = new PieDataSet(pieEntries,columnName);
            dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            dataSet.setSliceSpace(4.0f);
            dataSet.setValueTextSize(14f);
            dataSet.setValueTextColor(Color.DKGRAY);
            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter()); //
            pieChart.setData(data);
            pieChart.setDrawHoleEnabled(true);
            pieChart.setTransparentCircleRadius(25f);
            pieChart.setHoleRadius(25f);
            pieChart.animateY(1000);
            pieChart.invalidate();

            //comments   Log.d(getLocalClassName(),"Finished for column " +columnName);

        }else{
            //comments  Log.d(getLocalClassName(),"No Record in database  !!");
        }

    }
    public  String getQuestionIdentifier(int key){

        Map map = new HashMap();
        map.put(1,getResources().getString(R.string.q3));
        map.put(2,getResources().getString(R.string.q4));
        map.put(3,getResources().getString(R.string.q5));
        map.put(4,getResources().getString(R.string.q6));
        map.put(5,getResources().getString(R.string.q7));

        return map.get(key).toString();
    }
}
