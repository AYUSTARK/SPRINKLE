package com.ayustark.foodapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ayustark.foodapp.R
import com.ayustark.foodapp.adapter.CartRecyclerAdapter
import com.ayustark.foodapp.database.OrderEntity
import com.ayustark.foodapp.database.OrdersDatabase
import com.ayustark.foodapp.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    private var restaurantId: String? = "b"
    private var resName: String? = "Res"
    private var userId: String? = "z"
    private var orderList = listOf<OrderEntity>()
    private lateinit var toolbar: Toolbar
    private lateinit var txtRestarunName: TextView
    private lateinit var recyclerCart: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var btnPlaceOrder: Button
    lateinit var rlFinal: RelativeLayout
    private lateinit var btnOK: Button
    private lateinit var sharedPreferences: SharedPreferences

    private var totalCost: Int? = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        toolbar = findViewById(R.id.Toolbar)
        setUpToolbar()
        txtRestarunName = findViewById(R.id.txtRestarunName)
        recyclerCart = findViewById(R.id.RecyclerCart)
        layoutManager = LinearLayoutManager(this@CartActivity)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        rlFinal = findViewById(R.id.rlFinal)
        btnOK = findViewById(R.id.btnOK)
        sharedPreferences =
            getSharedPreferences(getString(R.string.user_shared_preferences), Context.MODE_PRIVATE)
        if (intent != null) {
            restaurantId = intent.getStringExtra("Restaurant Id")
            resName = intent.getStringExtra("Name")
            totalCost = intent.getIntExtra("totalCost", 0)
        } else {
            //finish()
            Toast.makeText(
                this@CartActivity,
                "Some unexpected error occurred01!!",
                Toast.LENGTH_SHORT
            ).show()
        }
        userId = sharedPreferences.getString("user_id", "z")
        if (restaurantId == "a" || resName == "Res" || totalCost == 0 || userId == "z") {
            //finish()
            Toast.makeText(
                this@CartActivity,
                "Some unexpected error occurred02!!",
                Toast.LENGTH_SHORT
            ).show()
        }
        orderList = ExtractOrders(this@CartActivity).execute().get()
        txtRestarunName.text = resName
        val buttonText = "PLACE ORDER(Total: Rs.$totalCost)"
        btnPlaceOrder.text = buttonText
        recyclerAdapter = CartRecyclerAdapter(this@CartActivity, orderList)
        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager = layoutManager
        btnPlaceOrder.setOnClickListener {
            val jsonParams = params()
            sendRequest(jsonParams)
            //println("its $jsonParams")
            rlFinal.visibility = View.VISIBLE
            btnPlaceOrder.visibility = View.GONE
        }
        btnOK.setOnClickListener {
            val intent=Intent(this@CartActivity,HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    class ExtractOrders(
        val context: Context
    ) : AsyncTask<Void, Void, List<OrderEntity>>() {
        val db =
            Room.databaseBuilder(context, OrdersDatabase::class.java, "order-db").build()

        override fun doInBackground(vararg params: Void?): List<OrderEntity> {

            val result = db.CartDao().getAllOrders()
            db.close()
            return result
        }
    }

    class Clear(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val db =
                Room.databaseBuilder(context, OrdersDatabase::class.java, "orders-db").build()
            db.clearAllTables()
            db.close()
            return true
        }

    }

    private fun params(): JSONObject {
        val jsonParams = JSONObject()
        jsonParams.put("user_id", userId)
        jsonParams.put("restaurant_id", restaurantId)
        jsonParams.put("total_cost", totalCost)
        val foodArray = JSONArray()
        for (i in 0 until orderList.size) {
            val foodId = JSONObject()
            foodId.put("food_item_id", orderList[i].food_item_id)
            foodArray.put(i, foodId)
        }
        jsonParams.put("food", foodArray)
        return jsonParams
    }

    private fun sendRequest(jsonParams: JSONObject) {
        val queue = Volley.newRequestQueue(this@CartActivity)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        if (ConnectionManager().checkConnectivity(this@CartActivity)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                url, jsonParams,
                Response.Listener {
                    try {
                        val success =
                            it.getJSONObject("data")
                                .getBoolean("success")
                        if (success) {
                            Toast.makeText(
                                this@CartActivity,
                                "Your Order have been Placed",
                                Toast.LENGTH_SHORT
                            ).show()
                            btnPlaceOrder.visibility=View.GONE
                            rlFinal.visibility=View.VISIBLE
                            Clear(this@CartActivity).execute()
                        } else {
                            val error = it.getJSONObject("data").getString("errorMessage")
                            Toast.makeText(this@CartActivity, error, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception){
                        Toast.makeText(this@CartActivity,"Error is $e",Toast.LENGTH_SHORT).show()
                    }
                },Response.ErrorListener {
                    Toast.makeText(
                        this@CartActivity,
                        "Volley error occurred!!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "7d090d7ff9230d"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }else {
            val dialog = AlertDialog.Builder(this@CartActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent =
                    Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this@CartActivity)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        Clear(this@CartActivity).execute()
        onBackPressed()
        finish()
        return true
    }

    override fun onBackPressed() {
        Clear(this@CartActivity)
        super.onBackPressed()
    }
}