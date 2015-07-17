package com.alex.grocer_free;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by alex on 8/07/15.
 */
public class DrawerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        double lat = bundle.getDouble("lat");
        double lng = bundle.getDouble("lng");

        double currentLat = bundle.getDouble("currentLat");
        double currentLng = bundle.getDouble("currentLng");


        return inflater.inflate(R.layout.drawer_fragment, container, false);
    }
}
