package com.ayustark.foodapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "restaurant_cost_for_one") val costForOne: String,
    @ColumnInfo(name = "restaurant_rating") val rating: String,
    @ColumnInfo(name = "restaurant_image") val image: String
)