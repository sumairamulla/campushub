package com.example.campushu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Student Profile (₹)");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView tvDetails = findViewById(R.id.tvProfileDetails);
        Button btnLogout = findViewById(R.id.btnLogout);

        String profileSummary = "📧 Email: student@college.ac.in\n" +
                "🏠 Hostel: Block B, Room 304\n" +
                "🎓 Status: Senior Student (Verified)\n\n" +
                "📦 Active Marketplace Listings: 2 Items\n" +
                "🛠 Active Freelance Services: Java Tutoring\n" +
                "💳 Account Earnings Balance: ₹2,450\n" +
                "🔒 Senior Badge Subscription: Active (₹199/month)";

        tvDetails.setText(profileSummary);

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}