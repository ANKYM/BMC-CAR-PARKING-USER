package com.omkar.bmccarparkinguser.Activity;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.omkar.bmccarparkinguser.Model.ParkingSpot;
import com.omkar.bmccarparkinguser.R;

/**
 * Created by Yogesh on 01-02-2018.
 */

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter  {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.info_window, null);

        TextView title_tv = view.findViewById(R.id.title);
        TextView parking_space_tv = view.findViewById(R.id.tv_parking_space);
        TextView address_tv = view.findViewById(R.id.tv_address);
        Button route_button = view.findViewById(R.id.button_route);
        Button button_book = view.findViewById(R.id.button_book);

        ParkingSpot parkingSpot = (ParkingSpot) marker.getTag();
        title_tv.setText(parkingSpot.getSpotName());
        parking_space_tv.setText(parkingSpot.getParkCapicity()+"");
        address_tv.setText(parkingSpot.getAddress());


        route_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Route Clicked",Toast.LENGTH_LONG);
            }
        });
        button_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Book Clicked",Toast.LENGTH_LONG);
            }
        });
        return view;
    }
}
