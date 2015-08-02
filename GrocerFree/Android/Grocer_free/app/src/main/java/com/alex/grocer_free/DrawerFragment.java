package com.alex.grocer_free;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by alex on 8/07/15.
 */
public class DrawerFragment extends Fragment {
    Geocoder geocoder;
    List<Address> addresses;
    Context context;
    LocalDatabase db;
    private ListView listView;
    String name;
    String des;
    ParseGeoPoint fruitLatLng;
    ParseFile img;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = container.getContext();
        db = new LocalDatabase(context);
        geocoder = new Geocoder(context);

        final View frag = inflater.inflate(R.layout.drawer_fragment, container, false);

        final TextView description = (TextView) frag.findViewById(R.id.description);
        listView = (ListView) frag.findViewById(R.id.update_list);
        final EditText updateEdit = (EditText) frag.findViewById(R.id.update_edit);
        Button updateListButton = (Button) frag.findViewById(R.id.updateListButton);
        final ImageView imageView = (ImageView) frag.findViewById(R.id.image);
        final TextView fruitType = (TextView) frag.findViewById(R.id.fruit_type);
        final TextView location = (TextView) frag.findViewById(R.id.location);

        Bundle bundle = this.getArguments();
        final double lat = bundle.getDouble("currentLat");
        final double lng = bundle.getDouble("currentLng");

        ParseQuery query = new ParseQuery("TestObject");
        ParseGeoPoint point = new ParseGeoPoint(lat, lng);
        //query.whereEqualTo("LatLng", point);
        query.whereWithinKilometers("LatLng", point, 0.001);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> trees, ParseException e) {
                //todo loading screen while async because jank
                ParseObject fruit = trees.get(0);
                name = fruit.getString("fruitType");
                Toast.makeText(context, name, Toast.LENGTH_LONG).show();

                des = fruit.getString("description");
                fruitLatLng = fruit.getParseGeoPoint("LatLng");
                img = fruit.getParseFile("images");
                try {
                    addresses = geocoder.getFromLocation(lat, lng, 1);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
                fruitType.setText(name + " tree");
                location.setText(addresses.get(0).getSubLocality() + ", " + addresses.get(0).getLocality());
                description.setText(des);
                try {
                    imageView.setImageBitmap(getImage(img.getData()));
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });



        //Set listview of updates for item
        //TODO custom adapter and all that
        //final ArrayList<String> updateItems = getUpdates(desc);
        //UpdateListAdapter adapter = new UpdateListAdapter(context, R.layout.update_list_row, updateItems);
        //listView.setAdapter(adapter);

        //Set image of item
        //imageView.setImageBitmap(
         //       getImage(item.getImage()));

        updateListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String update = updateEdit.getText().toString();
                db.updateItem(new LatLng(lat, lng), update);
                Item item = db.getItemByLatLng(lat, lng);
                ArrayList<String> updateItems = getUpdates(item.getDesc());
                UpdateListAdapter adapter = new UpdateListAdapter(context,
                        R.layout.update_list_row, updateItems);
                listView.setAdapter(adapter);
                Toast.makeText(context, item.getDesc().split("###").toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return frag;
        //return inflater.inflate(R.layout.drawer_fragment, container, false);
    }

    //private List<String> getUpdates(String desc) {
    //}


    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public ArrayList getUpdates(String desc){
        ArrayList updateList = new ArrayList();
        for(String update : desc.split("###")){
            updateList.add(update);
        }
        return updateList;
    }


}
