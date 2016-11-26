package com.example.healthmonitoring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class EmailClass extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Context context = this;

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            final String detailValue = extras.getString("patientEmail");
            if(detailValue != null) {
                Toast.makeText(context, "User Email" + detailValue, Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        sendEmail(detailValue);
                    }
                }).start();
            }
        }
    }


    protected void sendEmail(String email) {
        Log.i("Send email", "");
        String[] TO = {email};
        String[] CC = {"sjjoyvolk3640@eagle.fgcu.edu"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(EmailClass.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
