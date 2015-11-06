package com.neilvohra.asdghowns.ridealert;

import android.view.View;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class NavigationDrawerAdapter extends BaseAdapter {
    private Context context;
    private String[] menuOptions;

    public NavigationDrawerAdapter(Context context, String[] menuOptions) {
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
        final String company = menuOptions[position];
        final DrawerViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.navigation_drawer_list_item, null);

            holder = new DrawerViewHolder(context);
            holder.menuOption = (TextView) convertView.findViewById(R.id.drawer_list_text);
        } else {
            holder = (DrawerViewHolder) convertView.getTag();
        }

        convertView.setTag(holder);
        holder.menuOption.setText(company);
        return convertView;
    }

    private class DrawerViewHolder extends View {
        private TextView menuOption;

        public DrawerViewHolder(Context context) {
            super(context);
        }
    }
}