package com.neilvohra.asdghowns.ridealert.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.ListView;

import com.neilvohra.asdghowns.ridealert.R;
import com.neilvohra.asdghowns.ridealert.SettingsListAdapter;


public class SettingsActivity extends ActionBarActivity {
    private ListView settingsList;
    private String[] settingsMenuOption = { "General", "About" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsList = (ListView) findViewById(R.id.settings_list);
        SettingsListAdapter adapter = new SettingsListAdapter(this, settingsMenuOption);
        settingsList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
}
