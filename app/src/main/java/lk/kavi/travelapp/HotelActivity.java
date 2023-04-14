package lk.kavi.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import lk.kavi.travelapp.model.Hotel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HotelActivity extends AppCompatActivity {

    TextView hotelName, mobileNo,wifiTxt,poolText,stars,hotelDesc;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        imageView = findViewById(R.id.hotelImage);
        hotelName = findViewById(R.id.hotelName);
        mobileNo = findViewById(R.id.mobileNo);
        wifiTxt = findViewById(R.id.wifiTxt);
        poolText = findViewById(R.id.poolText);
        stars = findViewById(R.id.stars);
        hotelDesc = findViewById(R.id.hotelDesc);

        Intent intent = getIntent();
        String datas = (String) intent.getExtras().getSerializable("data");

        Hotel data = new Gson().fromJson(datas, Hotel.class);

        hotelName.setText(data.getName());
        mobileNo.setText(data.getMobile_no());
        wifiTxt.setText(data.getWifi());
        poolText.setText(data.getName());
        stars.setText(data.getRating()+" Stars");
        hotelDesc.setText(data.getDescription());

        imageView.setImageBitmap(data.getImgBitmap());


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
}