package com.example.campushu;

public class FreelanceService {
    private String title;
    private String providerName;
    private double rate;

    public FreelanceService(String title, String providerName, double rate) {
        this.title = title;
        this.providerName = providerName;
        this.rate = rate;
    }

    public String getDetails() {
        return "🛠 [SERVICE] " + title + " - $" + rate + "/hr (By: " + providerName + ")";
    }
}