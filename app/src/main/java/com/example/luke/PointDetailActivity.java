package com.example.luke;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class PointDetailActivity extends AppCompatActivity {

    private ImageView buttonBack;
    private ImageView buttonFavorite;
    private TextView textViewPointName;
    private TextView textViewPointAddress;
    private TextView textViewWorkHours;
    private ProgressBar progressBarOverall;
    private TextView textViewProgressPercent;
    private RecyclerView recyclerViewItems;
    private PointItemAdapter adapter;
    private List<Product> itemsList;
    private String currentPointName;
    private String currentPointAddress;
    private String currentWorkHours;
    private PointProfile currentPoint;
    private final AppRepository repository = AppRepository.getInstance();

    public static final String PREF_NAME = "LastPointPref";
    public static final String KEY_LAST_POINT = "last_point_id";
    private static final String KEY_FAVORITE_POINT = "favorite_point_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_point_detail);

        initViews();
        loadData();
        setupRecyclerView();
        setupListeners();
        saveAsLastPoint();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        buttonBack = findViewById(R.id.buttonBack);
        buttonFavorite = findViewById(R.id.buttonFavorite);
        textViewPointName = findViewById(R.id.textViewPointName);
        textViewPointAddress = findViewById(R.id.textViewPointAddress);
        textViewWorkHours = findViewById(R.id.textViewWorkHours);
        progressBarOverall = findViewById(R.id.progressBarOverall);
        textViewProgressPercent = findViewById(R.id.textViewProgressPercent);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
    }

    private void loadData() {
        // Получаем данные из Intent
        currentPointName = getIntent().getStringExtra("point_name");
        currentPointAddress = getIntent().getStringExtra("point_address");
        currentWorkHours = getIntent().getStringExtra("work_hours");
        String pointCity = getIntent().getStringExtra("point_city");

        currentPoint = repository.getPointByAddress(currentPointAddress);
        if (currentPoint == null && currentPointName != null) {
            currentPoint = repository.getPointByName(currentPointName);
        }
        if (currentPoint == null && pointCity != null) {
            List<NeedPoint> points = repository.getNeedPointsForCity(pointCity);
            if (!points.isEmpty()) {
                currentPoint = repository.getPointByAddress(points.get(0).getAddress());
            }
        }
        if (currentPoint == null) {
            currentPoint = repository.getActivePoint();
        }

        if (currentPoint == null) {
            currentPointName = "Штаб «Свои»";
            currentPointAddress = "Ленинский пр-т, 30";
            currentWorkHours = "10:00–20:00";
            itemsList = new ArrayList<>();
        } else {
            currentPointName = currentPoint.getPointName();
            currentPointAddress = currentPoint.getAddress();
            currentWorkHours = currentPoint.getWorkHours();
            itemsList = currentPoint.getProducts();
        }

        textViewPointName.setText(currentPointName);
        textViewPointAddress.setText(currentPointAddress);
        textViewWorkHours.setText("График работы\n" + currentWorkHours);

        // Общий прогресс
        int totalNeeded = 0;
        int totalCollected = 0;
        for (Product item : itemsList) {
            totalNeeded += item.getTotalQuantity();
            totalCollected += item.getCollectedQuantity();
        }
        int overallProgress = totalNeeded == 0 ? 0 : (int) ((float) totalCollected / totalNeeded * 100);
        progressBarOverall.setProgress(overallProgress);
        textViewProgressPercent.setText("Прогресс: " + overallProgress + "%");
    }

    private void setupRecyclerView() {
        adapter = new PointItemAdapter(itemsList);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        if (adapter != null) {
            adapter.updateItems(itemsList);
        }
    }

    private void setupListeners() {
        buttonBack.setOnClickListener(v -> finish());

        // Сердце - пока недоступно (серое)
        buttonFavorite.setEnabled(false);
        buttonFavorite.setAlpha(0.5f);

        // Здесь будет логика, когда сердце станет доступным
        // Например, после первого посещения
        checkIfFavoriteAvailable();
    }

    private void checkIfFavoriteAvailable() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean hasVisited = prefs.getBoolean("has_visited_before", false);

        if (hasVisited) {
            buttonFavorite.setEnabled(true);
            buttonFavorite.setAlpha(1.0f);
            updateFavoriteIcon();
            buttonFavorite.setOnClickListener(v -> toggleFavorite());
        }
    }

    private void saveAsLastPoint() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LAST_POINT, currentPointAddress);
        editor.putBoolean("has_visited_before", true);
        editor.apply();
    }

    private void toggleFavorite() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String favoriteAddress = prefs.getString(KEY_FAVORITE_POINT, null);
        SharedPreferences.Editor editor = prefs.edit();

        if (currentPointAddress.equals(favoriteAddress)) {
            editor.remove(KEY_FAVORITE_POINT);
            editor.apply();
            updateFavoriteIcon();
            buttonFavorite.setContentDescription("Добавить в избранное");
            Toast.makeText(this, "Убрано из избранного", Toast.LENGTH_SHORT).show();
        } else {
            editor.putString(KEY_FAVORITE_POINT, currentPointAddress);
            editor.apply();
            updateFavoriteIcon();
            buttonFavorite.setContentDescription("Убрать из избранного");
            Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFavoriteIcon() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String favoriteAddress = prefs.getString(KEY_FAVORITE_POINT, null);
        boolean isFavorite = currentPointAddress != null && currentPointAddress.equals(favoriteAddress);
        buttonFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    // Статический метод для получения последнего пункта
    public static String getLastPointAddress(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_LAST_POINT, null);
    }
}
