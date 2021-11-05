package com.cpc1hn.uimkk

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

@SuppressLint("SimpleDateFormat")
@Throws(ParseException::class)
fun modifyDateLayout(inputDate: String): String {
    val date = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(inputDate)
    return SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date!!)

}
//@Throws(ParseException::class)
//private fun modifyDateLayout(inputDate: String): String? {
//    val date: Date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").parse(inputDate)
//    return SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(date)
//}

fun convertSectoDay(time: Int):String {
    var n = time
    var time1=""
    val hour = n / 3600
    n %= 3600
    val minutes = n / 60
    n %= 60
    val seconds = n
    time1 ="${hour}:${minutes}:${seconds}"
    if ((hour<10) && (hour!=0)&&(minutes<10) &&(seconds<10)){
        time1= "0${hour}:0${minutes}:0${seconds}"
    }else if ((hour<10) && (hour!=0)&&(minutes<10)&&(seconds>=10)){
        time1= "0${hour}:0${minutes}:${seconds}"
    }else if ((hour==0)&&(minutes<10) &&(seconds<10)){
        time1= "0${minutes}:0${seconds}"
    }else if ((hour==0)&&(minutes<10)&&(seconds>=10)){
        time1= "0${minutes}:${seconds}"
    }
    else if ((hour==0)&&(minutes>=10) &&(seconds>=10)){
        time1= "${minutes}:${seconds}"
    }

    return time1
}
fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
fun Fragment.requestLocationPermission(): Int {
    if (ContextCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
        } else {
            // request permission
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1)
        }
    } else {
        // already granted
        return 1
    }

    // not available
    return 0
}