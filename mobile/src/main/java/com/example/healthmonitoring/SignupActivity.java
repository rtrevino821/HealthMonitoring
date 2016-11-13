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

    @InjectView(R.id.input_Fname) EditText _fname;
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


    //@OnClick(R.id.btn_signup)
    public void insertDatabase() {

        final String Fname = _fname.getText().toString();
        final String L_name = _lname.getText().toString();
        final String UserEmail = _emailText.getText().toString();
        final String UserAge = _age.getText().toString();
        final String UserGender = _gender.getText().toString();
        final String UserAddress = _address.getText().toString();
        final String UserCity = _city.getText().toString();
        final String UserState = _state.getText().toString();
        final String Userpassword = _passwordText.getText().toString();




        new Thread(new Runnable() {
            @Override
            public void run() {

                insert(Fname,L_name,UserAge,UserGender,UserAddress,UserCity,UserState,UserState);
                insertLogin(UserEmail,Userpassword, Fname);

            }
        }).start();
    }

    protected void insert(String... params) {

        String sql = "INSERT INTO Patient (F_Name, L_Name, Age, Gender, Address, City, State)" +
                " VALUES ('"+ params[0] +"' , '"+ params[1] +"', '"+ params[2] +"', '"+ params[3] +"', '"+ params[4] +
                "', '"+ params[5] +"', '"+ params[6] +"');";


        String dbuserName = "root";
        String dbpassword = "Ateamhealth";

        try {
            Class.forName("com.mysql.jdbc.Driver");  
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

    protected void insertLogin(String... params) {

          String sql = "INSERT INTO healthApp.Logins (Id, Username, Password)" +
                  "  Select id, '"+ params[0] +"','"+ params[1] +"' From healthApp.Patient where F_Name='"+ params[2] +"'";


        String dbuserName = "root";
        String dbpassword = "Ateamhealth";

        try {
            Class.forName("com.mysql.jdbc.Driver");
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

        String name = _fname.getText().toString();   // retrieving first name only !last
        final String L_name = _lname.getText().toString();
        String email = _emailText.getText().toString();
        final String UserAge = _age.getText().toString();
        final String UserGender = _gender.getText().toString();
        final String UserAddress = _address.getText().toString();
        final String UserCity = _city.getText().toString();
        final String UserState = _state.getText().toString();
        String password = _passwordText.getText().toString();

        /** login validations necessary characters needed*/
        if (name.isEmpty() || name.length() < 3) {             // only first name
            _fname.setError("enter your first name");
            valid = false;
        } else {
            _fname.setError(null);
        }

        if (L_name.isEmpty()) {             // only first name
            _lname.setError("Enter your last name");
            valid = false;
        } else {
            _lname.setError(null);
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

        if (UserAge.isEmpty() || UserAge.length() > 3) {
            _age.setError("Enter a valid age");
            valid = false;
        } else {
            _age.setError(null);
        }

        if (UserGender.isEmpty() || !UserGender.equals("M") || !UserGender.equals("F")) {             // only first name
            _gender.setError("Enter a valid gender");
            valid = false;
        } else {
            _gender.setError(null);
        }

        if (UserAddress.isEmpty()) {
            _address.setError("PLease enter Address");
            valid = false;
        } else {
            _address.setError(null);
        }

        if (UserCity.isEmpty()) {
            _city.setError("Please enter City");
            valid = false;
        } else {
            _city.setError(null);
        }

        if (UserState.isEmpty()) {
            _state.setError("Please enter State");
            valid = false;
        } else {
            _state.setError(null);
        }
        return valid;
    }
}
