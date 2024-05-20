package edu.uncc.giftlistapp.models;

import java.util.ArrayList;

import edu.uncc.giftlistapp.models.Products;

public class ProductRoot {
    public String status;
    public ArrayList<Products> products;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Products> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Products> products) {
        this.products = products;
    }
}
