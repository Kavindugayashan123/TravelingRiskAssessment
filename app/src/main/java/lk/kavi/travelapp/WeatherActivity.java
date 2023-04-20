package lk.kavi.travelapp;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.objectbox.reactive.DataSubscriptionList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import lk.kavi.travelapp.adapter.FiveDayAdapter;
import lk.kavi.travelapp.fragment.FiveDayWeather;
import lk.kavi.travelapp.model.CityInfo;
import lk.kavi.travelapp.model.currentweather.CurrentWeatherResponse;
import lk.kavi.travelapp.model.fivedayweather.FiveDayResponse;
import lk.kavi.travelapp.model.fivedayweather.ItemHourly;
import lk.kavi.travelapp.service.ApiService;
import lk.kavi.travelapp.utils.ApiClient;
import lk.kavi.travelapp.utils.AppUtil;
import lk.kavi.travelapp.utils.Constants;

public class WeatherActivity extends AppCompatActivity {

    //    private FastAdapter<FiveDayWeather> mFastAdapter;
//    private ItemAdapter<FiveDayWeather> mItemAdapter;
//    private CompositeDisposable disposable = new CompositeDisposable();
//    private String defaultLang = "en";
//    private List<FiveDayWeather> fiveDayWeathers;
    private ApiService apiService;
    //    private FiveDayWeather todayFiveDayWeather;
//    private Prefser prefser;
//    private Box<CurrentWeather> currentWeatherBox;
//    private Box<FiveDayWeather> fiveDayWeatherBox;
//    private Box<ItemHourlyDB> itemHourlyDBBox;
    private DataSubscriptionList subscriptions = new DataSubscriptionList();
    private boolean isLoad = false;
    private CityInfo cityInfo;
    private String apiKey;
    private Typeface typeface;

    private int[] colors;
    private int[] colorsAlpha;
    private CompositeDisposable disposable = new CompositeDisposable();


    TextView tempTextView;
    TextView descriptionTextView;
    TextView humidityTextView;
    TextView windTextView;

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        setupTextSwitchers();

//        humidityTextView =findViewById(R.id.humidity_text_view);
//       tempTextView = findViewById(R.id.temp_text_view);
//       windTextView =  findViewById(R.id.wind_text_view);
//        descriptionTextView = findViewById(R.id.description_text_view);
        colors = getResources().getIntArray(R.array.mdcolor_500);
        colorsAlpha = getResources().getIntArray(R.array.mdcolor_500_alpha);
        apiService = ApiClient.getClient().create(ApiService.class);
        List<FiveDayWeather> all = new ArrayList<>();


        getCurrentWeather("NEGOMBO");


        apiKey = getResources().getString(R.string.open_weather_map_api);

        disposable.add(
                apiService.getFiveDaysWeather(
                        "NEGOMBO", Constants.UNITS, "en", apiKey)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<FiveDayResponse>() {
                            @Override
                            public void onSuccess(FiveDayResponse response) {
                                handleFiveDayHourlyResponse(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }
                        })

        );

    }

    TextSwitcher simpleTextSwitcher;
    TextSwitcher simpleTextSwitcher1;
    TextSwitcher simpleTextSwitcher3;
    TextSwitcher simpleTextSwitchers3;

    private void setupTextSwitchers() {
        simpleTextSwitcher = findViewById(R.id.temp_text_view);
        simpleTextSwitcher1 = findViewById(R.id.description_text_view);
        simpleTextSwitcher3 = findViewById(R.id.humidity_text_view);
        simpleTextSwitchers3 = findViewById(R.id.wind_text_view);


    }


    private List<FiveDayWeather> fiveDayWeathers;

    private void handleFiveDayHourlyResponse(FiveDayResponse response) {
        fiveDayWeathers = new ArrayList<>();
        List<ItemHourly> list = response.getList();
        System.out.println("list.size()");
        System.out.println(list.size());

        list = make5DaysData(list);
        System.out.println(list.size());

        int day = 0;
        for (ItemHourly item : list) {
            int color = colors[day];
            int colorAlpha = colorsAlpha[day];
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            Calendar newCalendar = AppUtil.addDays(calendar, day);
            FiveDayWeather fiveDayWeather = new FiveDayWeather();
            fiveDayWeather.setWeatherId(item.getWeather().get(0).getId());
            fiveDayWeather.setDt(item.getDt());
            fiveDayWeather.setMaxTemp(item.getMain().getTempMax());
            fiveDayWeather.setMinTemp(item.getMain().getTempMin());
            fiveDayWeather.setTemp(item.getMain().getTemp());
            fiveDayWeather.setColor(color);
            fiveDayWeather.setColorAlpha(colorAlpha);
            fiveDayWeather.setTimestampStart(AppUtil.getStartOfDayTimestamp(newCalendar));
            fiveDayWeather.setTimestampEnd(AppUtil.getEndOfDayTimestamp(newCalendar));
            fiveDayWeathers.add(fiveDayWeather);
            day++;
        }

        RecyclerView rvContacts = findViewById(R.id.recycler_view);
        FiveDayAdapter adapter = new FiveDayAdapter(WeatherActivity.this,fiveDayWeathers);
        rvContacts.setAdapter(adapter);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvContacts.setLayoutManager(layoutManager);

    }

    private List<ItemHourly> make5DaysData(List<ItemHourly> list) {
        LinkedList<ItemHourly> list1 = new LinkedList<>();
        LinkedList<String> data = new LinkedList<>();
        for (ItemHourly itemHourly : list) {
            String date = itemHourly.getDtTxt().split(" ")[0];
            if (!data.contains(date)) {
                data.add(date);
                list1.add(itemHourly);
            }
        }
        list1.remove(0);
        return list1;
    }


    private void getCurrentWeather(String cityName) {
        apiKey = getResources().getString(R.string.open_weather_map_api);
        disposable.add(
                apiService.getCurrentWeather(
                        cityName, Constants.UNITS, "en", apiKey)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CurrentWeatherResponse>() {
                            @Override
                            public void onSuccess(CurrentWeatherResponse currentWeatherResponse) {

                                simpleTextSwitcher.setFactory(() -> {
                                    tempTextView = new TextView(WeatherActivity.this);
                                    tempTextView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                                    tempTextView.setTextColor(0xFFFFFFFF);
                                    tempTextView.setTextSize(100);
                                    tempTextView.setText(String.format(Locale.getDefault(), "%.0f°", currentWeatherResponse.getMain().getTemp()));
                                    return tempTextView;
                                });

                                // get reference of TextSwitcher
                                simpleTextSwitcher1.setFactory(() -> {
                                    descriptionTextView = new TextView(WeatherActivity.this);
                                    descriptionTextView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                                    descriptionTextView.setTextColor(0xFFFFFFFF);
                                    descriptionTextView.setText(AppUtil.getWeatherStatus(currentWeatherResponse.getWeather().get(0).getId(), AppUtil.isRTL(WeatherActivity.this)));
                                    descriptionTextView.setTextSize(30);
                                    return descriptionTextView;
                                });

                                // get reference of TextSwitcher
                                simpleTextSwitcher3.setFactory(() -> {
                                    humidityTextView = new TextView(WeatherActivity.this);
                                    humidityTextView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                                    humidityTextView.setTextColor(0xFFFFFFFF);
                                    humidityTextView.setText(String.format(Locale.getDefault(), "%d%%", currentWeatherResponse.getMain().getHumidity()));
                                    humidityTextView.setTextSize(14);
                                    return humidityTextView;
                                });


                                simpleTextSwitchers3.setFactory(() -> {
                                    windTextView = new TextView(WeatherActivity.this);
                                    windTextView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                                    windTextView.setTextColor(0xFFFFFFFF);
                                    windTextView.setText(String.format(Locale.getDefault(), getResources().getString(R.string.wind_unit_label), currentWeatherResponse.getWind().getSpeed()));

                                    windTextView.setTextSize(14);
                                    return windTextView;
                                });
                            }

                            @Override
                            public void onError(Throwable e) {
//                                binding.swipeContainer.setRefreshing(false);
//                                try {
//                                    HttpException error = (HttpException) e;
//                                    handleErrorCode(error);
//                                } catch (Exception exception) {
//                                    e.printStackTrace();
//                                }
                            }
                        })

        );
    }

}