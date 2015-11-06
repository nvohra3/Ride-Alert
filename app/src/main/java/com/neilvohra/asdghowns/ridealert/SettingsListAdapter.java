package com.neilvohra.asdghowns.ridealert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SettingsListAdapter extends BaseAdapter {
    private Context context;
    private String[][] menuOptions;

    public SettingsListAdapter(Context context, String[][] menuOptions) {
        this.context = context;
        this.menuOptions = menuOptions;
    }

    @Override
    public int getCount() {
        return menuOptions.length;
    }

    @Override
    public Object getItem(int position) {
        return menuOptions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String[] menuOption = menuOptions[position];
        final SettingsMenuHolder holder;

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.active_alerts_list_item, null);

            holder = new SettingsMenuHolder(context);
            holder.header = (TextView) convertView.findViewById(R.id.active_alerts_header);
            holder.description = (TextView) convertView.findViewById(R.id.active_alerts_description);
        } else
        {
            holder = (SettingsMenuHolder) convertView.getTag();
        }

        convertView.setTag(holder);
        holder.header.setText(menuOption[0]);
        holder.description.setText(menuOption[1]);
        return convertView;
    }

    private class SettingsMenuHolder extends View {
        private TextView header;
        private TextView description;

        public SettingsMenuHolder(Context context) {
            super(context);
        }
    }
}
