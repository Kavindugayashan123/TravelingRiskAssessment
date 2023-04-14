package lk.kavi.travelapp.listener;

import android.location.Location;
import android.location.LocationListener;

import lk.kavi.travelapp.HotelListActivity;

public class MyLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location loc) {

        String longitude = "Longitude: " + loc.getLongitude();
        String latitude = "Latitude: " + loc.getLatitude();

        System.out.println(latitude);
        System.out.println(longitude);

//        HotelListActivity.
    }

}
