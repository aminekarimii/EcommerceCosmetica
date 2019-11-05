package com.classroom.app1.Model;

import java.util.ArrayList;

public class Order {
    private String id_order;
    private ArrayList<Product> products;
    private String id_user;
    private Long state;

    public Order(String id_order, ArrayList<Product> products, String id_user, Long state) {
        this.id_order = id_order;
        this.products = products;
        this.id_user = id_user;
        this.state = state;
    }

    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public String getId_user() {
        return id_user;
    }

    public Long getState() {
        return state;
    }
}
