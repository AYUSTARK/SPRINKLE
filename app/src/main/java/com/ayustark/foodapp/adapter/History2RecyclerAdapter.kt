package com.ayustark.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayustark.foodapp.R
import com.ayustark.foodapp.model.FoodHistory

class History2RecyclerAdapter(val context: Context, private val food:ArrayList<FoodHistory>):RecyclerView.Adapter<History2RecyclerAdapter.HistoryViewHolder>() {
    class HistoryViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtFoodName: TextView =view.findViewById(R.id.txtFoodName)
        val txtFoodPrice:TextView=view.findViewById(R.id.txtFoodPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_history2, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return food.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val foodList=food[position]
        //println("in ${foodList}")
        holder.txtFoodName.text=foodList.foodName
        holder.txtFoodPrice.text=foodList.foodPrice

    }
}