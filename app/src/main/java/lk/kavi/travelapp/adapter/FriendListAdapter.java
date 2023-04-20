package lk.kavi.travelapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import lk.kavi.travelapp.HotelActivity;
import lk.kavi.travelapp.R;
import lk.kavi.travelapp.model.Hotel;
import lk.kavi.travelapp.model.User;

public class FriendListAdapter extends BaseAdapter {
    Context context;
    List<User> list;
    LayoutInflater inflter;

    public FriendListAdapter(Context applicationContext, List<User> list) {
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

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.friend, null);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView range = (TextView) view.findViewById(R.id.range);
        Button distanceBadge =  view.findViewById(R.id.distanceBadge);
        ImageView icon = (ImageView) view.findViewById(R.id.userImg);

        range.setText(list.get(i).getRange());
        icon.setImageBitmap(list.get(i).getBitmap());
        name.setText(list.get(i).getName());

        User user = list.get(i);
        double rangeInt = user.getRangeInt();

        if (rangeInt < 10) {
            distanceBadge.setBackgroundColor(0xFF00ff00);//gr
        } else {
            if (rangeInt < 20) {
                distanceBadge.setBackgroundColor(0xFFFFFF00);//y
            } else {
                distanceBadge.setBackgroundColor(0xFFFF0000);//re
            }
        }
        return view;
    }

}
