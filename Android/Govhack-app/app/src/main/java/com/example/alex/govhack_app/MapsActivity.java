package com.example.alex.govhack_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapsActivity extends FragmentActivity {
    Marker myMarker;
    Location getLastLocation;
    DrawerLayout drawerLayout;
    ListView drawer_list;
    double currentLongitude;
    double currentLatitude;
    LatLng currentLocation;
    GoogleMap fmap;
    public GoogleMap mMap; // Might be null if Google Play services APK is not available.
    LocationManager locationManager;
   FloatingActionButton fab;
    Spinner sortSpinner;
    String[] spinnerArray;
    Button spinner_button;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);


        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseWhereToAddFood();
            }
        });

        spinner_button = (Button) findViewById(R.id.spinner_button);
        sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
        spinner_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortSpinner.setVisibility(View.VISIBLE);
            }
        });
        //sortSpinner = (Spinner) findViewById(R.id.sort_spinner);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                String selecteditem;
                selecteditem = adapter.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(), selecteditem, Toast.LENGTH_SHORT);
                //mMap.clear();
                sortByItem(selecteditem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle() != null) {

                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                try {
                    getAllFoodLocations();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setUpMap() {

        locationManager = (LocationManager)getSystemService
                (Context.LOCATION_SERVICE);
        getLastLocation = locationManager.getLastKnownLocation
                (LocationManager.PASSIVE_PROVIDER);
        currentLongitude = getLastLocation.getLongitude();
        currentLatitude = getLastLocation.getLatitude();

        currentLocation = new LatLng(currentLatitude, currentLongitude);

        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 17);
        mMap.animateCamera(cameraUpdate);

        /*
        mMap.setInfoWindowAdapter(new PopupWindowAdapter(getLayoutInflater()));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        }
        */
    }



    public void addFoodLocation() throws IOException{
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new food");
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alert_xml = inflater.inflate(R.layout.alert_dialog, null);

        builder.setView(alert_xml);
        AlertDialog dialog = builder.create();

        final Spinner spinner = (Spinner) alert_xml.findViewById(R.id.food_spinner);
        spinner.setBackgroundColor(Color.WHITE);

        final EditText editText = (EditText) alert_xml.findViewById(R.id.info_box);
        editText.setTextColor(Color.BLACK);
        editText.setBackgroundColor(Color.rgb(230, 230, 230));

        spinnerArray = new String[] {"apple", "avocado", "blackberry",
        "cabbage", "cherry", "lemon", "oranges", "pear", "plum", "rosemary"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(adapter);

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Create",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Object spinner_item = spinner.getSelectedItem();

                        switch (spinner_item.toString()) {
                            case "apple":
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.apple)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "avocado":
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.avocado)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "blackberry":
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.blackberry)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "cabbage":
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.cabbage)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "cherry":
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.cherry)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "lemon":
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.lemon)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "oranges":
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.oranges)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "pear":
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.pear)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "plum":
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.plum)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "rosemary":
                                mMap.addMarker(new MarkerOptions().position(currentLocation).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.rosemary)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        dialog.show();

    }

    public void getAllFoodLocations() throws IOException{
        String line;
        String splitBy = ",";
        InputStream inputStream = getApplicationContext().getResources()
                .openRawResource(R.raw.values);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        while ((line = bufferedReader.readLine()) != null) {

            // use comma as separator
            String[] markerInfo  = line.split(splitBy);

            String name = markerInfo[0];
            Float lat = Float.parseFloat(markerInfo[1]);
            Float lng = Float.parseFloat(markerInfo[2]);
            String snip = markerInfo[3];
            String type = markerInfo[0].toLowerCase();

            mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(name)
                            .snippet(snip)
                            .icon(BitmapDescriptorFactory.fromResource(typeToResource(type)))
            );
        }
    }

    public int typeToResource(String type) {
        switch (type) {
            case "apple":
                return R.drawable.apple;
            case "avocado":
                return R.drawable.avocado;
            case "blackberry":
                return R.drawable.blackberry;
            case "cabbage":
                return R.drawable.cabbage;
            case "cherry":
                return R.drawable.cherry;
            case "lemon":
                return R.drawable.lemon;
            case "oranges":
                return R.drawable.oranges;
            case "pear":
                return R.drawable.pear;
            case "plum":
                return R.drawable.plum;
            case "rosemary":
                return R.drawable.rosemary;
        }
        return 0;
    }

    public void chooseWhereToAddFood(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new food");
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.make_here_or_there, null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();

        Button makeHere = (Button) view.findViewById(R.id.make_here);
        makeHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addFoodLocation();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        Button makeElsewhere = (Button) view.findViewById(R.id.make_elsewhere);
        makeElsewhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addElsewhere();
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    public void addElsewhere(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {

                Toast.makeText(getApplicationContext(),
                        point.latitude + ", " + point.longitude,
                        Toast.LENGTH_SHORT).show();


                addFoodLocation(point);


            }
        });

    }

    public void addFoodLocation(final LatLng point){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new food");
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alert_xml = inflater.inflate(R.layout.alert_dialog, null);

        builder.setView(alert_xml);
        AlertDialog dialog = builder.create();

        final Spinner spinner = (Spinner) alert_xml.findViewById(R.id.food_spinner);
        spinner.setBackgroundColor(Color.WHITE);

        final EditText editText = (EditText) alert_xml.findViewById(R.id.info_box);
        editText.setTextColor(Color.BLACK);
        editText.setBackgroundColor(Color.rgb(230,230,230));

        spinnerArray = new String[] {"apple", "avocado", "blackberry",
                "cabbage", "cherry", "lemon", "oranges", "pear", "plum", "rosemary"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(adapter);

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Create",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Object spinner_item = spinner.getSelectedItem();

                        switch (spinner_item.toString()) {
                            case "apple":
                                mMap.addMarker(new MarkerOptions().position(point).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.apple)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "avocado":
                                mMap.addMarker(new MarkerOptions().position(point).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.avocado)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "blackberry":
                                mMap.addMarker(new MarkerOptions().position(point).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.blackberry)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "cabbage":
                                mMap.addMarker(new MarkerOptions().position(point).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.cabbage)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "cherry":
                                mMap.addMarker(new MarkerOptions().position(point).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.cherry)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "lemon":
                                mMap.addMarker(new MarkerOptions().position(point).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.lemon)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "oranges":
                                mMap.addMarker(new MarkerOptions().position(point).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.oranges)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "pear":
                                mMap.addMarker(new MarkerOptions().position(point).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.pear)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "plum":
                                mMap.addMarker(new MarkerOptions().position(point).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.plum)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case "rosemary":
                                mMap.addMarker(new MarkerOptions().position(point).title(spinner_item.toString()).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.rosemary)).
                                        snippet(editText.getText().toString()));
                                Toast.makeText(getApplicationContext(), "Added an " + spinner_item.toString(),
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        dialog.show();
        mMap.setOnMapClickListener(null);
    }

    public void sortByItem(String item){

        mMap.clear();

        switch (item){
            case "All seasons":
                try {
                    getAllFoodLocations();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "Summer":
                try {
                    getSummerFoodLocation();
                } catch (IOException e){
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), item, Toast.LENGTH_SHORT);
        }
    }

    private void getSummerFoodLocation() throws IOException{
        String line;
        String splitBy = ",";
        InputStream inputStream = getApplicationContext().getResources()
                .openRawResource(R.raw.values);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        while ((line = bufferedReader.readLine()) != null) {

            // use comma as separator
            String[] markerInfo  = line.split(splitBy);

            String name = markerInfo[0];

            Float lat = Float.parseFloat(markerInfo[1]);
            Float lng = Float.parseFloat(markerInfo[2]);
            String snip = markerInfo[3];
            String type = markerInfo[0].toLowerCase();

            if(type == "lemon" || type == "apple") {

                mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(name)
                                .snippet(snip)
                                .icon(BitmapDescriptorFactory.fromResource(typeToResource(type)))
                );
            }
        }
    }
    public static class DebugExampleTwoFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_draw, container, false);
        }
    }

}
