package fi.lauriari.ar_project

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

// for handling value of gems in the reward shop
class Gems(private val value: Int?, private val layoutId: Int, private val textViewId: Int) {

    val gemValue = value

    fun initPriceText(view: View) {
        // to prevent displaying wrong data when the recyclerview recycles its views
        view.findViewById<LinearLayout>(layoutId).visibility = View.VISIBLE
        if (gemValue == 0) {
            view.findViewById<LinearLayout>(layoutId).visibility = View.GONE
        } else {
            view.findViewById<TextView>(textViewId).text = value.toString()
        }
    }
}