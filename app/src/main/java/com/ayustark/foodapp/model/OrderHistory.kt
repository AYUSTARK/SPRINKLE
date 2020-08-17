package com.ayustark.foodapp.model

import org.json.JSONArray

data class OrderHistory(
    val orderID: String,
    val resName: String,
    val total: String,
    val date: String,
    val items: JSONArray
)