package com.alex.grocer_free;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by alex on 8/07/15.
 */
public class DrawerFragment extends Fragment {
    Context context;
    LocalDatabase db;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        db = new LocalDatabase(context);

        View frag = inflater.inflate(R.layout.drawer_fragment, container, false);
        ImageView imageView = (ImageView) frag.findViewById(R.id.image);

        Bundle bundle = this.getArguments();
        double lat = bundle.getDouble("lat");
        double lng = bundle.getDouble("lng");

        double currentLat = bundle.getDouble("currentLat");
        double currentLng = bundle.getDouble("currentLng");

        Bitmap image = getImage(new LatLng(lat, lng));
        imageView.setImageBitmap(image);
        return frag;
        //return inflater.inflate(R.layout.drawer_fragment, container, false);
    }

    public Bitmap getImage(LatLng itemLatLng) {
        Item item = db.getItemByLatLng(itemLatLng.latitude, itemLatLng.longitude);
        Bitmap image = getImage(item.getImage());
        return image;
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
