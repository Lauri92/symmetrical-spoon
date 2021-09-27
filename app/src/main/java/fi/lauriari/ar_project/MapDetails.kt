package fi.lauriari.ar_project

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "mapdetails_table")
data class MapDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val time: Long
)

@Parcelize
@Entity(
    tableName = "maplatlng_table",
    foreignKeys = [ForeignKey(
        entity = MapDetails::class,
        onDelete = ForeignKey.CASCADE,
        parentColumns = ["id"],
        childColumns = ["mapDetailsId"]
    )]
)
data class MapLatLng(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val mapDetailsId: Long,
    val lat: Double,
    val lng: Double,
    val address: String,
    val reward: String,
    var isActive: Boolean
) : Parcelable

class MapDetailsWithAllLatLngValues {
    @Embedded
    var mapDetails: MapDetails? = null

    @Relation(parentColumn = "id", entityColumn = "mapDetailsId")
    var latLngValues: List<MapLatLng>? = null
}