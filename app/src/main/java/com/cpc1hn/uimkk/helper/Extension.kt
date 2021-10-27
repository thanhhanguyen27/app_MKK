package com.cpc1hn.uimkk.helper

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng


fun Fragment.showToast(message: String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//    if(view != null){
//        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
//    }else {
//
//    }
}

fun Fragment.hideKeyboard(){
    val inputMethodManager = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view?.applicationWindowToken,0)
}

fun Fragment.backPressed(){
    requireActivity().onBackPressed()
}

fun Fragment.getMapDirectionURL(from: LatLng, to: LatLng, directionsMode: String): String{
    val origin = "origin=${from.latitude},${from.longitude}"
    val dest = "destination=${to.latitude},${to.longitude}"
    val mode = "mode=$directionsMode"
    val params = "$origin&$dest&${mode}"
    return "https://maps.googleapis.com/maps/api/directions/json?${params}&key=AIzaSyDWfO03Mmk3_yLn-F-AccofJdWjC04XAns"
}


fun AppCompatActivity.showToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.hideKeyboard(){
    val inputMethodManager = applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(window.decorView.applicationWindowToken,0)
}

