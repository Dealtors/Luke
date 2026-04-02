package com.example.luke;

import android.content.Intent;
import android.os.Bundle;
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

public class RegisterPointActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Spinner spinnerCity;
    private EditText editTextPointName;
    private EditText editTextPointAddress;
    private Button buttonRegister;
    private TextView textViewHaveAccount;
    private String selectedCity = "Красноярск";
    private final AppRepository repository = AppRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_point);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        spinnerCity = findViewById(R.id.spinnerCity);
        editTextPointName = findViewById(R.id.editTextPointName);
        editTextPointAddress = findViewById(R.id.editTextPointAddress);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewHaveAccount = findViewById(R.id.textViewHaveAccount);

        setupCitySpinner();

        buttonRegister.setOnClickListener(v -> {
            if (editTextName.getText().toString().trim().isEmpty()
                    || editTextPhone.getText().toString().trim().isEmpty()
                    || editTextEmail.getText().toString().trim().isEmpty()
                    || editTextPassword.getText().toString().trim().isEmpty()
                    || selectedCity.trim().isEmpty()
                    || editTextPointName.getText().toString().trim().isEmpty()
                        || editTextPointAddress.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            PointProfile profile = new PointProfile(
                    editTextPointName.getText().toString().trim(),
                    selectedCity,
                    editTextPointAddress.getText().toString().trim(),
                    editTextName.getText().toString().trim(),
                    editTextPhone.getText().toString().trim(),
                    editTextEmail.getText().toString().trim(),
                    editTextPassword.getText().toString().trim(),
                    "10:00–20:00"
            );
            repository.upsertPoint(profile);

            Toast.makeText(this, "Пункт зарегистрирован: " + selectedCity, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterPointActivity.this, CompanyMainActivity.class));
            finish();
        });

        // Переход на вход для пунктов
        textViewHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterPointActivity.this, LoginPointActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupCitySpinner() {
        String[] cities = new String[]{"Красноярск", "Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);
        spinnerCity.setSelection(0);
        selectedCity = cities[0];
        spinnerCity.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedCity = cities[position];
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedCity = cities[0];
            }
        });
    }
}
