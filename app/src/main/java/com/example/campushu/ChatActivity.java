package com.example.campushu;

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

public class ChatActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout containerMessages;
    private EditText etChatMessage;
    private String sellerName = "Senior Trader";
    private String itemTitle = "Item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Campus Direct Chat (₹ INR)");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        containerMessages = findViewById(R.id.containerMessages);
        etChatMessage = findViewById(R.id.etChatMessage);
        Button btnSend = findViewById(R.id.btnSendMessage);
        TextView tvHeader = findViewById(R.id.tvChatHeader);

        if (getIntent().hasExtra("seller_name")) {
            sellerName = getIntent().getStringExtra("seller_name");
        }
        if (getIntent().hasExtra("item_title")) {
            itemTitle = getIntent().getStringExtra("item_title");
            tvHeader.setText("Negotiating: " + itemTitle + " (" + sellerName + ")");
        }

        // Fresh conversation populated in Indian Rupees (₹)
        if (dbHelper.getChatMessages().isEmpty()) {
            dbHelper.sendMessage("Junior (Alex)", sellerName, "Hi! Is " + itemTitle + " available for ₹400?");
            dbHelper.sendMessage(sellerName, "Junior (Alex)", "I can sell it for ₹450 if you pick it up at Hostel Block B.");
        }

        renderMessages();

        btnSend.setOnClickListener(v -> {
            String text = etChatMessage.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHelper.sendMessage("Me", sellerName, text);
            etChatMessage.setText("");
            renderMessages();
        });
    }

    private void renderMessages() {
        containerMessages.removeAllViews();
        List<String> messages = dbHelper.getChatMessages();

        for (String msg : messages) {
            CardView card = new CardView(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.setMargins(0, 0, 0, 8);
            card.setLayoutParams(p);
            card.setRadius(8);
            card.setCardElevation(1);

            TextView tv = new TextView(this);
            tv.setText(msg);
            tv.setPadding(12, 12, 12, 12);
            tv.setTextSize(14);

            if (msg.startsWith("Me:")) {
                tv.setTextColor(Color.parseColor("#0284C7"));
                tv.setBackgroundColor(Color.parseColor("#E0F2FE"));
            } else {
                tv.setTextColor(Color.parseColor("#0F172A"));
                tv.setBackgroundColor(Color.parseColor("#F1F5F9"));
            }

            card.addView(tv);
            containerMessages.addView(card);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}