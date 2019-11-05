package com.classroom.app1.Model;

public class Categories {
    private String id_cat;
    private String name;
    private String icon;
    private String catcolor;

    public Categories() {
    }


    public Categories(String id_cat, String name, String icon, String color) {
        this.id_cat = id_cat;
        this.name = name;
        this.icon = icon;
        this.catcolor = catcolor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColorCard() {
        return catcolor;
    }

    public void setColorCard(String catcolor) {
        this.catcolor = catcolor;
    }

    public String getId_cat() {
        return id_cat;
    }
}
