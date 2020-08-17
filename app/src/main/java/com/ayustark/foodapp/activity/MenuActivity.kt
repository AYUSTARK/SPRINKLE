package com.ayustark.foodapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ayustark.foodapp.R
import com.ayustark.foodapp.adapter.MenuRecyclerAdapter
import com.ayustark.foodapp.database.OrderEntity
import com.ayustark.foodapp.database.OrdersDatabase
import com.ayustark.foodapp.database.RestaurantDatabase
import com.ayustark.foodapp.database.RestaurantEntity
import com.ayustark.foodapp.model.Menu
import com.ayustark.foodapp.util.ConnectionManager
import org.json.JSONException

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MenuActivity : AppCompatActivity() {
    lateinit var recyclerDetail: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: MenuRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    private lateinit var toolbar: Toolbar
    private lateinit var imgBtnFav: ImageView
    lateinit var btnCart: Button
    val menuList = arrayListOf<Menu>()
    var orderList = arrayListOf<Menu>()
    var totalPrice: Int = 0
    private var restrauntId: String = "a"
    private var title: String = "Menu"
    private var cost: String = "0"
    private var rating: String = "0"
    private var image: String = "a"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        recyclerDetail = findViewById(R.id.RecyclerMenu)
        progressLayout = findViewById(R.id.progressLayout)
        imgBtnFav = findViewById(R.id.imgBtnFav)
        btnCart = findViewById(R.id.btnCart)
        toolbar = findViewById(R.id.Toolbar)
        setUpToolbar()
        progressLayout.visibility = View.VISIBLE
        btnCart.visibility = View.GONE
        layoutManager = LinearLayoutManager(this@MenuActivity)
        btnCart.setOnClickListener {
            proceedToCart()
        }
        OrdersOfCart(this@MenuActivity, OrderEntity("hg", "ghh", "hgjgg"), 3).execute()
        if (intent != null) {
            val list = intent.getBundleExtra("list")
            restrauntId = list.getString("id", "a")
            title = list.getString("name", "Menu")
            cost = list.getString("cost", "0")
            rating = list.getString("rating", "0")
            image = list.getString("image", "a")
            supportActionBar?.title = title
            //println(restrauntId+"\n"+title)
        } else {
            finish()
            Toast.makeText(
                this@MenuActivity,
                "Some unexpected error occurred01!!",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (restrauntId == "a") {
            finish()
            Toast.makeText(
                this@MenuActivity,
                "Some unexpected error occurred02!!",
                Toast.LENGTH_SHORT
            ).show()
        }
        val restaurantList = RestaurantEntity(
            restrauntId,
            title, cost, rating, image
        )
        val checkFav = Favourites(
            applicationContext,
            restaurantList,
            1
        ).execute()
        if (checkFav.get()) {
            //println("fav ${checkFav.get()}")
            imgBtnFav.setImageResource(R.drawable.ic_fav2)
        } else {
            //println("fav ${checkFav.get()}")
            imgBtnFav.setImageResource(R.drawable.ic_fav1)
        }
        imgBtnFav.setOnClickListener {
            if (!Favourites(applicationContext, restaurantList, 1).execute().get()) {
                val async =
                    Favourites(applicationContext, restaurantList, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        this@MenuActivity,
                        "Restaurant Added to Favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    imgBtnFav.setImageResource(R.drawable.ic_fav2)
                } else {
                    Toast.makeText(
                        this@MenuActivity,
                        "Some error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val async =
                    Favourites(applicationContext, restaurantList, 3).execute()
                if (async.get()) {
                    Toast.makeText(
                        this@MenuActivity,
                        "Restaurant Removed from Favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    imgBtnFav.setImageResource(R.drawable.ic_fav1)
                } else {
                    Toast.makeText(
                        this@MenuActivity,
                        "Some error occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        val queue = Volley.newRequestQueue(this@MenuActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restrauntId"
        if (ConnectionManager().checkConnectivity(this@MenuActivity)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                url, null,
                Response.Listener {
                    try {
                        val dataWhole = it.getJSONObject("data")
                        progressLayout.visibility = View.GONE
                        val success = dataWhole.getBoolean("success")
                        if (success) {
                            val data = dataWhole.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val menuJsonObject = data.getJSONObject(i)
                                val menuObject =
                                    Menu(
                                        menuJsonObject.getString("id"),
                                        menuJsonObject.getString("name"),
                                        menuJsonObject.getString("cost_for_one"),
                                        menuJsonObject.getString("restaurant_id")
                                    )
                                menuList.add(menuObject)
                                recyclerAdapter =
                                    MenuRecyclerAdapter(
                                        this@MenuActivity,
                                        menuList, object : MenuRecyclerAdapter.OnItemClickListener {
                                            override fun onAddItemClick(menuItem: Menu) {
                                                totalPrice += menuItem.cost_for_one.toInt()
                                                orderList.add(menuItem)
                                                val order = OrderEntity(
                                                    menuItem.food_item_id,
                                                    menuItem.name,
                                                    menuItem.cost_for_one
                                                )
                                                OrdersOfCart(
                                                    this@MenuActivity,
                                                    order,
                                                    1
                                                ).execute().get()
                                                if (orderList.size > 0) {
                                                    btnCart.visibility = View.VISIBLE
                                                    MenuRecyclerAdapter.isCartEmpty = false
                                                }
                                            }

                                            override fun onRemoveItemClick(menuItem: Menu) {
                                                totalPrice -= menuItem.cost_for_one.toInt()
                                                orderList.remove(menuItem)
                                                val order = OrderEntity(
                                                    menuItem.food_item_id,
                                                    menuItem.name,
                                                    menuItem.cost_for_one
                                                )
                                                OrdersOfCart(this@MenuActivity, order, 2).execute()
                                                if (orderList.isEmpty()) {
                                                    btnCart.visibility = View.GONE
                                                    MenuRecyclerAdapter.isCartEmpty = true
                                                }
                                            }
                                        }
                                    )
                                recyclerDetail.adapter = recyclerAdapter
                                recyclerDetail.layoutManager = layoutManager
                            }
                        } else {
                            val error = it.getJSONObject("data").getString("errorMessage")
                            Toast.makeText(
                                this@MenuActivity,
                                error,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        println("Error is $it")
                        Toast.makeText(
                            this@MenuActivity,
                            "Some Unexpected error occurred2!!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        this@MenuActivity,
                        "Voley error occurred!!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "7d090d7ff9230d"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(this@MenuActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this@MenuActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun proceedToCart() {
        val intent = Intent(this@MenuActivity, CartActivity::class.java)
        intent.putExtra("Restaurant Id", restrauntId)
        intent.putExtra("totalCost", totalPrice)
        intent.putExtra("Name", title)
        startActivity(intent)
        finish()
    }

    class OrdersOfCart(
        val context: Context,
        private val orderEntity: OrderEntity,
        private val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, OrdersDatabase::class.java, "order-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.CartDao().insertFood(orderEntity)
                    db.close()
                    return true
                }
                2 -> {
                    db.CartDao().deleteFood(orderEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.clearAllTables()
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    class Favourites(
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

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        totalPrice = 0
        orderList.clear()
        onBackPressed()
        finish()
        return true
    }
}