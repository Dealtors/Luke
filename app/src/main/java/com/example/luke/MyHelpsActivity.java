package com.example.luke;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyHelpsActivity extends AppCompatActivity {

    private TextView textViewTotalDeliveries;
    private TextView textViewTotalPeople;
    private TextView buttonEditData;
    private ImageView buttonBack;  // ДОБАВЛЕНО: объявление поля
    private RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;
    private List<HistoryItem> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_helps);

        initViews();
        loadStats();
        loadHistory();
        setupRecyclerView();
        setupButtons();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        textViewTotalDeliveries = findViewById(R.id.textViewTotalDeliveries);
        textViewTotalPeople = findViewById(R.id.textViewTotalPeople);
        buttonEditData = findViewById(R.id.buttonEditData);
        buttonBack = findViewById(R.id.buttonBack);  // ИСПРАВЛЕНО: теперь это поле класса
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
    }

    private void loadStats() {
        textViewTotalDeliveries.setText("Всего доставок: 3");
        textViewTotalPeople.setText("Помогло людей: 15");
    }

    private void loadHistory() {
        historyList = new ArrayList<>();

        historyList.add(new HistoryItem(
                "Ленинский пр-т, 30",
                new String[]{
                        "Носки тёплые: 3 шт",
                        "Перчатки такт: 2 шт"
                },
                "12 марта 2024"
        ));

        historyList.add(new HistoryItem(
                "Дмитровское ш., 23",
                new String[]{
                        "Термобельё М: 2 шт",
                        "Свечи: 10 шт"
                },
                "10 марта 2024"
        ));

        historyList.add(new HistoryItem(
                "ул. Весны, 15",
                new String[]{
                        "Продукты: 5 кг",
                        "Вода: 6 л"
                },
                "5 марта 2024"
        ));
    }

    private void setupRecyclerView() {
        historyAdapter = new HistoryAdapter(historyList);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistory.setAdapter(historyAdapter);
    }

    private void setupButtons() {
        buttonEditData.setOnClickListener(v -> {
            Intent intent = new Intent(MyHelpsActivity.this, EditHelperProfileActivity.class);
            startActivity(intent);
        });

        buttonBack.setOnClickListener(v -> {
            finish(); // Возврат на предыдущий экран
        });
    }
}