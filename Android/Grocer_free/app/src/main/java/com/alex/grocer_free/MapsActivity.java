package com.alex.grocer_free;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.software.shell.fab.ActionButton;

import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class MapsActivity extends FragmentActivity{
    Location getLastLocation;
    double currentLongitude;
    double currentLatitude;
    LatLng currentLocation;
    LocationManager locationManager;
    public GoogleMap mMap;
    DrawerFragment drawerFragment;
    FragmentTransaction transaction;
    LocalDatabase db;
    Polyline polyline;
    ActionButton fab;

    String[] item_list = new String[] {"lemon", "apple", "orange"};

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView add_image;
    ImageView imageOf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getActionBar().hide();

        db = new LocalDatabase(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ChUKpoLVli5C5y8oQfv4EmLILIXku8KpXjMmqCzG", "6VFpmrRjxRudE572ep15xk7j9ioBJ19KKTFWl4rw");

        fab = (ActionButton) findViewById(R.id.action_button);

        setUpMapIfNeeded();

        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(drawerFragment != null){
                    removeDrawerFrag();
                }
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if(drawerFragment != null){
                    removeDrawerFrag();
                }
                if(marker.getTitle().equals("Current Location")){
                    marker.showInfoWindow();
                    return false;
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putDouble("currentLat", marker.getPosition().latitude);
                    bundle.putDouble("currentLng", marker.getPosition().longitude);
                    addDrawerFragment(bundle);

                    //Routing
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
                            if (polyline != null)
                                polyline.remove();
                            polyline = null;
                            PolylineOptions polyOptions = new PolylineOptions();
                            polyOptions.color(Color.BLACK);
                            polyOptions.width(10);
                            polyOptions.addAll(polylineOptions.getPoints());
                            polyline = mMap.addPolyline(polyOptions);
                        }
                    };
                    routing.registerListener(routingListener);
                    routing.execute(start, end);

                    return false;
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerFragment != null){
                    removeDrawerFrag();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                //builder.setTitle("Add new marker");
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View add_marker = inflater.inflate(R.layout.add_marker, null);

                builder.setView(add_marker);
                AlertDialog dialog = builder.create();

                final MaterialSpinner spinner = (MaterialSpinner) add_marker.findViewById(R.id.spinner);

                String[] ITEMS = {"apple", "lemon", "orange"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, ITEMS);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                                //Create and save object in Parse database
                                if (imageOf.getDrawable() != null) {
                                    String desc = item_description.getText().toString();
                                    String item = spinner.getSelectedItem().toString();
                                    Bitmap bitmap = ((BitmapDrawable) imageOf.getDrawable()).getBitmap();
                                    byte[] imgByteArray = Utils.getBytes(bitmap);
                                    //addNewItemToMap(currentLocation, item, desc, imgByteArray);

                                    ParseFile imgFile = new ParseFile("img.bmp", imgByteArray);
                                    imgFile.saveInBackground();

                                    ParseObject fruit = new ParseObject("TestObject");
                                    fruit.put("fruitType", item);
                                    fruit.put("description", desc);
                                    fruit.put("LatLng", new ParseGeoPoint(currentLatitude,currentLongitude));
                                    fruit.put("images", imgFile);

                                    fruit.saveInBackground();
                                    addNewItemToMap(new LatLng(currentLatitude, currentLongitude), item, desc);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Please add an image", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                dialog.show();
            }
        });

        LinearLayout toolbar = (LinearLayout) findViewById(R.id.toolbar);

        RelativeLayout settings = (RelativeLayout) toolbar.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        RelativeLayout point = (RelativeLayout) toolbar.findViewById(R.id.point);
        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerFragment != null){
                    removeDrawerFrag();
                }
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 17);
                mMap.animateCamera(cameraUpdate);
            }
        });
        RelativeLayout search = (RelativeLayout) toolbar.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout l = (LinearLayout) findViewById(R.id.textbox);
                l.setVisibility(View.VISIBLE);

                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) l.findViewById(R.id.search_bar);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (getApplicationContext(),android.R.layout.simple_list_item_1, item_list);

                autoCompleteTextView.setThreshold(1);
                autoCompleteTextView.setAdapter(adapter);

                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String fruitToSearch = parent.getItemAtPosition(position).toString();

                        mMap.clear();
                        populateMapFromDatabase(fruitToSearch);

                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    }
                });
            }
        });
        RelativeLayout profile = (RelativeLayout) toolbar.findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("TestObject");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> markers, ParseException e) {
                if (e == null) {
                    for (ParseObject x : markers) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(x.getString("fruitType"));
                        LatLng pos = new LatLng(x.getParseGeoPoint("LatLng").getLatitude(),
                                x.getParseGeoPoint("LatLng").getLongitude());
                        markerOptions.position(pos);
                        markerOptions.icon(chooseMarkerIcon(x.getString("fruitType")));
                        mMap.addMarker(markerOptions);
                    }
                } else {
                    // handle Parse Exception here
                }
            }
        });

    }

    private void populateMapFromDatabase(String fruitType){
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("TestObject");
        query.whereEqualTo("fruitType", fruitType);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> markers, ParseException e) {
                if (e == null) {
                    for (ParseObject x : markers){
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(x.getString("fruitType"));
                        LatLng pos = new LatLng(x.getParseGeoPoint("LatLng").getLatitude(),
                                x.getParseGeoPoint("LatLng").getLongitude());
                        markerOptions.position(pos);
                        markerOptions.icon(chooseMarkerIcon(x.getString("fruitType")));
                        mMap.addMarker(markerOptions);
                    }
                } else {
                    // handle Parse Exception here
                }
            }
        });
    }

    public void addDrawerFragment(Bundle bundle){
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);

        drawerFragment = new DrawerFragment();
        drawerFragment.setArguments(bundle);
        transaction.add(R.id.drawer_container, drawerFragment).commit();
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

    public void removeDrawerFrag(){
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        transaction.remove(drawerFragment).commit();
    }
}
