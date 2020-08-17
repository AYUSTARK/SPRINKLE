package com.ayustark.foodapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RestaurantEntity::class],version = 1)
abstract class RestaurantDatabase:RoomDatabase() {

    abstract fun RestaurantDao():RestaurantDao

}