package com.example.luke;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HelperMainActivity extends AppCompatActivity {

    private Spinner spinnerCity;
    private TextView textViewChangeCity;
    private TextView textViewWeather;
    private ImageView imageViewWeather;
    private ImageView imageViewProfile;
    private ImageView imageViewSearch;  // ДОБАВЛЕНО
    private Button buttonLogout;
    private RecyclerView recyclerView;
    private NeedPointAdapter adapter;
    private List<String> cities;
    private String selectedCity;
    private final AppRepository repository = AppRepository.getInstance();
    private final WeatherService weatherService = WeatherService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_helper_main);

        initViews();
        setupCitySpinner();
        setupWeather();
        setupRecyclerView();
        setupClickListeners();
        refreshPoints();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        spinnerCity = findViewById(R.id.spinnerCity);
        textViewChangeCity = findViewById(R.id.textViewChangeCity);
        textViewWeather = findViewById(R.id.textViewWeather);
        imageViewWeather = findViewById(R.id.imageViewWeather);
        imageViewProfile = findViewById(R.id.imageViewIcon4);
        imageViewSearch = findViewById(R.id.imageViewIcon2);  // ДОБАВЛЕНО - инициализация
        buttonLogout = findViewById(R.id.buttonLogout);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupCitySpinner() {
        cities = new ArrayList<>();
        cities.add("Все города");
        cities.add("Красноярск");
        cities.add("Москва");
        cities.add("Санкт-Петербург");
        cities.add("Новосибирск");
        cities.add("Екатеринбург");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cities
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);
        selectedCity = repository.getHelperCity();
        int selectedIndex = cities.indexOf(selectedCity);
        spinnerCity.setSelection(selectedIndex >= 0 ? selectedIndex : 0);

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = cities.get(position);
                repository.setHelperCity(selectedCity);
                applyCityFilter();
                refreshWeather();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        textViewChangeCity.setOnClickListener(v -> {
            spinnerCity.performClick();
        });
    }

    private void setupWeather() {
        textViewWeather.setText("Загрузка погоды...");
        imageViewWeather.setImageResource(R.drawable.ic_weather_snow);
    }

    private void setupRecyclerView() {
        adapter = new NeedPointAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // <--- ЭТО ДОБАВИТЬ - обработчик нажатия на пункт списка
        adapter.setOnItemClickListener(point -> {
            Intent intent = new Intent(HelperMainActivity.this, PointDetailActivity.class);
            intent.putExtra("point_name", point.getPointName());
            intent.putExtra("point_address", point.getAddress());
            intent.putExtra("point_city", point.getCity());
            intent.putExtra("work_hours", "10:00–20:00"); // Заглушка
            startActivity(intent);
        });

        applyCityFilter();
    }

    private void setupClickListeners() {
        imageViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HelperMainActivity.this, MyHelpsActivity.class);
            startActivity(intent);
        });

        imageViewSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HelperMainActivity.this, MapActivity.class);
            startActivity(intent);
        });

        ImageView imageViewFavorite = findViewById(R.id.imageViewIcon3);
        imageViewFavorite.setOnClickListener(v -> {
            String lastPoint = PointDetailActivity.getLastPointAddress(this);
            if (lastPoint != null) {
                Intent intent = new Intent(HelperMainActivity.this, PointDetailActivity.class);
                intent.putExtra("point_address", lastPoint);
                // Здесь нужно будет загрузить остальные данные
                startActivity(intent);
            } else {
                Toast.makeText(this, "Нет последнего просмотренного пункта", Toast.LENGTH_SHORT).show();
            }
        });

        buttonLogout.setOnClickListener(v -> logout());
    }

    private void applyCityFilter() {
        if (adapter == null) return;
        adapter.updateItems(repository.getNeedPointsForCity(selectedCity));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPoints();
    }

    private void refreshPoints() {
        selectedCity = repository.getHelperCity();
        if (cities != null) {
            int selectedIndex = cities.indexOf(selectedCity);
            if (selectedIndex >= 0 && spinnerCity != null) {
                spinnerCity.setSelection(selectedIndex);
            }
        }
        applyCityFilter();
        refreshWeather();
    }

    private void refreshWeather() {
        setupWeather();
        String weatherCity = "Все города".equals(selectedCity) ? "Красноярск" : selectedCity;
        weatherService.loadWeather(weatherCity, new WeatherService.Callback() {
            @Override
            public void onSuccess(WeatherInfo weatherInfo) {
                textViewWeather.setText(weatherInfo.toDisplayText());
                imageViewWeather.setContentDescription(weatherInfo.getDescription());
            }

            @Override
            public void onError(Exception error) {
                textViewWeather.setText(weatherCity + ": погода недоступна");
            }
        });
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}
