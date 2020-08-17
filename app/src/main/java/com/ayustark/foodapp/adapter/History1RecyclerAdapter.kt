package com.ayustark.foodapp.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayustark.foodapp.R
import com.ayustark.foodapp.model.FoodHistory
import com.ayustark.foodapp.model.OrderHistory

class History1RecyclerAdapter(val context: Context, private val historyList: List<OrderHistory>) :
    RecyclerView.Adapter<History1RecyclerAdapter.HistoryViewHolder>() {
    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestraunName: TextView = view.findViewById(R.id.txtRestarunName)
        val txtOrderDate: TextView = view.findViewById(R.id.txtOrderDate)
        val recyclerHistory2: RecyclerView = view.findViewById(R.id.RecyclerHistory2)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(view.context)
        lateinit var recyclerAdapter: History2RecyclerAdapter
        val foodList= arrayListOf<FoodHistory>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_history1, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val orderList = historyList[position]
        holder.txtRestraunName.text = orderList.resName
        holder.txtOrderDate.text = orderList.date
        for (j in 0 until orderList.items.length()) {
            val items =
                orderList.items.getJSONObject(j)
            val foodObject = FoodHistory(
                items.getString("name"),
                items.getString("cost")
            )
            holder.foodList.add(foodObject)
            //println("out ${holder.foodList}")
            holder.recyclerAdapter =
                History2RecyclerAdapter(
                    Activity() as Context,
                    holder.foodList
                )
            holder.recyclerHistory2.adapter = holder.recyclerAdapter
            holder.recyclerHistory2.layoutManager = holder.layoutManager
        }
    }

}