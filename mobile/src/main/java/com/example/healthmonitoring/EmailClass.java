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
            final String patientEmail = extras.getString("patientEmail");
            if(patientEmail != null) {
                Toast.makeText(context, "User Email" + patientEmail, Toast.LENGTH_SHORT).show();
                final String patientName = extras.getString("patientName");
                final String date = extras.getString("date");
                final String heartRate = extras.getString("patientHeartRate");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        sendEmail(patientEmail, patientName, date, heartRate);
                    }
                }).start();
            }
        }
    }


    protected void sendEmail(String email, String name, String date, String heartRate) {
        Log.i("Send email", "");
        String[] TO = {email};
        String[] CC = {"sjjoyvolk3640@eagle.fgcu.edu"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Request For Heart Consultation");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello " + name + "\n"
                + "\tDue to a recent pulse reading of " + heartRate + " bpm on " + date
        + ", your Cardiologist would like to request for you to come in for a consultation "
        + "regarding this matter. You can request an appointment on our mobile app under the "
        + "contact us option.\n\nRegards.");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(EmailClass.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
