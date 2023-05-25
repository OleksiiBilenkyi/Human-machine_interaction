package com.example.lmb_3_7;

import com.google.gson.annotations.SerializedName;
import javafx.beans.property.*;

public class Product {
    @SerializedName("name")
    public String name;

    public int quantity;

    @SerializedName("location")
    public String location;

    @SerializedName("description")
    public String description;

    @SerializedName("price")
    public double price;

    public Product(String name, int quantity, String location, String description, double price) {
        this.name = name;
        this.quantity = quantity;
        this.location = location;
        this.description = description;
        this.price = price;
    }

    // Геттери для отримання значень полів товару

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }



    // Методи для створення JavaFX властивостей (Property) для полів товару

    public StringProperty nameProperty() {
        return new SimpleStringProperty(name);
    }

    public IntegerProperty quantityProperty() {
        return new SimpleIntegerProperty(quantity);
    }

    public StringProperty locationProperty() {
        return new SimpleStringProperty(location);
    }

    public StringProperty descriptionProperty() {
        return new SimpleStringProperty(description);
    }

    public DoubleProperty priceProperty() {
        return new SimpleDoubleProperty(price);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}