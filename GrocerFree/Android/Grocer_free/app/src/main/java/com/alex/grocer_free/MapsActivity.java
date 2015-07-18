package com.alex.grocer_free;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity{
    Location getLastLocation;
    double currentLongitude;
    double currentLatitude;
    LatLng currentLocation;
    LocationManager locationManager;
    public GoogleMap mMap;

    LocalDatabase db;
    Polyline polyline;

    String[] item_list = new String[] {"lemon", "apple", "orange"};

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView add_image;
    ImageView imageOf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        db = new LocalDatabase(this);
        this.getActionBar().hide();
        setUpMapIfNeeded();

        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //TODO query db for latlng position and return info in drawerfragment

                marker.setSnippet(null);
                final LatLng start = currentLocation;
                final LatLng end = marker.getPosition();

                final Routing routing = new Routing(Routing.TravelMode.WALKING);

                RoutingListener routingListener = new RoutingListener() {
                    @Override
                    public void onRoutingFailure() {

                    }

                    @Override
                    public void onRoutingStart() {

                    }

                    @Override
                    public void onRoutingSuccess(PolylineOptions polylineOptions, Route route) {

                        if(polyline!=null)
                            polyline.remove();
                        polyline=null;
                        PolylineOptions polyOptions = new PolylineOptions();
                        polyOptions.color(Color.BLACK);
                        polyOptions.width(10);
                        polyOptions.addAll(polylineOptions.getPoints());
                        polyline=mMap.addPolyline(polyOptions);
                    }
                };
                routing.registerListener(routingListener);
                routing.execute(start, end);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);

                Bundle bundle = new Bundle();
                bundle.putDouble("lat", marker.getPosition().latitude);
                bundle.putDouble("lng", marker.getPosition().longitude);
                bundle.putDouble("currentLat", currentLatitude);
                bundle.putDouble("currentLng", currentLongitude);

                DrawerFragment drawerFragment = new DrawerFragment();
                drawerFragment.setArguments(bundle);

                transaction.add(R.id.drawer_container, drawerFragment).commit();
                return false;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            //Add item to map
            @Override
            public void onMapClick(final LatLng latLng) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                //builder.setTitle("Add new marker");
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View add_marker = inflater.inflate(R.layout.add_marker, null);

                builder.setView(add_marker);
                AlertDialog dialog = builder.create();

                final Spinner spinner = (Spinner) add_marker.findViewById(R.id.item_spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, item_list);
                spinner.setAdapter(adapter);

                final ImageButton addImage = (ImageButton) add_marker.findViewById(R.id.addImage);
                addImage.setImageResource(R.drawable.ic_image_black_24dp);

                imageOf = (ImageView) add_marker.findViewById(R.id.imageOf);
                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchTakePictureIntent();
                    }
                });

                final EditText item_description = (EditText) add_marker.findViewById(R.id.item_description);
                item_description.setTextColor(Color.BLACK);

                dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Create",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String desc = item_description.getText().toString();
                                String item = spinner.getSelectedItem().toString();

                                if(imageOf != null){
                                    Bitmap bitmap = ((BitmapDrawable)imageOf.getDrawable()).getBitmap();
                                    byte[] imgByteArray = getBytes(bitmap);
                                    addNewItemToMap(latLng, item, desc, imgByteArray);
                                }

                                addNewItemToMap(latLng, item, desc);
                            }
                        });
                dialog.show();
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
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
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

        populateMapFromDatabase();

    }

    private void populateMapFromDatabase() {
        ArrayList<Item> items = db.getAllItems();
        for(Item item : items){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(item.getItemType());
            markerOptions.position(new LatLng(item.getLat(), item.getLng()));
            markerOptions.snippet(item.getDesc());
            markerOptions.icon(chooseMarkerIcon(item.getItemType()));
            mMap.addMarker(markerOptions);
        }

    }

    public void addDrawerFragment(){
        if (findViewById(R.id.drawer_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.


            // Create a new Fragment to be placed in the activity layout
            DrawerFragment firstFragment = new DrawerFragment();


            // Add the fragment to the 'fragment_container' FrameLayout

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
            transaction.add(R.id.drawer_container, firstFragment).commit();


        }

    }

    public void addNewItemToMap(LatLng latLng, String title, String description){

        Item item = new Item();
        item.setLat(latLng.latitude);
        item.setLng(latLng.longitude);
        item.setItemType(title);
        item.setDesc(description);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(title);
        //TODO custom drawable
        markerOptions.icon(chooseMarkerIcon(title));
        mMap.addMarker(markerOptions);
        //TODO insert into db

        db.insertItem(item);

    }

    public void addNewItemToMap(LatLng latLng, String title, String description, byte[] byteArray){

        Item item = new Item();
        item.setLat(latLng.latitude);
        item.setLng(latLng.longitude);
        item.setItemType(title);
        item.setDesc(description);
        item.setImage(byteArray);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(title);
        //TODO custom drawable
        markerOptions.icon(chooseMarkerIcon(title));
        mMap.addMarker(markerOptions);
        //TODO insert into db

        db.insertItem(item);

    }

    public BitmapDescriptor chooseMarkerIcon(String title){

        int id = getResources().getIdentifier(title, "drawable", getPackageName());
        Drawable drawable = getResources().getDrawable(id);

        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
        return icon;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageOf.setImageBitmap(imageBitmap);

        }
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap

}
