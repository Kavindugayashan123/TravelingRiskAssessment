package lk.kavi.travelapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import lk.kavi.travelapp.HotelActivity;
import lk.kavi.travelapp.LoginActivity;
import lk.kavi.travelapp.MainActivity;
import lk.kavi.travelapp.R;
import lk.kavi.travelapp.model.Hotel;

public class HotelListAdapter extends BaseAdapter {
    Context context;
    List<Hotel> list;
    LayoutInflater inflter;

    public HotelListAdapter(Context applicationContext, List<Hotel> list) {
        this.context = applicationContext;
        this.list = list;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.hotel, null);
        TextView country = (TextView) view.findViewById(R.id.hotelName);
        TextView range = (TextView) view.findViewById(R.id.range);
        RatingBar ratingBar =  view.findViewById(R.id.ratingBar);
        ImageView icon = (ImageView) view.findViewById(R.id.image);

        range.setText(list.get(i).getRange());
        country.setText(list.get(i).getName());
        icon.setImageBitmap(list.get(i).getImgBitmap());
        ratingBar.setMax(5);
        ratingBar.setRating(list.get(i).getRating());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HotelActivity.class);
                String data = new Gson().toJson(list.get(i));
                intent.putExtra("data",data);
                context.startActivity(intent);
            }
        });
        return view;
    }

}
