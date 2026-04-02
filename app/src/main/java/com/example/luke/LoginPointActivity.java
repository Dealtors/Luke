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

public class LoginPointActivity extends AppCompatActivity {

    private EditText editTextPointName;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewNoAccount;
    private final AppRepository repository = AppRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_point);

        // Инициализация views
        editTextPointName = findViewById(R.id.editTextPointName);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewNoAccount = findViewById(R.id.textViewNoAccount);

        // Обработчик кнопки входа
        buttonLogin.setOnClickListener(v -> {
            String pointName = editTextPointName.getText().toString();
            String password = editTextPassword.getText().toString();

            if (pointName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните название пункта и пароль", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Вход в пункт: " + pointName, Toast.LENGTH_SHORT).show();

            if (!repository.activatePointByName(pointName)) {
                Toast.makeText(this, "Пункт не найден, открыт первый доступный", Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(LoginPointActivity.this, CompanyMainActivity.class);
            startActivity(intent);
            finish();
        });

        // ДОБАВЛЕНО: обработчик нажатия на "У вас нет аккаунта?"
        textViewNoAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPointActivity.this, RegisterPointActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
