package com.ayustark.foodapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities =[OrderEntity::class],version = 1 )
abstract class OrdersDatabase:RoomDatabase() {
    abstract fun CartDao():CartDao
}