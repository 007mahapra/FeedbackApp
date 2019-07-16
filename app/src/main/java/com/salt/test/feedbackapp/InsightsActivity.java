package com.salt.test.feedbackapp;

import android.content.Intent;
import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class InsightsActivity extends AppCompatActivity {


    private Button btnDBShow,btnShowGraph,btnFireBase;
    private TextView tvResultScroll,tvGenderAndAgeGroup,tvPiechart1,tvPiechart2,tvPiechart3,tvPiechart4,tvPiechart5;
    private ScrollView scrollResult,scrollGraphs;
    private PieChart pieChart3,pieChart4,pieChart5,pieChart6,pieChart7;


    private FeedbackDatabase feedbackDatabase;

    public final String localDBName ="FEEDBACKDATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);


        //comments  System.out.println("Initialising database.... ");
        feedbackDatabase = new FeedbackDatabase(this);
        //comments  System.out.println("Database initialized .... ");

        // resolveViews() function will resolve all variables of layout
        //comments  System.out.println("Resolving Views.............");
        this.resolveViews();


        //Intialise Listeners
        System.out.println("Initialise Listeners.............");
        initialiseListeners();

    }

    @Override
    public void onBackPressed() {
        startMainActivity(1);
    }

    public void resolveViews () {

        //Buttons
        btnDBShow = (Button) findViewById(R.id.btnDBShow);
        btnShowGraph =(Button)findViewById(R.id.btnGraphShow);
        btnFireBase = (Button)findViewById(R.id.btnDBFireBase);


        //Label
        tvResultScroll =(TextView)findViewById(R.id.tvResultScroll);
        tvGenderAndAgeGroup = (TextView) findViewById(R.id.tvGenderAndAgeGroup);
        tvPiechart1 =(TextView)findViewById(R.id.tvPiechart1) ;
        tvPiechart2 =(TextView)findViewById(R.id.tvPiechart2) ;
        tvPiechart3 =(TextView)findViewById(R.id.tvPiechart3) ;
        tvPiechart4 =(TextView)findViewById(R.id.tvPiechart4) ;
        tvPiechart5 =(TextView)findViewById(R.id.tvPiechart5) ;

        //ScrollViews
        scrollGraphs =(ScrollView) findViewById(R.id.scrollGraphic);
        scrollResult = (ScrollView)findViewById(R.id.scrollResult);

        //Pie Charts
        pieChart3 =(PieChart)findViewById(R.id.pieChart3);
        pieChart4 =(PieChart)findViewById(R.id.pieChart4);
        pieChart5 =(PieChart)findViewById(R.id.pieChart5);
        pieChart6 =(PieChart)findViewById(R.id.pieChart6);
        pieChart7 =(PieChart)findViewById(R.id.pieChart7);
    }

    public void initialiseListeners(){

        btnDBShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               scrollGraphs.setVisibility(View.INVISIBLE);
               scrollResult.setVisibility(View.VISIBLE);
               tvResultScroll.setText("");
               fetchDataFromDB();
            }
        });

        btnShowGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResultScroll.setText("");
                scrollResult.setVisibility(View.INVISIBLE);
                scrollGraphs.setVisibility(View.VISIBLE);
                try {
                    showGenderAndAgeGroup();

                    tvPiechart1.setText(getResources().getString(R.string.q3));
                    drawPieChartFromDB(localDBName,"Q3",pieChart3);

                    tvPiechart2.setText(getResources().getString(R.string.q4));
                    drawPieChartFromDB(localDBName,"Q4",pieChart4);

                    tvPiechart3.setText(getResources().getString(R.string.q5));
                    drawPieChartFromDB(localDBName,"Q5",pieChart5);

                    tvPiechart4.setText(getResources().getString(R.string.q6));
                    drawPieChartFromDB(localDBName,"Q6",pieChart6);

                    tvPiechart5.setText(getResources().getString(R.string.q7));
                    drawPieChartFromDB(localDBName,"Q7",pieChart7);

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(),"Error occurred due to : "+e.toString(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


        btnFireBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //For non admin app - comment this function
                startAdminActivity(1);

            }
        });
    }

    private void startMainActivity(int index) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ACTIONCODE", index);
        startActivity(intent);
    }

    private void startAdminActivity(int index) {
        Intent intent = new Intent(this, AdminActivity.class);
        intent.putExtra("ACTIONCODE", index);
        startActivity(intent);
    }



    public void fetchDataFromDB(){
     Cursor res =  feedbackDatabase.getFeedbackData(localDBName);
          if(res.getCount() == 0) {
         // show message
         tvResultScroll.setText("Error, Nothing found");

     }else {
         StringBuilder buffer = new StringBuilder();
         int count =0;
         while (res.moveToNext()) {
             count ++;
             buffer.append("\nRecord Number : "+count+ "\n");
             buffer.append("TIMESTAMP : "+res.getString(0)+"\n");
             buffer.append("NAME : "+res.getString(1)+"\n");
             buffer.append("AGE : "+res.getInt(2)+"\n");
             buffer.append("GENDER : "+res.getString(3)+"\n");
             buffer.append("PLACE : "+res.getString(4)+"\n");
             buffer.append("CONTACT : "+res.getString(5)+"\n");
             buffer.append("EMAIL : "+res.getString(6)+"\n");
             buffer.append("Q1 : "+res.getInt(7)+"\n");
             buffer.append("Q2 : "+res.getInt(8)+"\n");
             buffer.append("Q3 : "+res.getInt(9)+"\n");
             buffer.append("Q4 : "+res.getInt(10)+"\n");
             buffer.append("Q5 : "+res.getInt(11)+"\n");
             buffer.append("COMMENTS : "+res.getString(12));
             buffer.append("\n");
         }
         tvResultScroll.setText("Total record is :"+count+ " "+buffer.toString());
     }
 }

 public void showGenderAndAgeGroup(){

     long countofMale,countOfFemale,totalCount,kidsAgeRange,youthAgeRange,midAgeRange,oldAgeRange;
      countofMale = feedbackDatabase.getGenderCount(localDBName,"Male");
      countOfFemale =feedbackDatabase.getGenderCount(localDBName,"Female");
      totalCount = feedbackDatabase.getDataCount(localDBName);

     float percentageOfMale = (float) countofMale /totalCount;
     percentageOfMale = percentageOfMale*100;

     float percentageOfFemale = (float) countOfFemale /totalCount;
     percentageOfFemale = percentageOfFemale*100;

     kidsAgeRange = feedbackDatabase.getAgeRange(localDBName,0,16);
     youthAgeRange = feedbackDatabase.getAgeRange(localDBName,17,25);
     midAgeRange = feedbackDatabase.getAgeRange(localDBName,26,45);
     oldAgeRange = feedbackDatabase.getAgeRange(localDBName,46,99);

     String ageAndGenderInfo = " % of Male   : "+percentageOfMale+"\n"+
                               " % of Female : "+percentageOfFemale+"\n\n"+
                               " Saints between 01 - 16 Years  : "+kidsAgeRange+"\n"+
                               " Saints between 17 - 25 Years  : "+youthAgeRange+"\n"+
                               " Saints between 26 - 45 Years  : "+midAgeRange+"\n"+
                               " Saints between 45 - 99 Years  : "+oldAgeRange+"\n"+
                               " Total number of feedback from Saints    : "+totalCount+"\n";
     tvGenderAndAgeGroup.setText(ageAndGenderInfo);
     tvGenderAndAgeGroup.setTextColor(getResources().getColor(R.color.ratingLableColor));
     tvGenderAndAgeGroup.setTypeface(null, Typeface.BOLD);

 }

 public void drawPieChartFromDB(String tableName,String columnName,PieChart pieChart)throws Exception{

     //comments Log.d(getLocalClassName(),"Running for table "+tableName+" & column name" +columnName);

    //Defining ArrayList to store PieEntry values
    ArrayList <PieEntry> pieEntries= new ArrayList<>();

    //getting total count from database to calculate percentage
    long totalRecordinDB = feedbackDatabase.getDataCount(tableName);
     //comments  Log.d(getLocalClassName(),"Total record in database = "+totalRecordinDB);
    if(!(totalRecordinDB<=0)) {

        //getting values of count of distinct feedback values from database
        TreeMap map = feedbackDatabase.getColumnCount(tableName,columnName);

        //Here I'm iterating hashmap for each dataset
        Set entrySet =  map.entrySet();
        Iterator itr = entrySet.iterator();

        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry) itr.next();
            int key = Integer.parseInt( entry.getKey().toString());
            long value = Long.parseLong( entry.getValue().toString());
            //float percentage ;
            long countOfFeedback = value;

            //comments System.out.println("Total feedback count for "+key+" is "+countOfFeedback);

            float percentage = (float) countOfFeedback /totalRecordinDB;
            //comments System.out.println("Before multiplying to 100"+percentage);
            percentage = percentage*100;
            //comments System.out.println("Calculation of percentage is "+percentage);
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

        //comments  System.out.println("finished for column " +columnName);

    }else{
        //comments System.out.println("No Record in database  !!");
    }

 }


}
