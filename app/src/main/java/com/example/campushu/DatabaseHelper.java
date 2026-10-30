package com.example.campushu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CampusHubProduction.db";
    private static final int DATABASE_VERSION = 5;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password TEXT, role TEXT)");
        db.execSQL("CREATE TABLE services (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, provider TEXT, rate REAL, category TEXT)");
        db.execSQL("CREATE TABLE items (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, seller TEXT, price REAL, condition TEXT, category TEXT, image_uri TEXT, is_exchange INTEGER, is_saved INTEGER DEFAULT 0, status TEXT DEFAULT 'Available')");
        db.execSQL("CREATE TABLE messages (id INTEGER PRIMARY KEY AUTOINCREMENT, sender TEXT, receiver TEXT, text TEXT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS services");
        db.execSQL("DROP TABLE IF EXISTS items");
        db.execSQL("DROP TABLE IF EXISTS messages");
        onCreate(db);
    }

    // --- USER AUTHENTICATION ---
    public boolean registerUser(String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("email", email);
        v.put("password", password);
        v.put("role", role);
        return db.insert("users", null, v) != -1;
    }

    public boolean registerUser(String email, String password) {
        return registerUser(email, password, "Student");
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM users WHERE email=? AND password=?", new String[]{email, password});
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }

    // --- MARKETPLACE ITEMS ---
    public boolean addItem(String title, String seller, double price, String condition, String category, String imageUri, boolean isExchange) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("title", title);
        v.put("seller", seller);
        v.put("price", price);
        v.put("condition", condition);
        v.put("category", category);
        v.put("image_uri", imageUri);
        v.put("is_exchange", isExchange ? 1 : 0);
        v.put("status", "Available");
        return db.insert("items", null, v) != -1;
    }

    public List<MarketItemModel> getFilteredItems(String categoryQuery, String searchQuery) {
        List<MarketItemModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM items WHERE 1=1";
        List<String> args = new ArrayList<>();

        if (!categoryQuery.equals("All")) {
            query += " AND category=?";
            args.add(categoryQuery);
        }
        if (!searchQuery.isEmpty()) {
            query += " AND (title LIKE ? OR seller LIKE ?)";
            args.add("%" + searchQuery + "%");
            args.add("%" + searchQuery + "%");
        }

        Cursor c = db.rawQuery(query, args.toArray(new String[0]));
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow("id"));
                String title = c.getString(c.getColumnIndexOrThrow("title"));
                String seller = c.getString(c.getColumnIndexOrThrow("seller"));
                double price = c.getDouble(c.getColumnIndexOrThrow("price"));
                String condition = c.getString(c.getColumnIndexOrThrow("condition"));
                String category = c.getString(c.getColumnIndexOrThrow("category"));
                String imageUri = c.getString(c.getColumnIndexOrThrow("image_uri"));
                boolean isExchange = c.getInt(c.getColumnIndexOrThrow("is_exchange")) == 1;
                boolean isSaved = c.getInt(c.getColumnIndexOrThrow("is_saved")) == 1;
                String status = c.getString(c.getColumnIndexOrThrow("status"));

                list.add(new MarketItemModel(id, title, seller, price, condition, category, imageUri, isExchange, isSaved, status));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    // Added method formatted in Indian Rupees (₹)
    public List<String> getAllItemsFormatted() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM items", null);
        if (c.moveToFirst()) {
            do {
                String title = c.getString(c.getColumnIndexOrThrow("title"));
                double price = c.getDouble(c.getColumnIndexOrThrow("price"));
                String condition = c.getString(c.getColumnIndexOrThrow("condition"));
                String seller = c.getString(c.getColumnIndexOrThrow("seller"));
                int exchange = c.getInt(c.getColumnIndexOrThrow("is_exchange"));

                String itemStr = "📦 " + title + " - ₹" + String.format("%.0f", price) + "\nCondition: " + condition + " | Seller: " + seller;
                if (exchange == 1) itemStr += " [Open for Exchange 🔄]";
                list.add(itemStr);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public void toggleSaveItem(int id, boolean currentState) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("is_saved", currentState ? 0 : 1);
        db.update("items", v, "id=?", new String[]{String.valueOf(id)});
    }

    // --- FREELANCE SERVICES ---
    public boolean addService(String title, String provider, double rate, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("title", title);
        v.put("provider", provider);
        v.put("rate", rate);
        v.put("category", category);
        return db.insert("services", null, v) != -1;
    }

    public List<String> getAllServicesFormatted() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM services", null);
        if (c.moveToFirst()) {
            do {
                String title = c.getString(c.getColumnIndexOrThrow("title"));
                String provider = c.getString(c.getColumnIndexOrThrow("provider"));
                double rate = c.getDouble(c.getColumnIndexOrThrow("rate"));
                String cat = c.getString(c.getColumnIndexOrThrow("category"));
                list.add("🛠 [" + cat + "] " + title + " - ₹" + String.format("%.0f", rate) + "/hr\nProvider: " + provider);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    // --- DIRECT MESSAGING ---
    public boolean sendMessage(String sender, String receiver, String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("sender", sender);
        v.put("receiver", receiver);
        v.put("text", text);
        return db.insert("messages", null, v) != -1;
    }

    public List<String> getChatMessages() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM messages ORDER BY id ASC", null);
        if (c.moveToFirst()) {
            do {
                String sender = c.getString(c.getColumnIndexOrThrow("sender"));
                String text = c.getString(c.getColumnIndexOrThrow("text"));
                list.add(sender + ": " + text);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }
}