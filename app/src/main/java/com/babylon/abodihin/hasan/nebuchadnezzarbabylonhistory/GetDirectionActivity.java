package com.babylon.abodihin.hasan.nebuchadnezzarbabylonhistory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetDirectionActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    String placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_direction);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // get a reference to the floating Action Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // set a click listener to trigger gotToLocation function.
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToLocation();
                }
            });
        }
        // get a reference to the Map fragment.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.myDirectionMap);
        mapFragment.getMapAsync(this);
        // get the place id which was passed to this activity by the intent
        //placeId = getIntent().getStringExtra("placeId");
    }

    // these function used to inflate the main_menu and sent a listener ti the back button.
    // back button direct user to the previous visited activity.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MoreActivity.class);
            intent.putExtra("placeId", placeId);
            startActivity(intent);
        }
        return true;
    }

    // this method called when the map i ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // get a reference to the map object.
        mMap = googleMap;

        // this code block used to hide the location default google map ui elements in particular the location button and the toolbar.
        // we are going to design our own location button.
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setMapToolbarEnabled(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // this is used to show the current location of the user on the map as a blue dot.
        mMap.setMyLocationEnabled(true);

        // block code used to connected to Google APIs using Google client class.
        // connecting to location services will allow the application to get the current location of the user in a latitude/longitude format.
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    // add the location API. Note we can add as many as APIs we would like our app to connect to.
                    .addApi(LocationServices.API)
                    .enableAutoManage(this, this)
                    .build();
        }
        googleApiClient.connect();
    }

    // this method will be called once the google client connect to the google API services.
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // line of code gets the location of the user and stores it to a location object using fused location API and it method get last location.
        // location is a class which will contains all the information about the user current location.
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {

            // get the latitude and longitude from the location object and store in a double variables which represents the current latitude and longitude of the user location.
            // get the place's latitude and longitude which have been passed to this activity through a intent object.
            double userLat = location.getLatitude();
            double userLong = location.getLongitude();
            double placeLat = getIntent().getDoubleExtra("placeLat", 0.0);
            double placeLong = getIntent().getDoubleExtra("placeLong", 0.0);

            // create a LatLng object using latitude and longitude for both locations.
            LatLng userCurrentLocation = new LatLng(userLat, userLong);
            LatLng placeLocation = new LatLng(placeLat, placeLong);

            // add marker to the map shows the place's location
            Marker placeMarker = mMap.addMarker(new MarkerOptions()
                    .position(placeLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pinlast)));
            placeMarker.showInfoWindow();
            // move camera to the user's current location.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrentLocation, 17));
            // call this method which will draw a line between the two locations.
            drawThePathAndGetDistanceAndTime(String.valueOf(userLat), String.valueOf(userLong), String.valueOf(placeLat), String.valueOf(placeLong));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void drawThePathAndGetDistanceAndTime(final String uLat, final String uLng, final String pLat, final String pLng) {
        new Thread() {
            @Override
            public void run() {

                // send an HTTP request to google directions api to get the direction information between the user and a place.
                HttpConnection h = new HttpConnection();
                h.setmURL("https://maps.googleapis.com/maps/api/directions/json?");
                h.setMethod("GET");
                h.setParameters("origin", uLat + "," + uLng);
                h.setParameters("destination", pLat + "," + pLng);
                h.setParameters("mode", "walking");
                h.setParameters("key", "AIzaSyDRucTDL3dsVxZQYZAGwJ_xA7WekVdHhR0");
                final String content = h.getData();

                runOnUiThread(new Thread(new Runnable() {
                    public void run() {
                        try {
                            // get the json object from the returned text
                            final JSONObject mainObject = new JSONObject(content);
                            // get all routes from route array
                            JSONArray routeArray = mainObject.getJSONArray("routes");
                            // get the first route
                            JSONObject routesObject = routeArray.getJSONObject(0);
                            // take all legs from the route
                            JSONArray legs = routesObject.getJSONArray("legs");
                            // grab the first leg
                            JSONObject leg = legs.getJSONObject(0);

                            JSONObject durationObject = leg.getJSONObject("duration");
                            String duration = durationObject.getString("text");
                            JSONObject distanceObject = leg.getJSONObject("distance");
                            String distance = distanceObject.getString("text");

                            // take all overview polyline from the route
                            JSONObject overviewPolylinesObject = routesObject.getJSONObject("overview_polyline");
                            // get the string points
                            String encodedString = overviewPolylinesObject.getString("points");
                            System.out.println(encodedString);
                            // get the encoded result which is latLng info and then save to latLing list.
                            List<LatLng> pointsList = decodePoly(encodedString);
                            // draw all points
                            Polyline line = mMap.addPolyline(new PolylineOptions()
                                            .addAll(pointsList)
                                            .width(15)
                                            .color(Color.BLUE)

                            );
                            TextView distanceTv = (TextView) findViewById(R.id.distanceTv);
                            if (distanceTv != null) {
                                distanceTv.setText(duration + " (" + distance + ")");
                            }

                        } catch (JSONException e) {

                        }
                    }
                }));

            }
        }.start();
    }
    // this method used to decode ploy lines which received from google api to latitude and longitude points
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    // this method simply get the current location of user using fused location api and then animate the camera to that location.
    private void goToLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // get the current location
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            // animate the camera
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
    }
}
