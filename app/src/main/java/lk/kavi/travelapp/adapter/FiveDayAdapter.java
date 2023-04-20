package lk.kavi.travelapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import lk.kavi.travelapp.R;
import lk.kavi.travelapp.fragment.FiveDayWeather;
import lk.kavi.travelapp.utils.AppUtil;
import lk.kavi.travelapp.utils.Constants;

public class FiveDayAdapter extends RecyclerView.Adapter<FiveDayAdapter.ViewHolder> {

    Context context;
    private List<FiveDayWeather> listdata;

    public FiveDayAdapter(Context applicationContext,List<FiveDayWeather> listdata) {
        this.context = applicationContext;
        this.listdata = listdata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.weather_day_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final FiveDayWeather item = listdata.get(position);
        holder.cardView.setCardBackgroundColor(item.getColor());
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(item.getDt() * 1000L);
        holder.dayNameTextView.setText(Constants.DAYS_OF_WEEK[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
        int[] colors = {
                Color.TRANSPARENT,
                item.getColorAlpha(),
                Color.TRANSPARENT
        };
        holder.tempTextView.setText(String.format(Locale.getDefault(), "%.0f°", item.getTemp()));
//      holder.minTempTextView.setText(String.format(Locale.getDefault(), "%.0f°", item.getMinTemp()));
//      holder.maxTempTextView.setText(String.format(Locale.getDefault(), "%.0f°", item.getMaxTemp()));
      AppUtil.setWeatherIcon(this.context, holder.weatherImageView, item.getWeatherId());
        GradientDrawable shape = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors);
        shape.setShape(GradientDrawable.OVAL);
        holder.shadowView.setBackground(shape);

    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView dayNameTextView;
        public TextView tempTextView;
        public AppCompatImageView weatherImageView;
        public View shadowView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.cardView = (CardView) itemView.findViewById(R.id.card_view);
            this.dayNameTextView = (TextView) itemView.findViewById(R.id.day_name_text_view);
            this.tempTextView = (TextView) itemView.findViewById(R.id.temp_text_view);
            this.weatherImageView = (AppCompatImageView) itemView.findViewById(R.id.weather_image_view);
            this.shadowView = (View) itemView.findViewById(R.id.shadow_view);
        }


    }
}
