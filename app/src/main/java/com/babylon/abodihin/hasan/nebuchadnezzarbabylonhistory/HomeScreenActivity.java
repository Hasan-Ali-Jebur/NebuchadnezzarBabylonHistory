package com.babylon.abodihin.hasan.nebuchadnezzarbabylonhistory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {
    public List<Place> listOfPlaces = new ArrayList<>();
    private String imageURL = "http://www.computing.northampton.ac.uk/~13432616/BabylonHistory/images/";
    Place place;
    String placeId;
    String placeTitle;
    String placeDescription;
    String placeImage;
    double placeLat;
    double placeLong;
    Bitmap bitmap;
    ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // set the content layout of the activity.
        setContentView(R.layout.activity_home_screen);
        // get reference to the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        // add the toolbar.
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        new Thread() {
            @Override
            public void run() {
                // instantiate the  Http connection class to get the h object
                HttpConnection h = new HttpConnection();
                // call the setmURL method and pass the url to it.
                h.setmURL("http://www.computing.northampton.ac.uk/~13432616/BabylonHistory/PlaceInfo.php");
                // specify the request method by calling the setMethod function
                h.setMethod("POST");
                // set the requests parameters in this case type is all because all places need to be displayed.
                h.setParameters("type", "all");
                // declare a string veriable which it get its data by calling the getData method from the httpConnection class
                final String content = h.getData();
                // parse the returned response by using parseData method and then save the data to a list Array
                listOfPlaces = parseData(content);
                // check if the List is not empty
                if (listOfPlaces != null) {
                    // foreach loop block used to through each place object in the list
                    for (Place place : listOfPlaces) {
                        try {
                            // get the image name from the place object
                            String imageName = place.getPlaceImage();
                            // add the image url to the image name to get a full image url
                            String fullImageURL = imageURL + imageName;
                            // download the image using input stream and getContent method
                            InputStream in = (InputStream) new URL(fullImageURL).getContent();
                            // decode the stream to bitmap
                            bitmap = BitmapFactory.decodeStream(in);
                            // set the bitmap to the current place object
                            place.setPlaceBitmap(bitmap);
                            // close the input stream
                            in.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                runOnUiThread(new Thread(new Runnable() {
                    public void run() {
                        // create a reference to the list view
                        ListView lv = (ListView) findViewById(R.id.lv);
                        // instantiate the MylistAdapter class and pass the HomeScreenActivity as the context, the layout and the array list
                        MylistAdapter adapter = new MylistAdapter(HomeScreenActivity.this, R.layout.row_layout, listOfPlaces);
                        if (lv != null) {
                            // remove the divider line from the list view
                            lv.setDivider(null);
                            // set the adapter to the list view
                            lv.setAdapter(adapter);
                        }
                        // hide the progress bar
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }));

            }
        }.start();

    }
    // this method used to parse the json data and return the result as list of place objects
    private List<Place> parseData(String content) {
        // define a list type Place and name as list
        List<Place> list = new ArrayList<>();
        try {
            // get the json array from the text
            JSONArray jsonArray = new JSONArray(content);
            // loop through the array
            for (int i = 0; i < jsonArray.length(); ++i) {
                // get the json object from the json array
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // get the place id, title, description, lat, long and image name from the json object
                placeId = jsonObject.getString("id");
                placeTitle = jsonObject.getString("title");
                placeDescription = jsonObject.getString("description");
                placeLat = jsonObject.getDouble("lat");
                placeLong = jsonObject.getDouble("long");
                placeImage = jsonObject.getString("image");
                // create object from the class Place
                place = new Place();
                // set the data to the variables in the Place class
                place.setPlaceId(placeId);
                place.setPlaceTitle(placeTitle);
                place.setPlaceDescription(placeDescription);
                place.setPlaceLat(placeLat);
                place.setPlaceLong(placeLong);
                place.setPlaceImage(placeImage);
                // add the Place object to the list
                list.add(place);
            }
            // return the list
            return list;
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu layout
        getMenuInflater().inflate(R.menu.home_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // get the ID of the selected item.
        int id = item.getItemId();
        // check whether the selected item id is equal to showOnMap item id
        if (id == R.id.showOnMap) {
            // create intent object and start the MapsActivity
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
