package com.example.healthmonitoring;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.healthmonitoring.LoginActivity.REQUEST_SIGNUP;

public class LoginActivityNeo extends AppCompatActivity   {
    private Connection conn;
    final static String sqlUser = "SELECT ID,Username,Password FROM healthApp.Logins WHERE Username = ? and `Password` = ?;";
    private AutoCompleteTextView usernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String username = "";
    private String password = "";
    private String TAG = "/Neo";
    private String ID;
    private boolean loginStatus;
    private BackgroundTask task;
    private Context context;
    private ResultSet rs;
    private Thread thread;

    public static final String MY_PREFS_NAME = "neoPref";
    @Override
    protected void onCreate   (Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        usernameView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        View sign_up =  findViewById(R.id.account_SignUp);  /** created for create signup finish manana*/
        sign_up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),SignupActivity.class);  // create activity for signup
                startActivityForResult(intent,REQUEST_SIGNUP);
            }
        });

        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getResulset();

                    task = new BackgroundTask(context);
                //showProgress(true);

                task.execute();

            }
        });
    }
    public void getResulset()
    {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    conn = SQLConnection.doInBackground();
                    PreparedStatement prepare = conn.prepareStatement(sqlUser);

                    getUsername();
                    getPassword();

                    prepare.setString(1, username);
                    prepare.setString(2, password);
                    Log.d(TAG, username);
                    Log.d(TAG, password);
                    rs = prepare.executeQuery();

                } catch (SQLException e) {
                    Log.d(TAG, e.getMessage());
                }

            }


        };
        thread = new Thread(runnable);
        thread.start();


    }
    private void getUsername()
    {
        username =usernameView.getText().toString();
    }
    private void getPassword()
    {
        password = mPasswordView.getText().toString();
    }
    private void loginStatus(boolean status)
    {
        loginStatus = status;
    }
    private void joinThread()
    {
        try{
            thread.join();
        }catch(Exception e){

        }
    }
    private class BackgroundTask extends AsyncTask<String,Void,Boolean> {

        private Context context;

        public BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            //String user = "Stoney";
            //String password = "FL";

            //String sql = "Select * From healthApp.Logins";

            joinThread();
            try {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = preferences.edit();
                Log.d(TAG, "before resulset");

                if (rs.next())
                {
                    Log.d(TAG, String.valueOf(rs.getInt("ID")));
                    Log.d(TAG, rs.getString("Username"));
                    Log.d(TAG, rs.getString("Password"));
                    editor.putString("name", rs.getString("Username"));
                    editor.putString("ID", String.valueOf(rs.getInt("ID")));
                    editor.commit();
                    //Login Successful
                    rs.close();
                    return true;
                }
                else
                {//no results
                    Log.d(TAG,"asynctask  inside false");
                    return false;
                }

            } catch (SQLException e) {
                Log.d(TAG, e.getMessage());
            }

            //Login Failed
            loginStatus(false);
            Log.d(TAG,"asynctask ouside false");
            return false;

        }


        @Override
        protected void onPostExecute(Boolean result)
        {
            showProgress(false);
            if(result)
            {
                Intent mainIntent = new Intent(LoginActivityNeo.this, MainActivity.class);
                startActivity(mainIntent);
            }
            else
            {
                Log.d(TAG,"Login fail");

            }
            Log.d("neoPost", "Preference received in background: " + result);
        }

    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
