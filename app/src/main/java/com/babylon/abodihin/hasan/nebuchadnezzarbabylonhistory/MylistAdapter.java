package com.babylon.abodihin.hasan.nebuchadnezzarbabylonhistory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class MylistAdapter extends ArrayAdapter<Place> {

    Context Homecontext;
    int layout;
    List<Place> places;

    String placeId;
    String placeTitle;
    String placeDescription;

    ImageView placeImageHolder;
    TextView placeIdHolder;
    TextView placeTitleHolder;
    Button more_btn;

    // instance the class
    public MylistAdapter(Context context, int resource, List<Place> listData) {
        super(context, resource, listData);
        // retrieve the received parameters and store them local parameters.
        Homecontext = context;
        layout = resource;
        places = listData;
    }
    // this method called each time a new item added to displays in the user screen
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // inflate the layout
            convertView = LayoutInflater.from(getContext()).
                    inflate(layout, parent, false);
        }

        // the getView method return the position of the current item in the list.
        // use get method to get a place object from the list
        final Place place = places.get(position);
        // retrieve the data from the place object
        placeId = place.getPlaceId();
        placeTitle = place.getPlaceTitle();
        placeDescription = place.getPlaceDescription();

        // reference the imageView and TextView in the xml layout
        placeImageHolder = (ImageView) convertView.findViewById(R.id.image_photo);
        placeIdHolder = (TextView) convertView.findViewById(R.id.placeIdHolder);
        placeTitleHolder = (TextView) convertView.findViewById(R.id.placeTitle);

        //assign the data to the views
        placeIdHolder.setText(place.getPlaceId());
        placeTitleHolder.setText(placeTitle);
        placeImageHolder.setScaleType(ImageView.ScaleType.FIT_XY);
        placeImageHolder.setImageBitmap(place.getPlaceBitmap());

        // set a listener to the button
        more_btn = (Button) convertView.findViewById(R.id.more_btn);
        final View finalConvertView = convertView;
        more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the id of the place which has been saved in a hidden TextView.
                TextView tv = (TextView) finalConvertView.findViewById(R.id.placeIdHolder);
                String p = tv.getText().toString();
                // create Intent object
                Intent intent = new Intent(Homecontext, MoreActivity.class);
                // used to pass the place id to the next activity
                intent.putExtra("placeId", p);
                // direct user to the MoreActivity
                Homecontext.startActivity(intent);
            }
        });
        return convertView;
    }
}


