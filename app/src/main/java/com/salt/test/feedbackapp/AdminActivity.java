package com.salt.test.feedbackapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class AdminActivity extends Activity {

    private Button btnShow;
    private EditText etBtnPin;
    private TextView tvPINError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        etBtnPin = (EditText) findViewById(R.id.etPinCode);
        btnShow = (Button)findViewById(R.id.btnPinVerify);
        tvPINError = (TextView)findViewById(R.id.tvPINError);

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etBtnPin.getText().toString().trim().equals("2018") ||
                   etBtnPin.getText().toString().trim().equals("2019") ||
                   etBtnPin.getText().toString().trim().equals("2020")){
                    startFireBaseActivity(1);
                    tvPINError.setText("");
                }else
                {
                    tvPINError.setText(getResources().getString(R.string.pinErrorMsg));
                    tvPINError.setTextColor(getResources().getColor(R.color.errorColor));
                }
            }
        });
    }
        @Override
    public void onBackPressed() {
        startInsightsActivity(1);
    }

    private void startFireBaseActivity(int index) {
        Intent intent = new Intent(this, FireBaseActivity.class);
        intent.putExtra("ACTIONCODE", index);
        startActivity(intent);
    }

    private void startInsightsActivity(int index) {
        Intent intent = new Intent(this, InsightsActivity.class);
        intent.putExtra("ACTIONCODE", index);
        startActivity(intent);
    }


}
