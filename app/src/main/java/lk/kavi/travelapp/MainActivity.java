package lk.kavi.travelapp;

import androidx.appcompat.app.AppCompatActivity;
import lk.kavi.travelapp.model.currentweather.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    Button btnRA,btnW,btnTF,btnCP,btnL,btnEm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                startActivity(new Intent(MainActivity.this, PoliceActivity.class));
            }
        });
    }
}