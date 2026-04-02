package com.example.luke;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditCompanyActivity extends AppCompatActivity {

    private final AppRepository repository = AppRepository.getInstance();
    private EditText editTextPointName;
    private EditText editTextAddress;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private String originalAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_company);

        editTextPointName = findViewById(R.id.editTextPointName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        PointProfile currentPoint = repository.getActivePoint();
        if (currentPoint != null) {
            originalAddress = currentPoint.getAddress();
            editTextPointName.setText(currentPoint.getPointName());
            editTextAddress.setText(currentPoint.getAddress());
            editTextEmail.setText(currentPoint.getEmail());
            editTextPassword.setText(currentPoint.getPassword());
        }

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> {
            PointProfile point = repository.getActivePoint();
            if (point == null) {
                point = new PointProfile(
                        editTextPointName.getText().toString().trim(),
                        "Красноярск",
                        editTextAddress.getText().toString().trim(),
                        "Контакт",
                        "",
                        editTextEmail.getText().toString().trim(),
                        editTextPassword.getText().toString().trim(),
                        "10:00–20:00"
                );
            } else {
                point.setPointName(editTextPointName.getText().toString().trim());
                point.setAddress(editTextAddress.getText().toString().trim());
                point.setEmail(editTextEmail.getText().toString().trim());
                point.setPassword(editTextPassword.getText().toString().trim());
            }

            if (originalAddress == null) {
                repository.upsertPoint(point);
            } else {
                repository.updatePoint(originalAddress, point);
            }
            Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
