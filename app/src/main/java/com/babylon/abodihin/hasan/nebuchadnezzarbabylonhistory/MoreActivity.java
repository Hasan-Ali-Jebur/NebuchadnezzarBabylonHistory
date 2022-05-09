package com.babylon.abodihin.hasan.nebuchadnezzarbabylonhistory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MoreActivity extends AppCompatActivity implements OnMapReadyCallback {
    SupportMapFragment mapFragment;
    GoogleMap mMap;
    String clickedId;
    String placeId;
    String placeTitle;
    String placeDescription;
    double placeLat;
    double placeLong;
    String placeImage;
    Bitmap bitmap;
    private String imageURL = "http://www.computing.northampton.ac.uk/~13432616/BabylonHistory/images/";

    TextView placeTitleView;
    TextView placeDesView;
    String address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.myMap);

        mapFragment.getMapAsync(MoreActivity.this);

        clickedId = getIntent().getStringExtra("placeId");

        new Thread() {
            @Override
            public void run() {

                // send http post request to get data about a particular place.
                HttpConnection h = new HttpConnection();
                h.setmURL("http://www.computing.northampton.ac.uk/~13432616/BabylonHistory/PlaceInfo.php");
                h.setMethod("POST");
                h.setParameters("type", clickedId);
                // get JSON text
                final String content = h.getData();
                // parse data
                parseData(content);

                try {
                    // setup the image URL
                    String fullImageURL = imageURL + placeImage;
                    // get the stream
                    InputStream in = (InputStream) new URL(fullImageURL).getContent();
                    // convert stream to bitmap
                    bitmap = BitmapFactory.decodeStream(in);
                    // close stream
                    in.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Thread(new Runnable() {
                    public void run() {
                        // get references to the UI elements.
                        ImageView imageView = (ImageView) findViewById(R.id.placeImageView);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        imageView.setImageBitmap(bitmap);
                        // assign the data to the UI elements
                        placeTitleView = (TextView) findViewById(R.id.placeTitleCustomView);
                        placeTitleView.setText(placeTitle);
                        placeDesView = (TextView) findViewById(R.id.placeDesView);
                        placeDesView.setMovementMethod(new ScrollingMovementMethod());
                        placeDesView.setText(placeDescription);

                    }
                }));

            }
        }.start();


        FloatingActionButton getDirectionBtn = (FloatingActionButton) findViewById(R.id.get_direction);
        if (getDirectionBtn != null) {
            getDirectionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MoreActivity.this, GetDirectionActivity.class);
                    intent.putExtra("placeId", clickedId);
                    intent.putExtra("placeLat", placeLat);
                    intent.putExtra("placeLong", placeLong);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu layout and added to the activity
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // get the selected item id
        int id = item.getItemId();
        // check if the selected item id is equal to home
        if (id == android.R.id.home) {
            // create intent to redirect the user to the HomeScreenActivity
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // create object to the Geocoder class
        Geocoder gc = new Geocoder(this, Locale.getDefault());

        try {
            // get the address from latitude and longitude and save the result in a list of Addresses.
            List<Address> list = gc.getFromLocation(placeLat, placeLong, 1);
            if (list.size() > 0) {
                // takes the first address from the list
                Address add = list.get(0);
                // take the road name, city, country name, postal code
                String roadNmae = add.getAddressLine(0);
                String city = add.getLocality();
                String countryName = add.getCountryName();
                String postalCode = add.getPostalCode();
                // combine the result in a single address variable.
                address = roadNmae + ", " + city + ", " + postalCode + ", " + countryName;


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create a LatLing object represents the location of a place.
        LatLng placeLocation = new LatLng(placeLat, placeLong);
        // add marker
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(placeLocation)
                .title(address)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pinnew))
        );

        // move camera to place location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15));
        // show info window
        marker.showInfoWindow();
    }

    private void parseData(String content) {
        try {
            // get the JSON array
            JSONArray jsonArray = new JSONArray(content);
            // get JSON object 0
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            // get data about from the object
            placeTitle = jsonObject.getString("title");
            placeDescription = jsonObject.getString("description");
            placeLat = jsonObject.getDouble("lat");
            placeLong = jsonObject.getDouble("long");
            placeImage = jsonObject.getString("image");

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
}
