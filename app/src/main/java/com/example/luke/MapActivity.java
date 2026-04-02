package com.example.luke;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private ImageView buttonBack;
    private Spinner spinnerCity;
    private TextView textViewWeatherSummary;
    private TextView textViewWeatherDetails;
    private MapView mapView;
    private TextView textViewSelectedPointName;
    private TextView textViewSelectedPointAddress;
    private TextView textViewSelectedPointDetails;
    private Button buttonBuildRoute;

    private final AppRepository repository = AppRepository.getInstance();
    private final WeatherService weatherService = WeatherService.getInstance();
    private final List<String> cities = new ArrayList<>();
    private List<PointProfile> visiblePoints = new ArrayList<>();
    private String selectedCity;
    private PointProfile selectedPoint;
    private IMapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        initViews();
        setupCities();
        setupMap();
        setupListeners();
        refreshAll();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        buttonBack = findViewById(R.id.buttonBack);
        spinnerCity = findViewById(R.id.spinnerCity);
        textViewWeatherSummary = findViewById(R.id.textViewWeatherSummary);
        textViewWeatherDetails = findViewById(R.id.textViewWeatherDetails);
        mapView = findViewById(R.id.mapView);
        textViewSelectedPointName = findViewById(R.id.textViewSelectedPointName);
        textViewSelectedPointAddress = findViewById(R.id.textViewSelectedPointAddress);
        textViewSelectedPointDetails = findViewById(R.id.textViewSelectedPointDetails);
        buttonBuildRoute = findViewById(R.id.buttonBuildRoute);
    }

    private void setupCities() {
        cities.add("Красноярск");
        cities.add("Москва");
        cities.add("Санкт-Петербург");
        cities.add("Новосибирск");
        cities.add("Екатеринбург");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
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
                refreshAll();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupMap() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapController = mapView.getController();
        mapController.setZoom(11.5);
    }

    private void setupListeners() {
        buttonBack.setOnClickListener(v -> finish());

        buttonBuildRoute.setOnClickListener(v -> buildRoute());
    }

    private void refreshAll() {
        loadPoints();
        renderMarkers();
        loadWeather();
    }

    private void loadPoints() {
        visiblePoints = repository.getPointProfilesForCity(selectedCity);
        if (visiblePoints.isEmpty()) {
            selectedPoint = null;
            updateSelectedPointCard(null);
            buttonBuildRoute.setEnabled(false);
            return;
        }

        if (selectedPoint == null || !selectedCity.equals(selectedPoint.getCity())) {
            selectedPoint = visiblePoints.get(0);
        } else {
            PointProfile refreshed = repository.getPointByAddress(selectedPoint.getAddress());
            selectedPoint = refreshed != null ? refreshed : visiblePoints.get(0);
        }

        buttonBuildRoute.setEnabled(true);
        updateSelectedPointCard(selectedPoint);
    }

    private void renderMarkers() {
        mapView.getOverlays().clear();

        for (PointProfile point : visiblePoints) {
            Marker marker = new Marker(mapView);
            marker.setPosition(new GeoPoint(point.getLatitude(), point.getLongitude()));
            marker.setTitle(point.getPointName());
            marker.setSnippet(point.getAddress() + "\n" + point.getWorkHours());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setOnMarkerClickListener((m, mapView1) -> {
                selectedPoint = point;
                updateSelectedPointCard(point);
                mapController.animateTo(new GeoPoint(point.getLatitude(), point.getLongitude()));
                return true;
            });
            mapView.getOverlays().add(marker);
        }

        if (selectedPoint != null) {
            mapController.setCenter(new GeoPoint(selectedPoint.getLatitude(), selectedPoint.getLongitude()));
        } else {
            double[] center = GeoUtils.cityCenter(selectedCity);
            mapController.setCenter(new GeoPoint(center[0], center[1]));
        }

        mapView.invalidate();
    }

    private void updateSelectedPointCard(PointProfile point) {
        if (point == null) {
            textViewSelectedPointName.setText("Пункт не выбран");
            textViewSelectedPointAddress.setText("Откройте город с доступными пунктами");
            textViewSelectedPointDetails.setText("Маркер на карте покажет описание и адрес.");
            return;
        }

        textViewSelectedPointName.setText(point.getPointName());
        textViewSelectedPointAddress.setText(point.getAddress());
        textViewSelectedPointDetails.setText(
                "Город: " + point.getCity() + "\n" +
                "График: " + point.getWorkHours() + "\n" +
                "Контакт: " + point.getContactName() + ", " + point.getContactPhone()
        );
    }

    private void loadWeather() {
        textViewWeatherSummary.setText("Загрузка погоды...");
        textViewWeatherDetails.setText("");

        weatherService.loadWeather(selectedCity, new WeatherService.Callback() {
            @Override
            public void onSuccess(WeatherInfo weatherInfo) {
                textViewWeatherSummary.setText(weatherInfo.getCity() + ": " + weatherInfo.getTemperature() + "°C, " + weatherInfo.getDescription());
                textViewWeatherDetails.setText("Ветер: " + weatherInfo.getWindSpeed() + " м/с\nВремя: " + weatherInfo.getTime());
            }

            @Override
            public void onError(Exception error) {
                textViewWeatherSummary.setText("Погода недоступна");
                textViewWeatherDetails.setText("Проверь интернет-соединение.");
            }
        });
    }

    private void buildRoute() {
        if (selectedPoint == null) {
            Toast.makeText(this, "Выберите пункт на карте", Toast.LENGTH_SHORT).show();
            return;
        }

        String uri = "google.navigation:q=" + selectedPoint.getLatitude() + "," + selectedPoint.getLongitude();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:" + selectedPoint.getLatitude() + "," + selectedPoint.getLongitude() + "?q=" + selectedPoint.getLatitude() + "," + selectedPoint.getLongitude() + "(" + selectedPoint.getPointName() + ")")));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        refreshAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
