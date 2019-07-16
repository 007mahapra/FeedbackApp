package com.salt.test.feedbackapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {


    //Defining required variable for this app
    // Edit Texts
    private EditText etName,etAge,etPlace,etContact,etEmail,etComments;

    //TextInputLayouts for validation and Material design
    private TextInputLayout txtLOName,txtLOPlace,txtLOAge,txtLOConact,txtLOEmail,txtLOComments;

    // Gender Radio Button
    private RadioGroup rbGrpGender;

    //Database stuffs
    private FeedbackDatabase database;

    //  Reference taken from https://www.simplifiedcoding.net/firebase-realtime-database-crud/
    // https://www.youtube.com/watch?v=EM2x33g4syY&index=1&list=PLk7v1Z2rk4hj6SDHf_YybDeVhUT9MXaj1&t=235s
    DatabaseReference firebaseDBFeedback;
/*    private RadioGroup radioGrpQ3,radioGrpQ4,radioGrpQ5,radioGrpQ6,radioGrpQ7;*/

    private Button btnSubmit,btnClear;

    static boolean dbtrue = false;

    private TextView rblbl3error,rblbl4error,rblbl5error,rblbl6error,rblbl7error,tvGenderErrorlbl,resultLable;

    private RatingBar ratingBarQ1,ratingBarQ2,ratingBarQ3,ratingBarQ4,ratingBarQ5;

    FeedbackDTO dto;

    // Identifier for the permission request
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Info","Setting ContentView...........");
        setContentView(R.layout.activity_main);

        Log.d("Info","Requesting Permission..");
        reqPermissions();

        Log.d("Info","Requesting Permission Ended..");

        if(!dbtrue){
            Log.d("Info","Initialising database.....");
            database = new FeedbackDatabase(this);
            Log.d("Info","Database initialized .... ");
        }

       // resolveViews() function will resolve all variables of layout
        Log.d("Info","Resolving Views...............");
        resolveViews();

        Log.d("Info","Initializing Listeners ......");
        initializeListeners();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startInsightsActivity(1);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //below overrided method is required for Android 6.0 Marshmallow
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode){
            case REQUEST_ID_MULTIPLE_PERMISSIONS:

                for (int res : grantResults){
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed){
            Toast.makeText(this, "All permission Granted..", Toast.LENGTH_SHORT).show();
        }
        else {
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) && shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    showDialogOK("Read,Write and Internet permissions required !",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                           reqPermissions();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            System.exit(1);
                                            break;
                                    }
                                }
                            });
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Reference taken from https://github.com/learnpainless/Runtime-Permissions-Example
    //https://stackoverflow.com/questions/34342816/android-6-0-multiple-permissions

    public void reqPermissions(){
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        if (!FeedbackUtility.hasPermissions(this, PERMISSIONS)) {
            Log.d("Info","App does not have permission..");
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public void resolveViews (){

        //Buttons
        btnSubmit = (Button)findViewById(R.id.btnSubmit) ;
        btnClear = (Button)findViewById(R.id.btnClear);
        //btnInsights = (Button)findViewById(R.id.btnInsights) ;

        //EditTexts
        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etPlace = (EditText) findViewById(R.id.etPlace);
        etContact = (EditText) findViewById(R.id.etContact);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etComments = (EditText)findViewById(R.id.etComments);

        // Labels to show errors
        txtLOName = (TextInputLayout) findViewById(R.id.nameInputLayout);
        txtLOPlace = (TextInputLayout)findViewById(R.id.placeInputLayout);
        txtLOAge = (TextInputLayout) findViewById(R.id.ageInputLayout);
        txtLOConact = (TextInputLayout) findViewById(R.id.contactInputLayout);
        txtLOEmail = (TextInputLayout) findViewById(R.id.emailInputLayout);
        txtLOComments = (TextInputLayout)findViewById(R.id.txtLOComments);

        // txtLOGender = (TextInputLayout)findViewById(R.id.);
        rbGrpGender = (RadioGroup) findViewById(R.id.rbgrpGender);


      /*  radioGrpQ3 = (RadioGroup) findViewById(R.id.radioGroupQ3);
        radioGrpQ4 = (RadioGroup) findViewById(R.id.radioGroupQ4);
        radioGrpQ5 = (RadioGroup) findViewById(R.id.radioGroupQ5);
        radioGrpQ6 = (RadioGroup) findViewById(R.id.radioGroupQ6);
        radioGrpQ7 = (RadioGroup) findViewById(R.id.radioGroupQ7);*/

        //Rating bars
        ratingBarQ1 =(RatingBar)findViewById(R.id.ratingBarQ1);
        ratingBarQ2 =(RatingBar)findViewById(R.id.ratingBarQ2);
        ratingBarQ3 =(RatingBar)findViewById(R.id.ratingBarQ3);
        ratingBarQ4 =(RatingBar)findViewById(R.id.ratingBarQ4);
        ratingBarQ5 =(RatingBar)findViewById(R.id.ratingBarQ5);


        //Error Textviews for RadioButtons

        rblbl3error =(TextView)findViewById(R.id.rb3lblerror);
        rblbl4error =(TextView)findViewById(R.id.rb4lblerror);
        rblbl5error =(TextView)findViewById(R.id.rb5lblerror);
        rblbl6error =(TextView)findViewById(R.id.rb6lblerror);
        rblbl7error =(TextView)findViewById(R.id.rb7lblerror);
        tvGenderErrorlbl =(TextView)findViewById(R.id.tvGenderError);

        resultLable = (TextView)findViewById(R.id.finalResultLable);

    }

    private void initializeListeners(){

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),"Clicked Submit button",Toast.LENGTH_SHORT).show();
                boolean validationStatus = false;
                validationStatus=validateFields();

                if(validationStatus){
                    String result = "";
                    dto = setValuesOfFeedback();

                    //Storing Data in Local SQLite Database
                    //comments    Log.d("Info","Saving value of feedback data in Database..............");
                    getFieldsValuesDB(dto);

                    //Storing Data in External Storage
                    //result = getFieldsValuesInCSV();
                    //comments  Log.d("Info","Saving value of feedback data in CSV.................");
                    result = getFieldsValuesInCSV2(dto);
                    Log.d("Info","Values in CSV...."+result);

                    if(result!=null){
                        resultLable.setText("Saved Values is :" +result);
                        resultLable.setTextColor(getResources().getColor(R.color.submitColor));
                        FeedbackUtility.writeFileToDisk(result);
                    }

                    //comments   Log.d("Info","Saving value of feedback data in Firebase.................");
                    addDataInFirebase(dto);
                    //comments   Log.d("Info","Saving value of feedback data in Firebase Ended.............");

                    //comments  Log.d("Info","Clearing error labels....");
                    clearErrorLables();

                    if(validationStatus){
                        clearFieldsOnClick();
                        resultLable.setText("Saved Values is :" +result);
                    }

                }else {
                    Toast.makeText(getBaseContext(),"Submit failed",Toast.LENGTH_SHORT).show();
                    resultLable.setText("Submit Failed !!");
                    resultLable.setTextColor(getResources().getColor(R.color.errorColor));
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFieldsOnClick();
            }
        });


    }

    public boolean validateFields() {

        boolean isValid=true;

        if(etName.getText().toString().isEmpty()){
            txtLOName.setError(getString(R.string.nameErrorMsg));
            isValid = false;
        }else if(etName.getText().toString().contains(",")){
            txtLOName.setError(getString(R.string.removeCommaError));
            isValid = false;
        }else{
            txtLOName.setErrorEnabled(false);
        }

        if(etAge.getText().toString().isEmpty()){
            txtLOAge.setError(getString(R.string.ageErrorMsg));
            isValid = false;
        }else {
            txtLOAge.setErrorEnabled(false);
        }


        if(etPlace.getText().toString().isEmpty()){
            txtLOPlace.setError(getString(R.string.placeErrorMsg));
            isValid = false;
        }else if(etPlace.getText().toString().contains(",")){
            txtLOPlace.setError(getString(R.string.removeCommaError));
            isValid = false;
        }else{
            txtLOName.setErrorEnabled(false);
        }


        if(etContact.getText().toString().isEmpty()){
            txtLOConact.setError(getString(R.string.contactErrorMsg));
            isValid = false;
        }else {
            txtLOConact.setErrorEnabled(false);
        }

        if(etEmail.getText().toString().isEmpty()){
            etEmail.setText("");
        }else if(etEmail.getText().length()>0){
            String email = etEmail.getText().toString().trim();
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if(!email.matches(emailPattern)){
                txtLOEmail.setError("Please enter valid Email.");
                isValid = false;
            }
        }else{
            txtLOEmail.setErrorEnabled(false);
        }

        if(etComments.getText().toString().isEmpty()){
            etComments.setText("");
        }else if(etComments.getText().toString().contains(",")){
            txtLOComments.setError(getString(R.string.removeCommaError));
            isValid = false;
        }else {
            txtLOComments.setErrorEnabled(false);
        }

        if(rbGrpGender.getCheckedRadioButtonId() == -1){
            tvGenderErrorlbl.setText("Please choose Gender..");
            isValid = false;
        }

        if(ratingBarQ1.getRating()==0.0){
            rblbl3error.setText("Please choose rating..");
            isValid=false;
        }

        if(ratingBarQ2.getRating()==0.0){
            rblbl4error.setText("Please choose rating..");
            isValid=false;
        }

        if(ratingBarQ3.getRating()==0.0){
            rblbl5error.setText("Please choose rating..");
            isValid=false;
        }

        if(ratingBarQ4.getRating()==0.0){
            rblbl6error.setText("Please choose rating..");
            isValid=false;
        }

        if(ratingBarQ5.getRating()==0.0){
            rblbl7error.setText("Please choose rating..");
            isValid=false;
        }

/*        if(radioGrpQ3.getCheckedRadioButtonId() == -1){
            rblbl3error.setText("Please choose any option..");
            isValid = false;
        }

        if(radioGrpQ4.getCheckedRadioButtonId() == -1){
            rblbl4error.setText("Please choose any option..");
            isValid = false;
        }

        if(radioGrpQ5.getCheckedRadioButtonId() == -1){
            rblbl5error.setText("Please choose any option..");
            isValid = false;
        }

        if(radioGrpQ6.getCheckedRadioButtonId() == -1){
            rblbl6error.setText("Please choose any option..");
            isValid = false;
        }

        if(radioGrpQ7.getCheckedRadioButtonId() == -1){
            rblbl7error.setText("Please choose any option..");
            isValid = false;
        }*/

        if(isValid){
            //Toast.makeText(MainActivity.this, R.string.successSubmit, Toast.LENGTH_SHORT).show();
            //comments  Log.d("Info","Validation Successful");
        }else{
            Toast.makeText(MainActivity.this, R.string.failSubmit, Toast.LENGTH_SHORT).show();
            //comments  Log.d("Info","Validation Failed ");
        }
        return isValid;
    }


    public FeedbackDTO setValuesOfFeedback( ){
        try{
            dto = new FeedbackDTO();
            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
            String currentDateandTime = sdf.format(new Date());

            String email =etEmail.getText().toString();
            String comments =etComments.getText().toString();

            dto.setTimestamp(currentDateandTime);
            dto.setName(etName.getText().toString().trim());
            dto.setAge(Integer.parseInt(etAge.getText().toString().trim()));
            dto.setGender(getRadioButtonOutputInString(rbGrpGender).toString().trim());
            dto.setPlace(etPlace.getText().toString().trim());
            dto.setContact(etContact.getText().toString().trim());
            dto.setEmail(etEmail.getText().toString().trim());
            dto.setQ3((int) ratingBarQ1.getRating());
            dto.setQ4((int) ratingBarQ2.getRating());
            dto.setQ5((int) ratingBarQ3.getRating());
            dto.setQ6((int) ratingBarQ4.getRating());
            dto.setQ7((int) ratingBarQ5.getRating());

/*          dto.setQ3(Integer.parseInt(getRadioButtonOutputInString(radioGrpQ3).toString().trim()));
            dto.setQ4(Integer.parseInt(getRadioButtonOutputInString(radioGrpQ4).toString().trim()));
            dto.setQ5(Integer.parseInt(getRadioButtonOutputInString(radioGrpQ5).toString().trim()));
            dto.setQ6(Integer.parseInt(getRadioButtonOutputInString(radioGrpQ6).toString().trim()));
            dto.setQ7(Integer.parseInt(getRadioButtonOutputInString(radioGrpQ7).toString().trim()));*/
            dto.setComments(etComments.getText().toString().trim());
            return dto;

        }catch (Exception ex){
            Log.d("Info","Exception occurred while retrieving values in DTO : "+ex.toString());
            ex.printStackTrace();
            return null;
        }
    }

    public String getFieldsValuesInCSV2(FeedbackDTO fdto){

        String values;
        String comma=",";

        values = fdto.getTimestamp()+comma+
                fdto.getName()+comma+
                fdto.getAge()+comma+
                fdto.getGender()+comma+
                fdto.getPlace()+comma+
                fdto.getContact()+comma+
                fdto.getEmail()+comma+
                fdto.getQ3()+comma+
                fdto.getQ4()+comma+
                fdto.getQ5()+comma+
                fdto.getQ6()+comma+
                fdto.getQ7()+comma+
                fdto.getComments();
        return values;
    }

    public boolean getFieldsValuesDB(FeedbackDTO fdto){

        boolean dbSuccessFlag;
        dbSuccessFlag = database.insertFeedback("FEEDBACKDATA",
                fdto.getTimestamp(),
                fdto.getName(),
                fdto.getAge(),
                fdto.getGender(),
                fdto.getPlace(),
                fdto.getContact(),
                fdto.getEmail(),
                fdto.getQ3(),
                fdto.getQ4(),
                fdto.getQ5(),
                fdto.getQ6(),
                fdto.getQ7(),
                fdto.getComments());
        return dbSuccessFlag;
    }

    //Function to save data in Firebase database
    public void addDataInFirebase(FeedbackDTO fdto){
        firebaseDBFeedback = FirebaseDatabase.getInstance().getReference("Feedback2019");
        String uniqeKey = firebaseDBFeedback.push().getKey();
        String values = getFieldsValuesInCSV2(fdto);
        firebaseDBFeedback.child(uniqeKey).setValue(values);
        Log.d("Info"," addDataInFirebase() --> Firebase operation completed ");
    }

    public void clearErrorLables(){

        if(!rblbl3error.getText().toString().isEmpty()){
            rblbl3error.setText("");
        }

        if(!rblbl4error.getText().toString().isEmpty()){
            rblbl4error.setText("");
        }

        if(!rblbl5error.getText().toString().isEmpty()){
            rblbl5error.setText("");
        }


        if(!rblbl6error.getText().toString().isEmpty()){
            rblbl6error.setText("");
        }

        if(!rblbl7error.getText().toString().isEmpty()){
            rblbl7error.setText("");
        }

        if(!tvGenderErrorlbl.getText().toString().isEmpty()){
            tvGenderErrorlbl.setText("");
        }
    }
    public void clearFieldsOnClick(){
        //clearing EditTexts
        etName.setText("");
        etPlace.setText("");
        etAge.setText("");
        etContact.setText("");
        etEmail.setText("");

        //Uncheking the RadioButtons
        rbGrpGender.clearCheck();

        ratingBarQ1.setRating(0);
        ratingBarQ2.setRating(0);
        ratingBarQ3.setRating(0);
        ratingBarQ4.setRating(0);
        ratingBarQ5.setRating(0);
/*
        radioGrpQ3.clearCheck();
        radioGrpQ4.clearCheck();
        radioGrpQ5.clearCheck();
        radioGrpQ6.clearCheck();
        radioGrpQ7.clearCheck();
*/

        if(txtLOName.isErrorEnabled())
            txtLOName.setErrorEnabled(false);

        if(txtLOPlace.isErrorEnabled())
            txtLOPlace.setErrorEnabled(false);

        if(txtLOAge.isErrorEnabled())
            txtLOAge.setErrorEnabled(false);

        if(txtLOConact.isErrorEnabled())
            txtLOConact.setErrorEnabled(false);

        if(txtLOEmail.isErrorEnabled())
            txtLOEmail.setErrorEnabled(false);

        if(txtLOComments.isErrorEnabled())
            txtLOComments.setErrorEnabled(false);

        //Clearing the labels
        clearErrorLables();

        if(!resultLable.getText().toString().isEmpty()){
            resultLable.setText("");
        }

        if(!etComments.getText().toString().isEmpty()){
            etComments.setText("");
        }

        Log.d("Info","Fields Cleared");
    }


    /* Method : getRadioButtonOutput()
   * Pass RadioGroup Variable to use this method
   *
   */
    public void getRadioButtonOutput(RadioGroup radioGroup){
        int radioButtonid = radioGroup.getCheckedRadioButtonId();
        RadioButton rb;
        rb =(RadioButton)findViewById(radioButtonid);
        String radioButtonValue = rb.getText().toString();
        Toast.makeText(getBaseContext(),"Selected value is : "+radioButtonValue, Toast.LENGTH_SHORT).show();
    }

    public String getRadioButtonOutputInString(RadioGroup radioGroup){
        int radioButtonid = radioGroup.getCheckedRadioButtonId();
        RadioButton rb;
        rb =(RadioButton)findViewById(radioButtonid);
        String radioButtonValue = rb.getText().toString();
        return radioButtonValue;
        //Toast.makeText(getBaseContext(),"Selected value is : "+radioButtonValue, Toast.LENGTH_SHORT).show();
    }

    private void startInsightsActivity(int index) {
        Intent intent = new Intent(this, InsightsActivity.class);
        intent.putExtra("ACTIONCODE", index);
        startActivity(intent);
    }

}