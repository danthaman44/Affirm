package com.affirm.takehome.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Restaurant {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "image")
    public String image;

    @ColumnInfo(name = "rating")
    public String rating;

    public Restaurant(String id, String name, String image, String rating) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.rating = rating;
    }
}