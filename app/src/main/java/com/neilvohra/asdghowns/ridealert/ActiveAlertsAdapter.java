package com.neilvohra.asdghowns.ridealert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ActiveAlertsAdapter extends BaseAdapter {
    private Context context;

    public ActiveAlertsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return RideAlertApplication.activeAlerts.size();
    }

    @Override
    public Object getItem(int position) {
        return RideAlertApplication.activeAlerts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final AlertContactObject alertObject = RideAlertApplication.activeAlerts.get(position);
        final DrawerViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.active_alerts_list_item, null);

            holder = new DrawerViewHolder(context);
            holder.header = (TextView) convertView.findViewById(R.id.active_alerts_header);
            holder.description = (TextView) convertView.findViewById(R.id.active_alerts_description);
        } else {
            holder = (DrawerViewHolder) convertView.getTag();
        }

        Button delete = (Button) convertView.findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RideAlertApplication.activeAlerts.remove(position);
                notifyDataSetChanged();
                if (RideAlertApplication.activeAlerts.size() == 0)
                {
                    RideAlertApplication.service.onDestroy();
                }
            }
        });

        convertView.setTag(holder);
        holder.header.setText(alertObject.getContactName());
        holder.description.setText(alertObject.getContactAddress().getAddressLine(0));
        return convertView;
    }

    private class DrawerViewHolder extends View {
        private TextView header;
        private TextView description;

        public DrawerViewHolder(Context context) {
            super(context);
        }
    }
}