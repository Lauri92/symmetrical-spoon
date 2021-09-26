package fi.lauriari.ar_project

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RewardItem(val name:String,val img:Int, val descriptions:String, val currency:Int, val price:Int) : Parcelable {

//    val img:Int
//    val currency:Int
//    val price:Int
//    val descriptions:String
//    init {
//        this.name = name
//        this.img = img
//        this.currency = currency
//        this.price = price
//        this.descriptions = descriptions
//    }
}