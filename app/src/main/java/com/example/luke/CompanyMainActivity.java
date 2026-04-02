package com.example.luke;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class CompanyMainActivity extends AppCompatActivity {

    private LinearLayout productsContainer;
    private List<Product> productList;
    private Button buttonAddProduct;
    private TextView buttonEditData;
    private Button buttonLogout;
    private final AppRepository repository = AppRepository.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_company_main);

        productsContainer = findViewById(R.id.productsContainer);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        buttonEditData = findViewById(R.id.buttonEditData);
        buttonLogout = findViewById(R.id.buttonLogout);

        loadProducts();

        // Кнопка добавления товара
        buttonAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(CompanyMainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        // Кнопка изменения данных компании - ПЕРЕХОД НА EditCompanyActivity
        buttonEditData.setOnClickListener(v -> {
            Intent intent = new Intent(CompanyMainActivity.this, EditCompanyActivity.class);
            startActivity(intent);
        });

        buttonLogout.setOnClickListener(v -> logout());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadProducts() {
        productList = repository.getActiveProducts();
        displayProducts();
    }

    private void displayProducts() {
        productsContainer.removeAllViews();

        for (Product product : productList) {

            LinearLayout productCard = new LinearLayout(this);
            productCard.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            productCard.setOrientation(LinearLayout.VERTICAL);
            productCard.setPadding(50, 30, 50, 30);
            productCard.setBackgroundResource(R.drawable.product_background);

            TextView nameText = new TextView(this);
            nameText.setText(product.getName());
            nameText.setTextSize(18);
            nameText.setTextColor(0xFF2C1E17);
            nameText.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView descText = new TextView(this);
            descText.setText(product.getDescription());
            descText.setTextSize(14);
            descText.setTextColor(0xFF2C1E17);

            TextView quantityText = new TextView(this);
            quantityText.setText("Собрано: " + product.getCollectedQuantity() + " / " + product.getTotalQuantity());
            quantityText.setTextSize(14);
            quantityText.setTextColor(0xFF2C1E17);

            TextView urgencyText = new TextView(this);
            urgencyText.setText("Срочность: " + product.getUrgency());
            urgencyText.setTextSize(14);
            urgencyText.setTextColor(0xFF757575);

            productCard.addView(nameText);
            productCard.addView(descText);
            productCard.addView(quantityText);
            productCard.addView(urgencyText);

            productCard.setOnClickListener(v -> {
                Intent intent = new Intent(CompanyMainActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            });

            productsContainer.addView(productCard);

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 2));
            divider.setBackgroundColor(0xFFE0E0E0);
            productsContainer.addView(divider);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }

    private void logout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
