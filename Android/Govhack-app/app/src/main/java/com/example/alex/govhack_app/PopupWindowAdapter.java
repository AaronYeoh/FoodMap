package com.example.alex.govhack_app;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by alex on 5/07/15.
 */
public class PopupWindowAdapter implements GoogleMap.InfoWindowAdapter{
    private View popup=null;
    private LayoutInflater inflater=null;

    PopupWindowAdapter(LayoutInflater inflater) {
        this.inflater=inflater;
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }
    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.popup_window, null);
        }
        TextView name=(TextView)popup.findViewById(R.id.name_of);
        name.setText(marker.getTitle());

        TextView info=(TextView)popup.findViewById(R.id.info_about);
        info.setText(marker.getSnippet());

        return(popup);
    }
}
