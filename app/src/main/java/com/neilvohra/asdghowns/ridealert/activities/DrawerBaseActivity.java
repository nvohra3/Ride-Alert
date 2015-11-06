package com.neilvohra.asdghowns.ridealert.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.neilvohra.asdghowns.ridealert.NavigationDrawerAdapter;
import com.neilvohra.asdghowns.ridealert.R;
import com.neilvohra.asdghowns.ridealert.RideAlertApplication;


public class DrawerBaseActivity extends ActionBarActivity {
    protected ListView drawerList;
    protected DrawerLayout drawerLayout;
    protected NavigationDrawerAdapter mAdapter;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected String mActivityTitle;
    protected final String[] navigationDrawerOptions = {"Set Up Alert", "Active Alerts", "Settings"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_base);

        addDrawerItems();
        setupDrawer();
    }

    protected void addDrawerItems() {
        drawerList = (ListView) findViewById(R.id.navList);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        mAdapter = new NavigationDrawerAdapter(DrawerBaseActivity.this, navigationDrawerOptions);
        drawerList.setAdapter(mAdapter);

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                int currentSelectedMenuItem = RideAlertApplication.currentDrawerListOption;
                if (currentSelectedMenuItem == position)
                    drawerLayout.closeDrawers();
                else {
                    RideAlertApplication.currentDrawerListOption = position;
                    switch (position) {
                        case 0:
                            intent = new Intent(DrawerBaseActivity.this, HomeScreenActivity.class);
                            break;
                        case 1:
                            intent = new Intent(DrawerBaseActivity.this, ActiveAlertsActivity.class);
                            break;
                        case 2:
                            intent = new Intent(DrawerBaseActivity.this, SettingsActivity.class);
                            break;
                        default:
                            break;
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    protected void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Ride Alert Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                drawerList.bringToFront();
                drawerLayout.requestLayout();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
