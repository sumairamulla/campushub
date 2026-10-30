package com.example.campushu;

public class MarketItemModel {
    private int id;
    private String title;
    private String seller;
    private double price;
    private String condition;
    private String category;
    private String imageUri;
    private boolean isExchange;
    private boolean isSaved;
    private String status;

    public MarketItemModel(int id, String title, String seller, double price, String condition, String category, String imageUri, boolean isExchange, boolean isSaved, String status) {
        this.id = id;
        this.title = title;
        this.seller = seller;
        this.price = price;
        this.condition = condition;
        this.category = category;
        this.imageUri = imageUri;
        this.isExchange = isExchange;
        this.isSaved = isSaved;
        this.status = status;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSeller() { return seller; }
    public double getPrice() { return price; }
    public String getCondition() { return condition; }
    public String getCategory() { return category; }
    public String getImageUri() { return imageUri; }
    public boolean isExchange() { return isExchange; }
    public boolean isSaved() { return isSaved; }
    public String getStatus() { return status; }
}