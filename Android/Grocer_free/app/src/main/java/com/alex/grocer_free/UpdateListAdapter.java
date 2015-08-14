package com.alex.grocer_free;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 20/07/15.
 */
public class UpdateListAdapter extends ArrayAdapter {
    public UpdateListAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.update_list_row, null);

        String item = String.valueOf(getItem(position));
        Log.d("item", item);
        //Not sure if this will work

        String[] descDate = item.split(",");
        String descrip = descDate[0];
        String dateOf = descDate[1];

        if(item != null){
            TextView desc = (TextView) convertView.findViewById(R.id.update_description);
            TextView date = (TextView) convertView.findViewById(R.id.date);

            desc.setText(descrip);
            date.setText(dateOf);
        }

        return convertView;
    }

}
