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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

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

        TextView description = (TextView) frag.findViewById(R.id.description);
        ListView listView = (ListView) frag.findViewById(R.id.update_list);
        final EditText updateEdit = (EditText) frag.findViewById(R.id.update_edit);
        Button updateList = (Button) frag.findViewById(R.id.updateListButton);

        ImageView imageView = (ImageView) frag.findViewById(R.id.image);

        Bundle bundle = this.getArguments();
        final double lat = bundle.getDouble("lat");
        final double lng = bundle.getDouble("lng");

        Item item = db.getItemByLatLng(lat, lng);

        updateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String update = updateEdit.getText().toString();
                db.updateItem(new LatLng(lat, lng), update);
            }
        });
        //Set description for item
        description.setText(item.getDesc());

        //Set listview of updates for item
        //TODO custom adapter and all that
        //List<String> updateItems = (item.getDesc());
        //UpdateListAdapter adapter = new UpdateListAdapter(this, R.layout.update_list_row);
        //listView.setAdapter(adapter);

        //Set image of item
        imageView.setImageBitmap(
                getImage(item.getImage()));

        return frag;
        //return inflater.inflate(R.layout.drawer_fragment, container, false);
    }

    //private List<String> getUpdates(String desc) {
    //}


    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
