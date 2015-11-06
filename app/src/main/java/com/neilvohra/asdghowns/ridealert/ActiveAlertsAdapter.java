package com.neilvohra.asdghowns.ridealert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class ActiveAlertsAdapter extends BaseAdapter {
    private Context context;
    private List<LocationTrackerService> activeServices;

    public ActiveAlertsAdapter(Context context, List<LocationTrackerService> activeServices) {
        this.context = context;
        this.activeServices = activeServices;
    }

    @Override
    public int getCount() {
        return activeServices.size();
    }

    @Override
    public Object getItem(int position) {
        return activeServices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LocationTrackerService service = activeServices.get(position);
        final AlertContactObject alertObject = service.getAlertObject();
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