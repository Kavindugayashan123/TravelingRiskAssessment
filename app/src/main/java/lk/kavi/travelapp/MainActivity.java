package lk.kavi.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import lk.kavi.travelapp.adapter.PoliceListAdapter;
import lk.kavi.travelapp.model.Police;
import lk.kavi.travelapp.model.currentweather.Main;
import lk.kavi.travelapp.utils.ApiClient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity  implements LocationListener {

    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    String userCountry, userAddress;
    ListView simpleList;
    List<Police> policeList;

    List<Police> myList;

    Button btnRA,btnW,btnTF,btnCP,btnL,btnEm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getNearPoliceList();
        locationPerm();
        myList = new ArrayList<>();

        btnRA = findViewById(R.id.btnRA);
        btnW = findViewById(R.id.btnW);
        btnTF = findViewById(R.id.btnTF);
        btnCP = findViewById(R.id.btnCP);
        btnL = findViewById(R.id.btnL);
        btnEm = findViewById(R.id.btnEM);

        btnRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  startActivity(new Intent(MainActivity.this, UploadActivity.class));
            }
        });

        btnW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WeatherActivity.class));
            }
        });

        btnTF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FriendsActivity.class));
            }
        });
        btnCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PoliceListActivity.class));
            }
        });
        btnL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HotelListActivity.class));
            }
        });
        btnEm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myList!=null){

                    if(myList.size()>0){

                        Intent intent = new Intent(MainActivity.this, PoliceActivity.class);
                        String data = new Gson().toJson(myList.get(0));
                        intent.putExtra("data",data);
                        startActivity(intent);
                    }
                }else {
                    Toast.makeText(MainActivity.this, "No Police Station Available", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location loc) {

        longitude =  loc.getLongitude();
        latitude = loc.getLatitude();

        System.out.println(latitude);
        System.out.println(longitude);


    }
    private void getNearPoliceList() {

        DatabaseReference dbRef = ApiClient.getDBRef();

        DatabaseReference hotels = dbRef.child("police");

        hotels.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                /* This method is called once with the initial value and again whenever data at this location is updated.*/
                long value=dataSnapshot.getChildrenCount();
                GenericTypeIndicator<List<Police>> genericTypeIndicator =new GenericTypeIndicator<List<Police>>(){};


                policeList=dataSnapshot.getValue(genericTypeIndicator);

                int size = policeList.size();
                for(int i = 0; i< size; i++){
                    Police police = policeList.get(i);
                    police.setImgBitmap(getBitmapFromURL(police.getImg()));

                    double v = checkDistance(police);
                    System.out.println("sss");
                    System.out.println(v);
                    police.setRange(Math.round(v * 100.0) / 100.0+" KM");

                    if(v<50){
                        myList.add(police);
                    }

                }

                System.out.println("hotelList.size()");
                System.out.println(size);
            }

            @Override
            public void onCancelled(DatabaseError error){
                // Failed to read value
                Log.w(TAG,"Failed to read value.",error.toException());
            }
        });

    }
    private void locationPerm() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        try {

            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_loc != null) {
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else if (network_loc != null) {
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else {
            latitude = 0.0;
            longitude = 0.0;
        }


        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);

        try {

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                userCountry = addresses.get(0).getCountryName();
                userAddress = addresses.get(0).getAddressLine(0);
//                tv.setText(userCountry + ", " + userAddress);
            }
            else {
                userCountry = "Unknown";
//                tv.setText(userCountry);
            }

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 4000, 1f, this);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private double checkDistance(Police hotel) {

        Location startPoint=new Location("locationA");
        startPoint.setLatitude(latitude);
        startPoint.setLongitude(longitude);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(hotel.getLat());
        endPoint.setLongitude(hotel.getLon());
        double distance=startPoint.distanceTo(endPoint);
        return distance/1000;
    }
}