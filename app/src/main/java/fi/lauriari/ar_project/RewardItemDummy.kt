package fi.lauriari.ar_project

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// class for testing the reward list
@Parcelize
class RewardItemDummy(val name:String, val img:Int, val descriptions:String, val currency:Int, val price:Int) : Parcelable