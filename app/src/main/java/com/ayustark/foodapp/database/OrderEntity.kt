package com.ayustark.foodapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Orders")
data class OrderEntity(
    @PrimaryKey val food_item_id:String,
    val foodName:String,
    val cost_for_one:String
)
