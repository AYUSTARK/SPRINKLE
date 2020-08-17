package com.ayustark.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayustark.foodapp.R
import com.ayustark.foodapp.model.Menu

class MenuRecyclerAdapter(val context: Context, private val menuList: ArrayList<Menu>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {

    companion object {
        var isCartEmpty = true
    }
    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSerial: TextView = view.findViewById(R.id.txtSerial)
        val txtFoodName: TextView = view.findViewById(R.id.txtFoodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
        val btnRemove:Button=view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_single_row, parent, false)
        return MenuViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
    interface OnItemClickListener {
        fun onAddItemClick(menuItem: Menu)
        fun onRemoveItemClick(menuItem: Menu)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuList[position]
        val serial="${position+1}"
        holder.txtSerial.text=serial
        holder.txtFoodName.text=menuItem.name
        val price="Rs.${menuItem.cost_for_one}"
        holder.txtFoodPrice.text=price
        holder.btnRemove.visibility = View.GONE
        holder.btnAdd.visibility = View.VISIBLE
        holder.btnAdd.setOnClickListener {
            holder.btnAdd.visibility = View.GONE
            holder.btnRemove.visibility = View.VISIBLE
            listener.onAddItemClick(menuItem)
        }

        holder.btnRemove.setOnClickListener {
            holder.btnRemove.visibility = View.GONE
            holder.btnAdd.visibility = View.VISIBLE
            listener.onRemoveItemClick(menuItem)
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}