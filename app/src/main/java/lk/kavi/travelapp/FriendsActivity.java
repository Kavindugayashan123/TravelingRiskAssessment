package lk.kavi.travelapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import lk.kavi.travelapp.adapter.FriendListAdapter;
import lk.kavi.travelapp.model.User;
import lk.kavi.travelapp.utils.ApiClient;

public class FriendsActivity extends AppCompatActivity {
    Location gps_loc;
    Location network_loc;
    Location final_loc;
    double longitude;
    double latitude;

    Button searchBtn;
    EditText mobileNo;
    ListView simpleList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        showLoadingDialog();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mobileNo = findViewById(R.id.frdMobileNo);
        searchBtn = findViewById(R.id.addBtn);

        searchBtn.setOnClickListener(view -> {

            Query q = ApiClient.getDBRef()
                    .child("users").orderByChild("mobileno")
                    .equalTo(mobileNo.getText().toString());

            q.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    System.out.println(snapshot.toString());
                    Iterable<DataSnapshot> children = snapshot.getChildren();
                    DataSnapshot data = null;
                    String key = null;
                    for (DataSnapshot child : children) {
                        key = child.getKey();
                        System.out.println("key");
                        System.out.println(key);
                        System.out.println(child);
                        data = child;
                    }


                    if(data!=null){
                        User user = data.getValue(User.class);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FriendsActivity.this);

                        // Setting Alert Dialog Title
                        alertDialogBuilder.setTitle("User Found");
                        // Icon Of Alert Dialog
                        // Setting Alert Dialog Message
                        alertDialogBuilder.setMessage(user.getName()+" ,Add as Friend?");
                        alertDialogBuilder.setCancelable(false);

                        String finalKey = key;
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                System.out.println("sss");
                                DatabaseReference db = ApiClient.getDBRef();
                                DatabaseReference usersRef = db.child("users");

                                DatabaseReference users = usersRef.child(uid).child("friends");
                                List<String> friends = new ArrayList<>();
                                users.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {

                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            String value = child.getValue(String.class);
                                            if(value.equals(finalKey)){
                                               break;
                                            }
                                            friends.add(value);
                                        }
                                        friends.add(finalKey);
                                        users.setValue(friends);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {

                                    }
                                });


                            }
                        });

                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(FriendsActivity.this,"You clicked over No",Toast.LENGTH_SHORT).show();
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }else{
                        Toast.makeText(getApplicationContext(),"No User Found",Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
            System.out.println();
        });

        getMyFriends();
    }

    private void getMyFriends() {
        List<User> myList = new ArrayList<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference child = ApiClient.getDBRef()
                .child("users").child(uid).child("friends");
        child.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                GenericTypeIndicator<List<String>> genericTypeIndicator = new GenericTypeIndicator<List<String>>() {
                };
                List<String> frindList = snapshot.getValue(genericTypeIndicator);

                if(frindList!=null){
                    for (String frindIds : frindList) {
                        DatabaseReference child = ApiClient.getDBRef()
                                .child("users").child(frindIds);
                        child.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                User police = snapshot.getValue(User.class);
                                police.setBitmap(getBitmapFromURL(police.getImg()));
                                double v = checkDistance(police);
                                police.setRange(Math.round(v * 100.0) / 100.0 + " KM");
                                police.setRangeInt(Math.round(v * 100.0) / 100.0);
                                myList.add(police);

                                simpleList = (ListView) findViewById(R.id.simpleListView);
                                FriendListAdapter customAdapter = new FriendListAdapter(FriendsActivity.this, myList);
                                simpleList.setAdapter(customAdapter);
                                dismissLoadingDialog();
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        System.out.println("final");

    }

    private double checkDistance(User hotel) {

        Location startPoint=new Location("locationA");
        startPoint.setLatitude(latitude);
        startPoint.setLongitude(longitude);

        Location endPoint=new Location("locationA");
        endPoint.setLatitude(hotel.getLat());
        endPoint.setLongitude(hotel.getLon());
        double distance=startPoint.distanceTo(endPoint);
        return distance/1000;
    }


    public static Bitmap getBitmapFromURL(String src) {
        if(src!=null){
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
        return null;
    }

}