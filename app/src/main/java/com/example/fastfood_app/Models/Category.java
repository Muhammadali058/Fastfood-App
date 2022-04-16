package com.example.fastfood_app.Models;

public class Category {

    String categoryName;
    int image;

    public Category() {
    }

    public Category(String categoryName, int image) {
        this.categoryName = categoryName;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
