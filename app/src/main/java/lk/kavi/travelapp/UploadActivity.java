package lk.kavi.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.reactivex.annotations.NonNull;
import lk.kavi.travelapp.adapter.PoliceListAdapter;
import lk.kavi.travelapp.model.Police;
import lk.kavi.travelapp.model.RiskData;
import lk.kavi.travelapp.utils.ApiClient;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class UploadActivity extends AppCompatActivity  implements LocationListener {
    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;
    String userCountry, userAddress;

    LinearLayout data1,data2;

    ImageView uploadImage;

    Button uploadBtn;
    TextView placeName,deathCount,riskPtg,placeDesc;

    boolean isUploaded = false;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        locationPerm();

        placeName = findViewById(R.id.placeName);
        deathCount = findViewById(R.id.deathCount);
        riskPtg = findViewById(R.id.riskPtg);
        placeDesc = findViewById(R.id.placeDesc);

        uploadImage = findViewById(R.id.uploadImage);
        uploadBtn = findViewById(R.id.uploadBtn);
        data1 = findViewById(R.id.data1);
        data2 = findViewById(R.id.data2);


        uploadBtn.setOnClickListener(view -> {

            if(!isUploaded){
                Toast.makeText(UploadActivity.this,
                        "Please Upload Photo",
                        Toast.LENGTH_LONG).show();
                return;
            }
            showLoadingDialog();
            DatabaseReference dbRef = ApiClient.getDBRef();

            DatabaseReference riskdata = dbRef.child("riskdata");
            riskdata.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    /* This method is called once with the initial value and again whenever data at this location is updated.*/
                    GenericTypeIndicator<List<RiskData>> genericTypeIndicator =new GenericTypeIndicator<List<RiskData>>(){};

                    RiskData data = new RiskData();
                    List<RiskData> dataList=dataSnapshot.getValue(genericTypeIndicator);

                    int size = dataList.size();
                    for(int i = 0; i< size; i++){
                        RiskData riskData = dataList.get(i);
                        double v = checkDistance(riskData);

                        if(v<50){
                            data = riskData;
                            break;
                        }
                    }

                    placeDesc.setText(data.getDescription());
                    deathCount.setText("Death Count : "+data.getDeath()+"");
                    riskPtg.setText(data.getRisk_pg());
                    placeName.setText(data.getName());

                    data1.setVisibility(View.VISIBLE);
                    data2.setVisibility(View.VISIBLE);
                    dismissLoadingDialog();


                }

                @Override
                public void onCancelled(DatabaseError error){
                    // Failed to read value
                    Log.w(TAG,"Failed to read value.",error.toException());
                    dismissLoadingDialog();
                }
            });


        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED){
                    Toast.makeText(UploadActivity.this, "camera permission denied", Toast.LENGTH_LONG).show();
                    return;
                }else {
                    openCamera();
                }
            }
        });

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static final int MY_CAMERA_REQUEST_CODE = 100;

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                uploadImage.setImageBitmap(photo);
                isUploaded = true;
            }

        }

    }

    private double checkDistance(RiskData hotel) {

        Location startPoint=new Location("locationA");
        startPoint.setLatitude(latitude);
        startPoint.setLongitude(longitude);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(hotel.getLat());
        endPoint.setLongitude(hotel.getLon());
        double distance=startPoint.distanceTo(endPoint);
        return distance/1000;
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


    @Override
    public void onLocationChanged(Location loc) {

        longitude =  loc.getLongitude();
        latitude = loc.getLatitude();

        System.out.println(latitude);
        System.out.println(longitude);


    }


    private ProgressDialog progress;



    public void showLoadingDialog() {

        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Please Wait...");
        }
        progress.show();
    }

    public void dismissLoadingDialog() {

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }
}