package com.example.fastfood_app.Models;

public class Item {

    String itemname;
    float price;
    int image;

    public Item() {
    }

    public Item(String itemname, float price, int image) {
        this.itemname = itemname;
        this.price = price;
        this.image = image;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
