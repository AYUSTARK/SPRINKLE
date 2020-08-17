package com.ayustark.foodapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ayustark.foodapp.R
import com.ayustark.foodapp.adapter.HomeRecyclerAdapter
import com.ayustark.foodapp.model.Restaurant
import com.ayustark.foodapp.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

class HomeFragment : Fragment() {
    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    val restraunList = arrayListOf<Restaurant>()
    private var costComparator= Comparator<Restaurant> { res1, res2 ->
        res1.cost_for_one.compareTo(res2.cost_for_one,true)
    }
    private var ratingComparator= Comparator<Restaurant> { res1, res2 ->
        res1.rating.compareTo(res2.rating,true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        recyclerHome = view.findViewById(R.id.RecyclerHome)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(activity)
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
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
                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObject =
                                    Restaurant(
                                        restaurantJsonObject.getString("id"),
                                        restaurantJsonObject.getString("name"),
                                        restaurantJsonObject.getString("rating"),
                                        restaurantJsonObject.getString("cost_for_one"),
                                        restaurantJsonObject.getString("image_url")
                                    )
                                restraunList.add(restaurantObject)
                                recyclerAdapter =
                                    HomeRecyclerAdapter(
                                        activity as Context,
                                        restraunList
                                    )
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sorting, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSort -> {
                val builder: AlertDialog.Builder = AlertDialog.Builder(activity as Context)
                builder.setTitle("Sort By")
                val customLayout: View = layoutInflater.inflate(R.layout.sorting_menu, null)
                builder.setView(customLayout)
                val radioGroup:RadioGroup=customLayout.findViewById(R.id.rbGroup)
                builder.setPositiveButton("Sort") { _, _ ->
                    val check=radioGroup.checkedRadioButtonId
                    checkRadioButton(check)
                }
                builder.setNegativeButton("Cancel"){ _, _ ->

                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun checkRadioButton(Number:Int){
        when(Number){
            R.id.rbOne->{Collections.sort(restraunList,costComparator)}
            R.id.rbTwo->{Collections.sort(restraunList,costComparator)
            restraunList.reverse()}
            R.id.rbThree->{Collections.sort(restraunList,ratingComparator)
                restraunList.reverse()}
        }
        recyclerAdapter.notifyDataSetChanged()
    }
    //recyclerAdapter.notifyDataSetChanged()
    //return super.onOptionsItemSelected(item)
}

/*private fun showPopup(view: View?) {
    var popup: PopupMenu? = null;
    popup = PopupMenu(activity, view)
    val inflater:MenuInflater=popup.menuInflater
    inflater.inflate(R.menu.sorting_menu,popup.getMenu())
    popup.show()
    //popup.inflate(R.menu.sorting_menu)

    popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

        when (item!!.itemId) {
            R.id.one -> {
                Toast.makeText(activity, item.title, Toast.LENGTH_SHORT).show();
            }
            R.id.two -> {
                Toast.makeText(activity, item.title, Toast.LENGTH_SHORT).show();
            }
            R.id.three -> {
                Toast.makeText(activity, item.title, Toast.LENGTH_SHORT).show();
            }
        }

        true
    })

    //popup.show()
}*/