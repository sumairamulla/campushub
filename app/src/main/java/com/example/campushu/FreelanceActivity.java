package com.example.campushu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

public class FreelanceActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText etTitle, etProvider, etRate, etCategory;
    private LinearLayout containerServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelance);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student Freelance Hub (₹)");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        etTitle = findViewById(R.id.etServiceTitle);
        etProvider = findViewById(R.id.etServiceProvider);
        etRate = findViewById(R.id.etServiceRate);
        etCategory = findViewById(R.id.etCategory);
        containerServices = findViewById(R.id.containerServices);
        Button btnAdd = findViewById(R.id.btnAddService);

        renderServices();

        btnAdd.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String provider = etProvider.getText().toString().trim();
            String rateStr = etRate.getText().toString().trim();
            String cat = etCategory.getText().toString().trim();

            if (title.isEmpty() || provider.isEmpty() || rateStr.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double rate = Double.parseDouble(rateStr);
            String category = cat.isEmpty() ? "General" : cat;

            boolean isInserted = dbHelper.addService(title, provider, rate, category);

            if (isInserted) {
                Toast.makeText(this, "Service Posted Successfully!", Toast.LENGTH_SHORT).show();
                etTitle.setText("");
                etProvider.setText("");
                etRate.setText("");
                etCategory.setText("");
                renderServices();
            } else {
                Toast.makeText(this, "Failed to post service", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderServices() {
        containerServices.removeAllViews();
        List<String> services = dbHelper.getAllServicesFormatted();

        if (services.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("No freelance services offered yet.");
            tv.setTextColor(Color.GRAY);
            containerServices.addView(tv);
            return;
        }

        for (String service : services) {
            CardView card = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, 12);
            card.setLayoutParams(cardParams);
            card.setRadius(10);
            card.setCardElevation(2);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(16, 16, 16, 16);

            TextView tv = new TextView(this);
            tv.setText(service);
            tv.setTextSize(14);
            tv.setTextColor(Color.parseColor("#1E293B"));

            Button btnHire = new Button(this);
            btnHire.setText("💬 Hire / Send Request");
            btnHire.setTextSize(11);
            btnHire.setBackgroundColor(Color.parseColor("#2563EB"));
            btnHire.setTextColor(Color.WHITE);

            btnHire.setOnClickListener(v -> startActivity(new Intent(FreelanceActivity.this, ChatActivity.class)));

            layout.addView(tv);
            layout.addView(btnHire);
            card.addView(layout);
            containerServices.addView(card);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}