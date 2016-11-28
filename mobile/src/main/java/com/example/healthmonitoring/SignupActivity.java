package com.example.healthmonitoring;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * similar to find view by ID
 */

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
    @InjectView(R.id.input_Phone) EditText _phone;
    @InjectView(R.id.input_Emergency) EditText _emergencyPhone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);  //  inject views

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
                if (validate()==true) {
                    insertDatabase();
                }

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


    //@OnClick(R.patientIdValue.btn_signup)
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
        final String PhoneNum = _phone.getText().toString();
        final String EmergencyPhone = _emergencyPhone.getText().toString();


        new Thread(new Runnable() {
            @Override
            public void run() {

                insert(Fname,L_name,UserAge,UserGender,UserAddress,UserCity,UserState, PhoneNum,EmergencyPhone);
                insertLogin(UserEmail,Userpassword, Fname);

            }
        }).start();
    }

    protected void insert(String... params) {

        String sql = "INSERT INTO Patient (F_Name, L_Name, Age, Gender, Address, City, State, Phone, Emer_Contact)" +
                " VALUES ('"+ params[0] +"' , '"+ params[1] +"', '"+ params[2] +"', '"+ params[3] +"', '"+ params[4] +
                "', '"+ params[5] +"', '"+ params[6] +"', '"+params[7]+"','"+params[8]+"');";

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
                "  Select Id, '"+ params[0] +"','"+ params[1] +"' From healthApp.Patient where F_Name='"+ params[2] +"'";

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


    //@OnClick(R.patientIdValue.btn_signup)
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
        final String Phone = _phone.getText().toString();
        final String EmerPhone = _emergencyPhone.getText().toString();


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

        if (Phone.length() < 10) {
            _phone.setError("Enter a valid phone number including area code");
            valid = false;
        }else {
            _phone.setError(null);
        }

        if (EmerPhone.length() < 10) {
            _emergencyPhone.setError("Enter a valid phone number including area code");
            valid = false;
        }else {
            _emergencyPhone.setError(null);
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

        if (UserGender.isEmpty()){
            if(UserGender.equals("M") || UserGender.equals("F")){
                _gender.setError(null);
            }else {
                _gender.setError("Enter a valid gender: M or F");
                valid = false;
            }
        }

        if (UserAddress.isEmpty()) {
            _address.setError("Please enter Address");
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
