package com.example.servicelist.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.material.R.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.Currency
import java.util.Locale
import java.util.SortedMap
import java.util.TreeMap


fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
    val bottomSheet =
        bottomSheetDialog.findViewById<View>(id.design_bottom_sheet) as FrameLayout?
    val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
    /*bottomSheetDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation*/
    val layoutParams = bottomSheet.layoutParams
    if (layoutParams != null) {
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
    }
    bottomSheet.layoutParams = layoutParams
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
    behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }

    })
}

fun Context.getAssetJsonData(): String? {
    var json: String? = null
    json = try {
        val `is` = this.assets.open("item_data.json")
        val size = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()
        String(buffer, charset("UTF-8"))
    } catch (ex: IOException) {
        ex.printStackTrace()
        return null
    }
    json?.let { Log.e("data", it) }
    return json
}


internal object Utils {
    var currencyLocaleMap: SortedMap<Currency, Locale>? = null

    fun getCurrencySymbol(currencyCode: String): String {
        if (currencyCode.isEmpty()) return ""
        val currency = Currency.getInstance(currencyCode)
        val url = currency.getSymbol(currencyLocaleMap!![currency])
        return if (url.contains("US")) {
            val separator = "US"
            val sepPos: Int = url.indexOf(separator)
            url.substring(sepPos + separator.length)
        } else {
            if (currencyCode == currency.getSymbol(currencyLocaleMap!![currency])) {
                "${currency.getSymbol(currencyLocaleMap!![currency])} "
            } else {
                currency.getSymbol(currencyLocaleMap!![currency])
            }
        }
    }

    init {
        currencyLocaleMap = TreeMap { c1, c2 -> c1.currencyCode.compareTo(c2.currencyCode) }
        for (locale in Locale.getAvailableLocales()) {
            try {
                val currency = Currency.getInstance(locale)
                (currencyLocaleMap as TreeMap<Currency, Locale>)[currency] = locale
            } catch (e: Exception) {
            }
        }
    }
}

fun showHide(context: Context, view: View, show: Boolean) {
    val shortAnimTime = context.resources.getInteger(android.R.integer.config_shortAnimTime)
    view.animate().setDuration(0).alpha(
        if (show) 1F else 0F
    ).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            view.visibility = if (show) View.VISIBLE else View.GONE
        }
    })
}

 fun View.snackBar(message: String) {
     Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()

}
fun Context.toast(message: String) {
    Toast.makeText(
        this, message,
        Toast.LENGTH_LONG
    ).show()

}