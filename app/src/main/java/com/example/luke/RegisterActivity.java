package com.example.luke;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextCity;
    private Button buttonRegister;
    private TextView textViewHaveAccount;
    private final AppRepository repository = AppRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextCity = findViewById(R.id.editTextCity);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewHaveAccount = findViewById(R.id.textViewHaveAccount);

        buttonRegister.setOnClickListener(v -> {
            String city = editTextCity.getText().toString().trim();
            if (editTextName.getText().toString().trim().isEmpty()
                    || editTextPhone.getText().toString().trim().isEmpty()
                    || editTextEmail.getText().toString().trim().isEmpty()
                    || editTextPassword.getText().toString().trim().isEmpty()
                    || city.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            repository.setHelperCity(city);
            Toast.makeText(this, "Регистрация завершена", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, HelperMainActivity.class));
            finish();
        });

        // Переход на вход
        textViewHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
