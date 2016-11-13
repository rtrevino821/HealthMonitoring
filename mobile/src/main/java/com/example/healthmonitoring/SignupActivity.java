package com.example.healthmonitoring;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/** similar to find view by ID */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @InjectView(R.id.input_Lname) EditText _lname;
    @InjectView(R.id.input_age) EditText _age;
    @InjectView(R.id.input_Gender) EditText _gender;
    @InjectView(R.id.input_Address) EditText _address;
    @InjectView(R.id.input_city) EditText _city;
    @InjectView(R.id.input_state) EditText _state;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);  //  inject views


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
                insertDatabase();

              // Log.e("tag",name);
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

    }


   // String name = _nameText.getText().toString();
   // String email = _emailText.getText().toString();
   // String password = _passwordText.getText().toString();

    //@OnClick(R.id.btn_signup)
    public void insertDatabase() {

       final String Username = _nameText.getText().toString();
       final String Userpassword = _passwordText.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {

                insert(Username,Userpassword);
            }
        }).start();
    }

    protected void insert(String Username, String Userpassword)  {
        Log.e(Username,Userpassword);

        String sql = "INSERT INTO Login (UserName, Password)" +
                " VALUES ('"+ Username +"' , '"+ Userpassword+ "');";

        String dbuserName = "root";
        String dbpassword = "Ateamhealth";

        try {
            Class.forName("com.mysql.jdbc.Driver");  //healthApp?zeroDateTimeBehavior=convertToNull
            String url = "jdbc:mysql://104.196.134.4/healthApp?account=root&password=Ateamhealth";
            Connection c = DriverManager.getConnection(url, dbuserName, dbpassword);
            PreparedStatement st = c.prepareStatement(sql);


            st.execute();
            st.close();
            c.close();
        } catch (ClassNotFoundException e)  {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    //@OnClick(R.id.btn_signup)
    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);        /** created in styles.xml */
        progressDialog.setIndeterminate(true);       /** loading bar */
        progressDialog.setMessage("Creating Account...");  /** display when account created */
        progressDialog.show();

       // String name = _nameText.getText().toString();
       // String email = _emailText.getText().toString();
       // String password = _passwordText.getText().toString();


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {

                        onSignupSuccess();
                        // onSignupFailed();  //*adjust later*/
                        progressDialog.dismiss();
                    }
                }, 3000);
    }




    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        /** login validations necessary characters needed*/
        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");  // pop up screen for error
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
