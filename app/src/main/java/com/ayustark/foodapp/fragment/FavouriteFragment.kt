package com.ayustark.foodapp.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ayustark.foodapp.R
import com.ayustark.foodapp.adapter.FavouriteRecyclerAdapter
import com.ayustark.foodapp.database.RestaurantDatabase
import com.ayustark.foodapp.database.RestaurantEntity

class FavouriteFragment : Fragment() {
    private lateinit var recyclerFavourite: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    private lateinit var progressLayout: RelativeLayout
    private var restaurantList = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        recyclerFavourite = view.findViewById(R.id.RecyclerFav)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        layoutManager=LinearLayoutManager(activity)
        restaurantList = RetrieveFavourites(activity as Context).execute().get()
        if (activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter =
                FavouriteRecyclerAdapter(activity as Context, restaurantList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }
        return view
    }

    class RetrieveFavourites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {

        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "Restaurant-db").build()
            return db.RestaurantDao().getAllRestaurant()
        }
    }
}
