package com.salt.test.feedbackapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FireBaseActivity extends AppCompatActivity {


    private DatabaseReference firebaseDbRef;

    //private EditText editText;
    private ListView listView;
    private TextView scrollText ;
    private Button btnShowData,btnGlobalAnlysis;

    //Reference https://www.youtube.com/watch?v=sT8jJPJqMEg
    //https://github.com/mitchtabian/Firebase-Read-Database


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_base);

        firebaseDbRef = FirebaseDatabase.getInstance().getReference("Feedback2019");

        listView = (ListView) findViewById(R.id.firebaseListView);
        scrollText = (TextView)findViewById(R.id.tvFirebaseScrollText) ;
        btnShowData = (Button)findViewById(R.id.showFirebaseDataaaaa);
        btnGlobalAnlysis = (Button)findViewById(R.id.btnGlobalAnlysis);


        btnShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseDbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // This method is called once with the initial value and again
                        //https://github.com/mitchtabian/Firebase-Read-Database
                        // whenever data at this location is updated.
                        showData(dataSnapshot);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        btnGlobalAnlysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFireGraphActivity(1);

            }
        });



/*        firebaseDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String value = dataSnapshot.getValue(String.class);
                arrayList.add(value);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                arrayList.remove(value);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }

    private void showData(DataSnapshot dataSnapshot) {

        ArrayList<String> list  = new ArrayList<>();
        String value;
        long count =0;

        if(dataSnapshot.exists()){
            scrollText.setText("");
            for(DataSnapshot ds : dataSnapshot.getChildren()){
                value = ds.getValue(String.class);
                list.add("Record No : "+count+ "-> "+value);
        //comments        Log.d("Firebase Info","Firebase Values reading --> "+value);
                ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
                listView.setAdapter(adapter);
                count++;
            }

        }else {
            scrollText.setText("No data found in Firebase !");
            scrollText.setTextColor(getResources().getColor(R.color.errorColor));
        }

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


    private void startFireGraphActivity(int index) {
        Intent intent = new Intent(this, FireBaseGraph.class);
        intent.putExtra("ACTIONCODE", index);
        startActivity(intent);
    }
}
