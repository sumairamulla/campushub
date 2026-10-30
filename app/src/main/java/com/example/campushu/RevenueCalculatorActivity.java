package com.example.campushu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RevenueCalculatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_calculator);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Revenue Model (₹ INR)");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EditText etGMV = findViewById(R.id.etGMV);
        EditText etVerified = findViewById(R.id.etVerifiedUsers);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        TextView tvResult = findViewById(R.id.tvRevenueResult);

        btnCalculate.setOnClickListener(v -> {
            String gmvStr = etGMV.getText().toString().trim();
            String verifiedStr = etVerified.getText().toString().trim();

            if (gmvStr.isEmpty() || verifiedStr.isEmpty()) {
                Toast.makeText(this, "Please enter GMV amount and badge count", Toast.LENGTH_SHORT).show();
                return;
            }

            double gmv = Double.parseDouble(gmvStr);
            int badges = Integer.parseInt(verifiedStr);

            double platformFee = gmv * 0.10; // 10% marketplace commission
            double badgeRevenue = badges * 199.00; // ₹199/month badge fee
            double totalEarnings = platformFee + badgeRevenue;

            String summary = "📊 Financial Projection (INR):\n\n" +
                    "• Marketplace GMV: ₹" + String.format("%.0f", gmv) + "\n" +
                    "• Platform Commission (10%): ₹" + String.format("%.0f", platformFee) + "\n" +
                    "• Verified Badges Income (₹199/mo): ₹" + String.format("%.0f", badgeRevenue) + "\n" +
                    "-----------------------------------\n" +
                    "💰 Total Projected Monthly Revenue: ₹" + String.format("%.0f", totalEarnings);

            tvResult.setText(summary);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}