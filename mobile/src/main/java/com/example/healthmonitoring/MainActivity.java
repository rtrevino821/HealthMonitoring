package com.example.healthmonitoring;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.healthmonitoring.R.id.action_settings;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView btnHRHistory;
    private ImageView btnViewProfile;
    private ImageView btnCheckMyPulse;
    private ImageView btnContactDoctor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");


        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_settings) {
                    Log.d("Tag", "logout is clicked");
                    Intent LogoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(LogoutIntent);
                    return true;
                }
                return false;
            }
        });

        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Tag", "SHIT IS CLICKED");
                Intent LogoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(LogoutIntent);
        }
        });*/


        btnHRHistory = (ImageView) findViewById(R.id.btn_view_alerts);
        btnViewProfile = (ImageView) findViewById(R.id.btn_my_patients);
        btnCheckMyPulse = (ImageView) findViewById(R.id.btn_check_my_pulse);
        btnContactDoctor = (ImageView) findViewById(R.id.btn_contact_doctor);

        btnHRHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent heartRateHistoryIntent = new Intent(MainActivity.this, HeartHistoryActivity.class);
                startActivity(heartRateHistoryIntent);
            }
        });

        btnViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewProfileIntent = new Intent(MainActivity.this, ViewProfileActivity.class);
                startActivity(viewProfileIntent);

            }
        });

        btnCheckMyPulse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkPulseIntent = new Intent(MainActivity.this, CheckPulseActivity.class);
                startActivity(checkPulseIntent);
            }
        });

        btnContactDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactIntent = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(contactIntent);
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.patientIdValue.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


       // insertDatabase(); // testing database connection

        //SharePreference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String restoredText = prefs.getString("ID", null);
        Log.d("neoGotId", restoredText);

    }





 /*   public void insertDatabase() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                insert();
            }
        }).start();
    }
    protected void insert()  {
        String sql = "INSERT INTO Login (UserName, Password)" +
                " VALUES ('hello', '567')";

        String userName = "root";
        String password = "Ateamhealth";

        try {

            Class.forName("com.mysql.jdbc.Driver");  //healthApp?zeroDateTimeBehavior=convertToNull

            String url = "jdbc:mysql://104.196.134.4/healthApp?account=root&password=Ateamhealth";
            Connection c = DriverManager.getConnection(url, userName, password);
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
*/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
