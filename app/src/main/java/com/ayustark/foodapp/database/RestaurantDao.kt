package com.ayustark.foodapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(RestaurantEntity: RestaurantEntity)
    @Delete
    fun deleteRestaurant(RestaurantEntity: RestaurantEntity)
    @Query("Select * from restaurants")
    fun getAllRestaurant():List<RestaurantEntity>
    @Query("Select * from restaurants where id = :restaurantId")
    fun getRestaurantById(restaurantId:String):RestaurantEntity

}