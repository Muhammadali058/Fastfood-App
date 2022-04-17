package com.example.fastfood_app.Models;

import androidx.annotation.Nullable;

public class Cart {
    String id, itemname, imageUrl;
    float price;
    int qty;

    public Cart() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Cart cart = (Cart) obj;
        if(this.id == cart.getId())
            return true;
        else
            return false;
    }
}
