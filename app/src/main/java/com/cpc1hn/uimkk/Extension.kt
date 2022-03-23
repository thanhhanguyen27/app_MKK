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
    if (inputDate.isNotEmpty()){
        val date = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(inputDate)
        return SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date!!)
    }else return ""
}

@SuppressLint("SimpleDateFormat")
fun String.convertStringToDate() : Date? {
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    sdf.timeZone = TimeZone.getTimeZone("GMT+7")
    return if(this.isEmpty()){
        null
    }else {
        sdf.parse(this)
    }
}

@SuppressLint("SimpleDateFormat")
fun Int.convertStringToDateHHMMSS() : Date? {
    val sdf = SimpleDateFormat("HH:mm:ss")
    sdf.timeZone = TimeZone.getTimeZone("GMT+7")
    return if(this==0){
        null
    }else {
        sdf.parse(this.toString())
    }
}

@SuppressLint("SimpleDateFormat")
fun String.convertStringToDateHHMMSS() : Date? {
    val sdf = SimpleDateFormat("HH:mm:ss")
    sdf.timeZone = TimeZone.getTimeZone("GMT+7")
    return if(this.isEmpty()){
        null
    }else {
        sdf.parse(this)
    }
}


fun dateToLong(date: String?, fomat: String?): Long {
    var milliseconds: Long = -1
    val f = SimpleDateFormat(fomat)
    f.timeZone = TimeZone.getDefault()
    try {
        val d = f.parse(date)
        milliseconds = d.time
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return milliseconds
}

fun Fragment.showDialogShort(title: String, message: String?) {
    val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
    builder.setTitle(title)
    message?.let {
        builder.setMessage(message)
    }
    builder.setPositiveButton("OK") { dialogInterface, i ->
    }
        .create()
        .show()
}


fun convertSecToTime(seconds: Int):String {
    val h = seconds / 3600
    val m = seconds % 3600 / 60
    val s = seconds % 3600 % 60
    return if (h <= 0){
        String.format("%02d:%02d", m, s)
    }else{
        String.format("%02d:%02d:%02d", h, m, s)
    }
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