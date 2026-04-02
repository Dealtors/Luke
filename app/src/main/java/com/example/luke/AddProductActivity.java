package com.example.luke;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;  // ДОБАВЛЕНО
    private EditText editTextQuantity;
    private Spinner spinnerUrgency;
    private Button buttonMinus;
    private Button buttonPlus;
    private Button buttonAdd;

    private int quantity = 50;
    private String selectedUrgency = "Обычная";
    private final AppRepository repository = AppRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);

        initViews();
        setupSpinner();
        setupButtons();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);  // ДОБАВЛЕНО
        editTextQuantity = findViewById(R.id.editTextQuantity);
        spinnerUrgency = findViewById(R.id.spinnerUrgency);
        buttonMinus = findViewById(R.id.buttonMinus);
        buttonPlus = findViewById(R.id.buttonPlus);
        buttonAdd = findViewById(R.id.buttonAdd);

        editTextQuantity.setText(String.valueOf(quantity));
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
        spinnerUrgency.setSelection(3);

        spinnerUrgency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUrgency = urgencyList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedUrgency = "Обычная";
            }
        });
    }

    private void setupButtons() {
        buttonMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                editTextQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Минимальное количество: 1", Toast.LENGTH_SHORT).show();
            }
        });

        buttonPlus.setOnClickListener(v -> {
            quantity++;
            editTextQuantity.setText(String.valueOf(quantity));
        });

        buttonAdd.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();  // ДОБАВЛЕНО

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название товара", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                quantity = Integer.parseInt(editTextQuantity.getText().toString());
            } catch (NumberFormatException e) {
                quantity = 1;
            }

            String message = "Товар добавлен:\n" +
                    "Название: " + name + "\n" +
                    "Описание: " + (description.isEmpty() ? "нет" : description) + "\n" +
                    "Количество: " + quantity + "\n" +
                    "Срочность: " + selectedUrgency;

            repository.addProductToActivePoint(new Product(name, description, quantity, 0, selectedUrgency));

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
