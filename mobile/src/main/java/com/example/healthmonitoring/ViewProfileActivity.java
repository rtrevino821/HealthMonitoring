package com.example.healthmonitoring;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewProfileActivity extends AppCompatActivity {
    EditText fName;
    EditText lName;
    EditText dateOfBirth;
    EditText gender;
    EditText emerContact;
    TextView hrLimit;
    EditText address;
    EditText city;
    EditText state;
    String patientID;
    ResultSet rs;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        initializeTextViews();
        retrievePatientID();
        //getDatabaseData(patientID);
        //setText(rs);

    }

    private void initializeTextViews() {
        fName = (EditText) findViewById(R.id.tv_First_Name);
        lName = (EditText) findViewById(R.id.tv_Last_Name);
        dateOfBirth = (EditText) findViewById(R.id.tv_DOB);
        gender = (EditText) findViewById(R.id.tv_Gender);
        emerContact = (EditText) findViewById(R.id.tv_Emergency_Contact);
        hrLimit = (TextView) findViewById(R.id.tv_HR_Limit);
        address = (EditText) findViewById(R.id.tv_Address);
        city = (EditText) findViewById(R.id.tv_City);
        state = (EditText) findViewById(R.id.tv_State);
    }


    private void retrievePatientID() {
        patientID = null;
    }

    private void getDatabaseData(String id) {

        try {
            String sql = "SELECT F_Name, L_Name, Age, Gender, Emer_Contact, HR_Limits, Address, City, State FROM Patient WHERE patientIdValue =?";
            Connection conn = SQLConnection.doInBackground();
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, id);

            rs = pst.executeQuery(sql);
            setText(rs);

            rs.close();
            pst.close();
            conn.close();

        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }


    private void setText(ResultSet rs) {
        try {
            while (rs.next()) {
                fName.setText(rs.getString("F_Name"));
                lName.setText(rs.getString("L_Name"));
                dateOfBirth.setText(rs.getString("Age"));
                gender.setText(rs.getString("Gender"));
                emerContact.setText(rs.getString("Emer_Contact"));
                hrLimit.setText(rs.getString("HR_Limits"));
                address.setText(rs.getString("Address"));
                city.setText(rs.getString("City"));
                state.setText(rs.getString("State"));
            }
        } catch (SQLException e) {

        }

    }
}