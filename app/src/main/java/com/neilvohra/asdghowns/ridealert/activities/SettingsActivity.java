package com.neilvohra.asdghowns.ridealert.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.neilvohra.asdghowns.ridealert.R;
import com.neilvohra.asdghowns.ridealert.SettingsListAdapter;


public class SettingsActivity extends Activity {
    private ListView settingsList;
    private String[] settingsMenuOption = {
            "General",
            "About",
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsList = (ListView) findViewById(R.id.settings_list);
        SettingsListAdapter adapter = new SettingsListAdapter(this, settingsMenuOption);
        settingsList.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
