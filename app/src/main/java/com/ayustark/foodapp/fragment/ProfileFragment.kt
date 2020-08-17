package com.ayustark.foodapp.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ayustark.foodapp.R

class ProfileFragment : Fragment() {
    private lateinit var txtUserName:TextView
    private lateinit var txtUserMobileNumber:TextView
    private lateinit var txtUserEmail:TextView
    private lateinit var txtUserAddress:TextView
    private var sharedPreferences=activity?.getSharedPreferences(getString(R.string.user_shared_preferences), Context.MODE_PRIVATE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences=activity?.getSharedPreferences(getString(R.string.user_shared_preferences), Context.MODE_PRIVATE)
        //println(sharedPreferences?.getString("name","ff"))
        val view=inflater.inflate(R.layout.fragment_profile, container, false)
        txtUserName=view.findViewById(R.id.txtUserName)
        txtUserMobileNumber=view.findViewById(R.id.txtUserMobileNumber)
        txtUserEmail=view.findViewById(R.id.txtUserEmail)
        txtUserAddress=view.findViewById(R.id.txtUserAddress)
        txtUserName.text= sharedPreferences?.getString("name","ABC")
        val mobile="+91- ${sharedPreferences?.getString("mobile_number","0")}"
        txtUserMobileNumber.text=mobile
        txtUserEmail.text=sharedPreferences?.getString("email","@")
        txtUserAddress.text=sharedPreferences?.getString("address","xyz")
        return view
    }
}