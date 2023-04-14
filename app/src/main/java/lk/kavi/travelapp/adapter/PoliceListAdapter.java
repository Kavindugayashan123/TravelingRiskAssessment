package lk.kavi.travelapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import lk.kavi.travelapp.HotelActivity;
import lk.kavi.travelapp.PoliceActivity;
import lk.kavi.travelapp.R;
import lk.kavi.travelapp.model.Police;

public class PoliceListAdapter extends BaseAdapter {
    Context context;
    List<Police> list;

    LayoutInflater inflter;

    public PoliceListAdapter(Context applicationContext, List<Police> list) {
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
        view = inflter.inflate(R.layout.police, null);
        TextView range = (TextView) view.findViewById(R.id.policeRange);
        TextView name = (TextView) view.findViewById(R.id.policeName);
        ImageView icon = (ImageView) view.findViewById(R.id.policeImage);

        icon.setImageBitmap(list.get(i).getImgBitmap());
        range.setText(list.get(i).getRange());
        name.setText(list.get(i).getName());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PoliceActivity.class);
                String data = new Gson().toJson(list.get(i));
                intent.putExtra("data",data);
                context.startActivity(intent);
            }
        });
        return view;
    }
}