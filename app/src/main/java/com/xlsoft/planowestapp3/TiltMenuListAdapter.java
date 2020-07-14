package com.xlsoft.planowestapp3;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.ArrayList;

public class TiltMenuListAdapter extends ArrayAdapter<TiltMenuEntry> {
    Context context;
    int resource;

    public TiltMenuListAdapter(Context context, int resource, ArrayList<TiltMenuEntry> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        String desc = getItem(position).getDesc();
        int item = getItem(position).getItem();
        TiltMenuEntry tiltMenuEntry = new TiltMenuEntry(desc, item);

        LayoutInflater inflator = LayoutInflater.from(context);
        convertView = inflator.inflate(resource, parent, false);
        ImageView itemDisplay = (ImageView) convertView.findViewById(R.id.menuIcon);
        TextView descDisplay = (TextView) convertView.findViewById(R.id.menuDesc);

        int[] resIds = new int[] {R.drawable.ic_lock, R.drawable.ic_unlock, R.drawable.ic_recenter};
        itemDisplay.setImageResource(resIds[item]);
        descDisplay.setText(desc);

        return convertView;
    }
}
