package com.example.healthmonitoring;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    private String admin = null;
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    /*private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };*/
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;
    private BackgroundTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View sign_up;
    public static final int REQUEST_SIGNUP=0;
    String patientIdReference;
    int patientIdValue;
    private Connection conn;
    //final  String sqlUser = "SELECT ID,Username,Password,Admin FROM healthApp.Logins WHERE Username = ? and `Password` = ?;";
    final  String sqlUser = "SELECT Logins.Id,Username, Password, Admin, Emer_Contact " +
                        "from healthApp.Logins join healthApp.Patient on Logins.Id = Patient.Id " +
                        "where Username = ? and Password = ?;";
    String TAG = "/LoginActivity";
    String ID;
    private String username = "";
    private String password = "";
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });



        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        sign_up =  findViewById(R.id.account_SignUp);  /** created for create signup finish manana*/

        sign_up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),SignupActivity.class);  // create activity for signup
                startActivityForResult(intent,REQUEST_SIGNUP);
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
                attemptLogin();
                /*if (attemptLogin()) {
                    Log.d("attempLogin", String.valueOf(patientIdValue));
                    //setPatientID();
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }*/
            }
        });



    }

    private void getPatientID() {
        String sql = "SELECT Id FROM healthApp.Logins WHERE Username = '" +  mEmailView.getText().toString() + "'";
        Log.d("SQL", sql);
        Connection conn = SQLConnection.doInBackground();
        if(conn == null){
            Log.d("Conn:", "Null!!");
        }
        try {

            PreparedStatement pst = conn.prepareStatement(sql);

            /*pst.setString(1, mEmailView.getText().toString());*/

            ResultSet rs = pst.executeQuery();

            if(rs.next()) {
                patientIdValue = rs.getInt("Id");
                System.out.println("PatientID!!!!!!!!" + patientIdValue);
                /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Name", "FUCKKKKKKK");
                editor.apply();*/
            }

            rs.close();
            pst.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setPatientID(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);


        /*SharedPreferences.Editor editor = getSharedPreferences("patientIdReference", MODE_PRIVATE).edit();
        editor.putString("patientID", "12345678");
        editor.commit();*/
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        /*if (mAuthTask != null) {
            return;
        }*/

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        username = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        } /*else if (!isPasswordValid(password)){
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }*/

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(username)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            //return false;
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new BackgroundTask(context);
            mAuthTask.execute();
            //return true;
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void getUsername()
    {
        username =mEmailView.getText().toString();
    }
    private void getPassword()
    {
        password = mPasswordView.getText().toString();
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

            //joinThread();

            getUsername();
            getPassword();
            try {
                Patient patient = login(username);
                if(patient != null) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("ID", patient.getId());
                    editor.putString("Admin", admin);
                    editor.putString("EmergencyContact", patient.getEmer_contact());
                    editor.commit();

                    return true;
                }else{
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*
            try {
                conn = SQLConnection.doInBackground();
                PreparedStatement prepare = conn.prepareStatement(sqlUser);

                getUsername();
                getPassword();

                prepare.setString(1, username);
                prepare.setString(2, password);
                Log.d("SQL", sqlUser);
                Log.d(TAG, username);
                Log.d(TAG, password);
                ResultSet rs = prepare.executeQuery();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = preferences.edit();
                Log.d(TAG, "before resulset");

                if (rs.next())
                {
                    Log.d(TAG, String.valueOf(rs.getInt("ID")));
                    Log.d(TAG, rs.getString("Username"));
                    Log.d(TAG, rs.getString("Password"));
                    Log.d(TAG, "Admin: " + rs.getString("Admin"));
                    Log.d(TAG, "EmerContact: " + rs.getString("Emer_Contact"));
                    //editor.putString("name", rs.getString("Username")); Unnecessary write to pref
                    editor.putString("ID", String.valueOf(rs.getInt("ID")));
                    editor.putString("Admin", String.valueOf(rs.getString("Admin")));
                    editor.putString("EmergencyContact", String.valueOf(rs.getString("Emer_Contact")));
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
            */
            //Login Failed
            Log.d(TAG,"asynctask ouside false");
            return false;

        }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
/*    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private Context context;

        UserLoginTask(String email, String password, Context context) {
            mEmail = email;
            mPassword = password;
            this.context = context;
        }



        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                conn = SQLConnection.doInBackground();

                PreparedStatement prepare = conn.prepareStatement(sqlUser);
                prepare.setString(1, username);
                ResultSet rs = prepare.executeQuery();
//                SharedPreferences prefs =
//                        context.getSharedPreferences(MY_PREFS_NAME,
//                                Context.MODE_PRIVATE);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = prefs.edit();

                while(rs.next())
                {
                    ID = String.valueOf(rs.getInt("ID"));
                    Log.d(TAG, String.valueOf(rs.getInt("ID")));
                    Log.d(TAG,rs.getString("Username"));
                    Log.d(TAG,rs.getString("Password"));

                }
                editor.putString("name", "Stoney");
                editor.putString("ID", ID);
                editor.commit();
                return true;

            }
            catch(SQLException e)
            {
                Log.d(TAG, e.getMessage());
            }
            return false;
        }*/

            /*for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }*/

        // TODO: register the new account here.


    @Override
    protected void onPostExecute(Boolean result) {
        //mAuthTask = null;
        showProgress(false);
        if (result) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String admin = preferences.getString("Admin", "N");
            if(admin.equalsIgnoreCase("Y")){
                Log.d("OnPostAdmin", "Yes");
                Intent mainDoctorIntent = new Intent(LoginActivity.this, MainActivityDoctor.class);
                startActivity(mainDoctorIntent);
                finish();
            }else {
                Log.d("OnPostAdmin", "Nah Fam");
                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        } else {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        }
    }

    @Override
    protected void onCancelled() {
        mAuthTask = null;
        showProgress(false);
    }

        public Patient login(String username) throws IOException, JSONException {

            URL url2 = new URL("https://q3igdv3op1.execute-api.us-east-1.amazonaws.com/prod/getPatientInfo?username=" + username);
            StringBuilder result2 = null;

            HttpURLConnection urlConnection2 = (HttpURLConnection) url2.openConnection();
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(url2.openStream()));
            result2 = new StringBuilder();
            String line2;
            while ((line2 = reader2.readLine()) != null) {
                result2.append(line2);
            }
            String resultString2 = result2.toString();
            //Log.d("TAG",resultString2);
            JSONObject patientItem = new JSONObject(resultString2);
            JSONArray patientInfoArray = patientItem.getJSONArray("Items");
            JSONObject patientInfo = (JSONObject) patientInfoArray.get(0);

            // Log.d("Tag", member.toString());
            ObjectMapper mapper = new ObjectMapper();

            Patient patient = mapper.readValue(patientInfo.toString(), Patient.class);
            // Log.d("Tag", user.getPassword());
            admin = patientInfo.getString("admin");

            return patient;
        }
}
}

