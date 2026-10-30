package com.example.campushu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MealShareActivity extends AppCompatActivity {

    private final List<String> mealList = new ArrayList<>();
    private EditText etMealDetails, etHostelName, etMealPrice;
    private TextView tvMealList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_share);

        etMealDetails = findViewById(R.id.etMealDetails);
        etHostelName = findViewById(R.id.etHostelName);
        etMealPrice = findViewById(R.id.etMealPrice);
        tvMealList = findViewById(R.id.tvMealList);
        Button btnAddMeal = findViewById(R.id.btnAddMeal);

        mealList.add("🍱 Mess Dinner Pass - Block A Mess ($2.00)");
        mealList.add("🥪 Extra Breakfast Coupon - Central Cafeteria (FREE)");
        updateDisplay();

        btnAddMeal.setOnClickListener(v -> {
            String details = etMealDetails.getText().toString().trim();
            String hostel = etHostelName.getText().toString().trim();
            String price = etMealPrice.getText().toString().trim();

            if (details.isEmpty() || hostel.isEmpty() || price.isEmpty()) {
                Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
                return;
            }

            mealList.add("🍱 " + details + " - " + hostel + " ($" + price + ")");
            etMealDetails.setText("");
            etHostelName.setText("");
            etMealPrice.setText("");
            updateDisplay();
        });
    }

    private void updateDisplay() {
        StringBuilder builder = new StringBuilder();
        for (String meal : mealList) {
            builder.append(meal).append("\n\n");
        }
        tvMealList.setText(builder.toString());
    }
}