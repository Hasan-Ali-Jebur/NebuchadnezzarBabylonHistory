package com.babylon.abodihin.hasan.nebuchadnezzarbabylonhistory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public List<Place> listOfPlaces = new ArrayList<>();
    private String imageURL = "http://www.computing.northampton.ac.uk/~13432616/BabylonHistory/";
    String placeId;
    String placeTitle;
    String placeDescription;
    double placeLat;
    double placeLong;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        new Thread() {
            @Override
            public void run() {
                // send http post request to download place information from the web service.
                HttpConnection h = new HttpConnection();
                h.setmURL("http://www.computing.northampton.ac.uk/~13432616/BabylonHistory/PlaceInfo.php");
                h.setMethod("POST");
                // requesting all data
                h.setParameters("type", "all");
                // get the JSON string
                final String content = h.getData();
                // parse data and assign the result to list
                listOfPlaces = parseData(content);

                runOnUiThread(new Thread(new Runnable() {
                    public void run() {
                        // call the map
                        mapFragment.getMapAsync(MapsActivity.this);
                    }
                }));

            }
        }.start();


    }


    private List<Place> parseData(String content) {
        List<Place> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); ++i) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String placeId = jsonObject.getString("id");
                String placeTitle = jsonObject.getString("title");
                String placeDescription = jsonObject.getString("description");
                double placeLat = jsonObject.getDouble("lat");
                double placeLong = jsonObject.getDouble("long");
                String placeImage = jsonObject.getString("image");

                Place place = new Place();
                place.setPlaceId(placeId);
                place.setPlaceTitle(placeTitle);
                place.setPlaceDescription(placeDescription);
                place.setPlaceLat(placeLat);
                place.setPlaceLong(placeLong);
                place.setPlaceImage(placeImage);
                list.add(place);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (Place places : listOfPlaces
                ) {
            placeId = places.getPlaceId();
            placeTitle = places.getPlaceTitle();
            placeDescription = places.getPlaceDescription();
            bitmap = places.getPlaceBitmap();
            placeLat = places.getPlaceLat();
            placeLong = places.getPlaceLong();

            LatLng placeLocation = new LatLng(placeLat, placeLong);
            mMap.addMarker(new MarkerOptions()
                    .position(placeLocation)
                    .title(placeId)
                    .snippet(placeTitle + ";" + placeDescription)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pinnew)));
        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // inflate the xml layout
                View v = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                // get reference to the UI elements.
                TextView title = (TextView) v.findViewById(R.id.placesTitleTV);
                TextView Des = (TextView) v.findViewById(R.id.placesDescriptionTV);
                // get the text which was saved in the snippet field. split the text into two values depending on the ; and save the result in a string array.
                String[] T = marker.getSnippet().split(";");
                // set the values to textview
                title.setText(T[0]);
                Des.setText(T[1]);
                return v;
            }
        });

        // set a click listener to the info window
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                // get the place id from the title field in the clicked marker.
                String Id = marker.getTitle();
                // direct user to moreActivity and pass the id as string
                Intent intent = new Intent(MapsActivity.this,MoreActivity.class);
                intent.putExtra("placeId" , Id);
                System.out.println();
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, HomeScreenActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
