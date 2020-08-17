package com.ayustark.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayustark.foodapp.R
import com.ayustark.foodapp.database.OrderEntity

class CartRecyclerAdapter(val context: Context, private val foodList:List<OrderEntity>):RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {
    class CartViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtFoodName:TextView=view.findViewById(R.id.txtFoodName)
        val txtFoodPrice:TextView=view.findViewById(R.id.txtFoodPrice)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart, parent, false)
        return CartViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val orderlist=foodList[position]
        holder.txtFoodName.text=orderlist.foodName
        val price="Rs.${orderlist.cost_for_one}"
        holder.txtFoodPrice.text=price
    }
}