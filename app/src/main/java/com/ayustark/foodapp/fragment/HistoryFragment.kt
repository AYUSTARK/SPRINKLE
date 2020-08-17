package com.ayustark.foodapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ayustark.foodapp.R
import com.ayustark.foodapp.adapter.History1RecyclerAdapter
import com.ayustark.foodapp.model.OrderHistory
import com.ayustark.foodapp.util.ConnectionManager
import org.json.JSONException

class HistoryFragment : Fragment() {
    lateinit var recyclerHistory1: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: History1RecyclerAdapter
    val orderList = arrayListOf<OrderHistory>()
    private var sharedPreferences = activity?.getSharedPreferences(
        getString(R.string.user_shared_preferences),
        Context.MODE_PRIVATE
    )
    private var userId: String? = "0"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.user_shared_preferences),
            Context.MODE_PRIVATE
        )
        userId = sharedPreferences?.getString("user_id", "0")
        //println(user_id)
        recyclerHistory1 = view.findViewById(R.id.RecyclerHistory1)
        progressLayout = view.findViewById(R.id.ProgressLayout)
        progressLayout.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(activity)
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                url, null,
                Response.Listener {
                    try {
                        //println("Response is $it")
                        val dataWhole = it.getJSONObject("data")
                        progressLayout.visibility = View.GONE

                        val success = dataWhole.getBoolean("success")
                        if (success) {

                            val data = dataWhole.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val orderJsonObject = data.getJSONObject(i)
                                val date=orderJsonObject.getString("order_placed_at")
                                val orderObject = OrderHistory(
                                    orderJsonObject.getString("order_id"),
                                    orderJsonObject.getString("restaurant_name"),
                                    orderJsonObject.getString("total_cost"),
                                    "${date[0]}${date[1]}/${date[3]}${date[4]}/20${date[6]}${date[7]}",
                                    orderJsonObject.getJSONArray("food_items")
                                )
                                orderList.add(orderObject)
                                recyclerAdapter =
                                    History1RecyclerAdapter(
                                        activity as Context,
                                        orderList
                                    )
                                recyclerHistory1.adapter=recyclerAdapter
                                recyclerHistory1.layoutManager = layoutManager
                                recyclerHistory1.addItemDecoration(DividerItemDecoration(recyclerHistory1.context,(layoutManager as LinearLayoutManager).orientation))
                            }

                        } else {
                            val error = it.getJSONObject("data").getString("errorMessage")
                            Toast.makeText(
                                activity as Context,
                                error,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        println("Error is $it")
                        Toast.makeText(
                            activity,
                            "Some Unexpected error occurred2!!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Voley error occurred!!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()

        }

        return view
    }

}