package com.example.luke;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    // ID из вашего XML
    private TextView textViewProductName;        // android:id="@+id/textViewProductName"
    private TextView textViewProductDescription; // android:id="@+id/textViewProductDescription"
    private EditText editTextCurrentQuantity;    // android:id="@+id/editTextCurrentQuantity"
    private TextView textViewTotalInfo;           // android:id="@+id/textViewTotalInfo"
    private Spinner spinnerUrgency;               // android:id="@+id/spinnerUrgency"
    private Button buttonMinus;                    // android:id="@+id/buttonMinus"
    private Button buttonPlus;                      // android:id="@+id/buttonPlus"
    private Button buttonSave;                       // android:id="@+id/buttonSave"
    private Button buttonDelete;

    private final AppRepository repository = AppRepository.getInstance();
    private long productId = -1;
    private Product currentProduct;
    private String selectedUrgency = "Обычная";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        productId = getIntent().getLongExtra("product_id", -1);
        currentProduct = repository.getProductById(productId);
        if (currentProduct == null) {
            Toast.makeText(this, "Товар не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        selectedUrgency = currentProduct.getUrgency();

        initViews();
        setupSpinner();
        setupButtons();
        displayData();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        // Инициализация с правильными ID из XML
        textViewProductName = findViewById(R.id.textViewProductName);
        textViewProductDescription = findViewById(R.id.textViewProductDescription);
        editTextCurrentQuantity = findViewById(R.id.editTextCurrentQuantity);
        textViewTotalInfo = findViewById(R.id.textViewTotalInfo);
        spinnerUrgency = findViewById(R.id.spinnerUrgency);
        buttonMinus = findViewById(R.id.buttonMinus);
        buttonPlus = findViewById(R.id.buttonPlus);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);
    }

    private void setupSpinner() {
        List<String> urgencyList = new ArrayList<>();
        urgencyList.add("Критическая");
        urgencyList.add("Высокая");
        urgencyList.add("Средняя");
        urgencyList.add("Обычная");
        urgencyList.add("Низкая");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                urgencyList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUrgency.setAdapter(adapter);

        int position = urgencyList.indexOf(selectedUrgency);
        if (position >= 0) {
            spinnerUrgency.setSelection(position);
        }

        spinnerUrgency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUrgency = urgencyList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupButtons() {
        buttonMinus.setOnClickListener(v -> {
            int value = readQuantityFromField(currentProduct.getCollectedQuantity());
            if (value > 0) {
                value--;
                editTextCurrentQuantity.setText(String.valueOf(value));
                updateTotalInfo(value);
            }
        });

        buttonPlus.setOnClickListener(v -> {
            int value = readQuantityFromField(currentProduct.getCollectedQuantity());
            if (value < currentProduct.getTotalQuantity()) {
                value++;
                editTextCurrentQuantity.setText(String.valueOf(value));
                updateTotalInfo(value);
            } else {
                Toast.makeText(this, "Не может быть больше общего количества", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSave.setOnClickListener(v -> {
            int newQuantity = readQuantityFromField(currentProduct.getCollectedQuantity());
            currentProduct.setCollectedQuantity(newQuantity);
            currentProduct.setUrgency(selectedUrgency);
            repository.updateActiveProduct(currentProduct);
            Toast.makeText(this, "Сохранено: " + newQuantity + " шт., Срочность: " + selectedUrgency, Toast.LENGTH_LONG).show();
            finish();
        });

        buttonDelete.setOnClickListener(v -> {
            if (repository.deleteProduct(currentProduct.getId())) {
                Toast.makeText(this, "Товар удалён", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Не удалось удалить товар", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayData() {
        textViewProductName.setText(currentProduct.getName());
        textViewProductDescription.setText(currentProduct.getDescription());
        editTextCurrentQuantity.setText(String.valueOf(currentProduct.getCollectedQuantity()));
        updateTotalInfo(currentProduct.getCollectedQuantity());
    }

    private void updateTotalInfo(int current) {
        textViewTotalInfo.setText("Добавлено: " + current + " / Всего: " + currentProduct.getTotalQuantity());
    }

    private int readQuantityFromField(int fallback) {
        try {
            int value = Integer.parseInt(editTextCurrentQuantity.getText().toString().trim());
            if (value < 0) return 0;
            if (value > currentProduct.getTotalQuantity()) return currentProduct.getTotalQuantity();
            return value;
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
