package com.example.campushu;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

public class MarketplaceActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText etTitle, etSeller, etPrice, etCondition, etSearch;
    private CheckBox cbExchange;
    private ImageView imgPreview;
    private LinearLayout containerings;
    private CardView cardPostForm;
    private Spinner spinnerCategory;
    private String selectedImageUri = "";
    private String currentCategoryFilter = "All";

    private final ActivityResultLauncher<Intent> imagePicker =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        try {
                            getContentResolver().takePersistableUriPermission(
                                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception ignored) {}
                        selectedImageUri = uri.toString();
                        imgPreview.setImageURI(uri);
                    }
                } else {
                    Toast.makeText(this, "Photo selection cancelled", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Campus Buy & Sell Hub (₹)");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);

        etTitle = findViewById(R.id.etItemTitle);
        etSeller = findViewById(R.id.etItemSeller);
        etPrice = findViewById(R.id.etItemPrice);
        etCondition = findViewById(R.id.etCondition);
        etSearch = findViewById(R.id.etSearchMarket);
        cbExchange = findViewById(R.id.cbExchange);
        imgPreview = findViewById(R.id.imgPreview);
        containerings = findViewById(R.id.containerListings);
        cardPostForm = findViewById(R.id.cardPostForm);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        Button btnToggleForm = findViewById(R.id.btnTogglePostForm);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnAddItem = findViewById(R.id.btnAddItem);

        String[] categories = {"Books", "Tech", "Lab Gear", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        btnToggleForm.setOnClickListener(v -> {
            if (cardPostForm.getVisibility() == View.VISIBLE) {
                cardPostForm.setVisibility(View.GONE);
                btnToggleForm.setText("➕ Post New Item for Sale");
            } else {
                cardPostForm.setVisibility(View.VISIBLE);
                btnToggleForm.setText("✖ Close Form");
            }
        });

        Button btnAll = findViewById(R.id.btnCatAll);
        Button btnBooks = findViewById(R.id.btnCatBooks);
        Button btnTech = findViewById(R.id.btnCatTech);
        Button btnLab = findViewById(R.id.btnCatLab);

        View.OnClickListener catListener = v -> {
            Button b = (Button) v;
            currentCategoryFilter = b.getText().toString().replace("📚 ", "").replace("💻 ", "").replace("🔬 ", "");
            renderListings();
        };

        btnAll.setOnClickListener(catListener);
        btnBooks.setOnClickListener(catListener);
        btnTech.setOnClickListener(catListener);
        btnLab.setOnClickListener(catListener);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderListings(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            imagePicker.launch(intent);
        });

        btnAddItem.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String seller = etSeller.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String cond = etCondition.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();

            if (title.isEmpty() || seller.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Please complete required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            boolean isInserted = dbHelper.addItem(title, seller, price, cond, category, selectedImageUri, cbExchange.isChecked());

            if (isInserted) {
                Toast.makeText(this, "Item Listed Successfully!", Toast.LENGTH_SHORT).show();
                etTitle.setText("");
                etSeller.setText("");
                etPrice.setText("");
                etCondition.setText("");
                cbExchange.setChecked(false);
                imgPreview.setImageResource(0);
                selectedImageUri = "";
                cardPostForm.setVisibility(View.GONE);
                btnToggleForm.setText("➕ Post New Item for Sale");
                renderListings();
            } else {
                Toast.makeText(this, "Failed to list item", Toast.LENGTH_SHORT).show();
            }
        });

        renderListings();
    }

    private void renderListings() {
        containerings.removeAllViews();
        String searchQuery = etSearch.getText().toString().trim();
        List<MarketItemModel> items = dbHelper.getFilteredItems(currentCategoryFilter, searchQuery);

        if (items.isEmpty()) {
            TextView emptyTv = new TextView(this);
            emptyTv.setText("No marketplace items found in this category.");
            emptyTv.setPadding(16, 32, 16, 32);
            emptyTv.setTextColor(Color.GRAY);
            containerings.addView(emptyTv);
            return;
        }

        for (MarketItemModel item : items) {
            CardView card = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, 16);
            card.setLayoutParams(cardParams);
            card.setRadius(12);
            card.setCardElevation(3);
            card.setCardBackgroundColor(Color.WHITE);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(16, 16, 16, 16);

            // Display Title & Price in Indian Rupees (₹)
            TextView titleTv = new TextView(this);
            titleTv.setText("📦 " + item.getTitle() + " - ₹" + String.format("%.0f", item.getPrice()));
            titleTv.setTextSize(16);
            titleTv.setTextColor(Color.parseColor("#0F172A"));
            titleTv.setTypeface(null, Typeface.BOLD);

            TextView detailTv = new TextView(this);
            String desc = "Category: " + item.getCategory() + " | Condition: " + item.getCondition() +
                    "\nSeller: " + item.getSeller() + " (" + item.getStatus() + ")";
            if (item.isExchange()) desc += " [Exchange Allowed 🔄]";
            detailTv.setText(desc);
            detailTv.setTextSize(13);
            detailTv.setTextColor(Color.parseColor("#475569"));
            detailTv.setPadding(0, 6, 0, 10);

            LinearLayout btnBox = new LinearLayout(this);
            btnBox.setOrientation(LinearLayout.HORIZONTAL);

            Button btnChat = new Button(this);
            btnChat.setText("💬 Negotiate / Chat");
            btnChat.setTextSize(11);
            btnChat.setBackgroundColor(Color.parseColor("#2563EB"));
            btnChat.setTextColor(Color.WHITE);

            Button btnSave = new Button(this);
            btnSave.setText(item.isSaved() ? "❤️ Saved" : "🤍 Save");
            btnSave.setTextSize(11);
            btnSave.setBackgroundColor(Color.parseColor("#E2E8F0"));
            btnSave.setTextColor(Color.parseColor("#0F172A"));

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            p.setMargins(0, 0, 6, 0);
            btnChat.setLayoutParams(p);
            btnSave.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            btnChat.setOnClickListener(v -> {
                Intent intent = new Intent(MarketplaceActivity.this, ChatActivity.class);
                intent.putExtra("seller_name", item.getSeller());
                intent.putExtra("item_title", item.getTitle());
                startActivity(intent);
            });

            btnSave.setOnClickListener(v -> {
                dbHelper.toggleSaveItem(item.getId(), item.isSaved());
                renderListings();
            });

            btnBox.addView(btnChat);
            btnBox.addView(btnSave);

            layout.addView(titleTv);
            layout.addView(detailTv);
            layout.addView(btnBox);

            card.addView(layout);
            containerings.addView(card);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}