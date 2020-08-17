package com.ayustark.foodapp.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ayustark.foodapp.R
import com.ayustark.foodapp.activity.MenuActivity
import com.ayustark.foodapp.database.RestaurantDatabase
import com.ayustark.foodapp.database.RestaurantEntity
import com.ayustark.foodapp.model.Restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, private val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestraunName: TextView = view.findViewById(R.id.txtRestarunName)
        val txtRestraunPrice: TextView = view.findViewById(R.id.txtRestraunCostForOne)
        val txtRestraunRating: TextView = view.findViewById(R.id.txtRestraunRating)
        val imgRestraunImage: ImageView = view.findViewById(R.id.imgRestraun)
        val imgBtnFav: ImageView = view.findViewById(R.id.imgBtnFav)
        val llContent: LinearLayout = view.findViewById(R.id.LLContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_single_row, parent, false)
        return HomeViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtRestraunName.text = restaurant.name
        holder.txtRestraunPrice.text = restaurant.cost_for_one
        holder.txtRestraunRating.text = restaurant.rating
        Picasso.get().load(restaurant.image_url).error(R.drawable.app_logo)
            .into(holder.imgRestraunImage)
        val restaurantList = RestaurantEntity(
            restaurant.id,
            restaurant.name, restaurant.cost_for_one, restaurant.rating, restaurant.image_url
        )
        val checkFav = DBAsyncTask(context.applicationContext, restaurantList, 1).execute()
        if (checkFav.get()) {
            //println("fav ${checkFav.get()}")
            holder.imgBtnFav.setImageResource(R.drawable.ic_fav2)
        } else {
            //println("fav ${checkFav.get()}")
            holder.imgBtnFav.setImageResource(R.drawable.ic_fav1)
        }
        holder.imgBtnFav.setOnClickListener {
            if (!DBAsyncTask(context.applicationContext, restaurantList, 1).execute().get()) {
                val async =
                    DBAsyncTask(context.applicationContext, restaurantList, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant Added to Favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.imgBtnFav.setImageResource(R.drawable.ic_fav2)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val async =
                    DBAsyncTask(context.applicationContext, restaurantList, 3).execute()
                if (async.get()) {
                    Toast.makeText(
                        context,
                        "Restaurant Removed from Favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.imgBtnFav.setImageResource(R.drawable.ic_fav1)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        holder.llContent.setOnClickListener {
            val intent = Intent(context, MenuActivity::class.java)
            val bundle = Bundle()
            bundle.putString("id", restaurant.id)
            bundle.putString("name", restaurant.name)
            bundle.putString("cost", restaurant.cost_for_one)
            bundle.putString("rating", restaurant.rating)
            bundle.putString("image", restaurant.image_url)
            intent.putExtra("list", bundle)
            context.startActivity(intent)
        }
    }

    class DBAsyncTask(
        val context: Context,
        private val restaurantEntity: RestaurantEntity,
        private val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {
        /*Mode 1-> Check DB is the Restaurant in fav or not
        * Mode 2-> Save the Restaurant in DB as fav
        * Mode 3-> Remove the Restaurant from DB as fav*/
        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "Restaurant-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.RestaurantDao()
                        .getRestaurantById(restaurantEntity.id)
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.RestaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.RestaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

}