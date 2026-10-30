package com.example.campushu;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout containerFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Campus Student Hub 🇮🇳");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        containerFeed = findViewById(R.id.containerHomeFeed);

        Button btnMarketplace = findViewById(R.id.btnMarketplace);
        Button btnFreelance = findViewById(R.id.btnFreelance);
        Button btnChat = findViewById(R.id.btnChat);
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnRevenue = findViewById(R.id.btnRevenue);

        // Navigation
        btnMarketplace.setOnClickListener(v -> startActivity(new Intent(this, MarketplaceActivity.class)));
        btnFreelance.setOnClickListener(v -> startActivity(new Intent(this, FreelanceActivity.class)));
        btnChat.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        btnRevenue.setOnClickListener(v -> startActivity(new Intent(this, RevenueCalculatorActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEcosystemFeed();
    }

    private void loadEcosystemFeed() {
        containerFeed.removeAllViews();
        List<MarketItemModel> items = dbHelper.getFilteredItems("All", "");
        List<String> services = dbHelper.getAllServicesFormatted();

        if (items.isEmpty() && services.isEmpty()) {
            TextView emptyTv = new TextView(this);
            emptyTv.setText("🌟 No campus listings yet. Tap Market or Services above to post the first item!");
            emptyTv.setPadding(16, 32, 16, 32);
            emptyTv.setTextColor(Color.parseColor("#64748B"));
            emptyTv.setTextSize(14);
            containerFeed.addView(emptyTv);
            return;
        }

        // Render Marketplace Cards in Rupees (₹)
        for (MarketItemModel item : items) {
            CardView card = new CardView(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.setMargins(0, 0, 0, 16);
            card.setLayoutParams(p);
            card.setRadius(14);
            card.setCardElevation(3);
            card.setCardBackgroundColor(Color.WHITE);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(20, 20, 20, 20);

            // Item Title & Rupee Price Tag
            LinearLayout headerBox = new LinearLayout(this);
            headerBox.setOrientation(LinearLayout.HORIZONTAL);

            TextView titleTv = new TextView(this);
            titleTv.setText("📦 " + item.getTitle());
            titleTv.setTextSize(16);
            titleTv.setTypeface(null, Typeface.BOLD);
            titleTv.setTextColor(Color.parseColor("#0F172A"));
            LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            titleTv.setLayoutParams(p1);

            TextView priceTv = new TextView(this);
            priceTv.setText("₹" + String.format("%.0f", item.getPrice()));
            priceTv.setTextSize(16);
            priceTv.setTypeface(null, Typeface.BOLD);
            priceTv.setTextColor(Color.parseColor("#16A34A"));

            headerBox.addView(titleTv);
            headerBox.addView(priceTv);

            // Seller Info with Indian Campus Details
            TextView sellerTv = new TextView(this);
            sellerTv.setText("By " + item.getSeller() + " ✓ Verified Senior • ⭐ 4.9/5");
            sellerTv.setTextSize(12);
            sellerTv.setTextColor(Color.parseColor("#2563EB"));
            sellerTv.setPadding(0, 4, 0, 8);

            // Condition Tag
            TextView tagTv = new TextView(this);
            String tagText = "Condition: " + item.getCondition() + " | Category: " + item.getCategory();
            if (item.isExchange()) tagText += " [🔄 Open for Trade]";
            tagTv.setText(tagText);
            tagTv.setTextSize(12);
            tagTv.setTextColor(Color.parseColor("#64748B"));

            // Chat & Negotiate Action Button
            Button btnAction = new Button(this);
            btnAction.setText("💬 Chat & Negotiate Price");
            btnAction.setTextSize(12);
            btnAction.setBackgroundColor(Color.parseColor("#0F172A"));
            btnAction.setTextColor(Color.WHITE);
            LinearLayout.LayoutParams btnP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            btnP.setMargins(0, 12, 0, 0);
            btnAction.setLayoutParams(btnP);

            btnAction.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("seller_name", item.getSeller());
                intent.putExtra("item_title", item.getTitle());
                startActivity(intent);
            });

            layout.addView(headerBox);
            layout.addView(sellerTv);
            layout.addView(tagTv);
            layout.addView(btnAction);
            card.addView(layout);

            containerFeed.addView(card);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
        return true;
    }
}