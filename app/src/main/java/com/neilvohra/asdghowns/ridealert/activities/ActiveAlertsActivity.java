package com.neilvohra.asdghowns.ridealert.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.neilvohra.asdghowns.ridealert.ActiveAlertsAdapter;
import com.neilvohra.asdghowns.ridealert.R;

public class ActiveAlertsActivity extends DrawerBaseActivity {
    private ListView activeAlerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_alerts);

        addDrawerItems();
        setupDrawer();

        activeAlerts = (ListView) findViewById(R.id.active_alerts_list_old);
        ActiveAlertsAdapter adapter = new ActiveAlertsAdapter(this);
        activeAlerts.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_active_alerts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
