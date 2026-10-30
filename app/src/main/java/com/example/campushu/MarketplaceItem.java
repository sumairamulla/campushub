package com.example.campushu;

public class MarketplaceItem {
    private String title;
    private String sellerName;
    private double price;

    public MarketplaceItem(String title, String sellerName, double price) {
        this.title = title;
        this.sellerName = sellerName;
        this.price = price;
    }

    public String getDetails() {
        return "📦 [ITEM] " + title + " - $" + price + " (Seller: " + sellerName + ")";
    }
}