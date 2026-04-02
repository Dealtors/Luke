package com.example.luke;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditHelperProfileActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextCity;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_helper_profile);

        initViews();
        setupButtons();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextCity = findViewById(R.id.editTextCity);
        buttonSave = findViewById(R.id.buttonSave);

        // Заглушка данных
        editTextName.setText("Анна");
        editTextPhone.setText("+7 (999) 999-99-99");
        editTextEmail.setText("anna@example.ru");
        editTextPassword.setText("********");
        editTextCity.setText("Красноярск");
    }

    private void setupButtons() {
        buttonSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString();
            String phone = editTextPhone.getText().toString();
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String city = editTextCity.getText().toString();

            Toast.makeText(this,
                    "Данные сохранены:\n" +
                            "Имя: " + name + "\n" +
                            "Телефон: " + phone + "\n" +
                            "Email: " + email + "\n" +
                            "Город: " + city,
                    Toast.LENGTH_LONG).show();
            AppRepository.getInstance().setHelperCity(city);
            finish(); // Возврат на предыдущий экран
        });
    }
}
