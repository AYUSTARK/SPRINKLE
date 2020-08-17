package com.ayustark.foodapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {
    @Insert
    fun insertFood(orderEntity: OrderEntity)
    @Delete
    fun deleteFood(orderEntity: OrderEntity)
    @Query("Select * from Orders" )
    fun getAllOrders():List<OrderEntity>
    @Query("Delete from Orders")
    fun deleteAll()

}