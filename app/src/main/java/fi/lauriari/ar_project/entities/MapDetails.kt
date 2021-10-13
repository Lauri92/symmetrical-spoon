package fi.lauriari.ar_project.entities

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize


@Entity(tableName = "mapdetails_table")
data class MapDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val time: Long,
    var collectedEmeralds: Int,
    var collectedRubies: Int,
    var collectedSapphires: Int,
    var collectedTopazes: Int,
    var collectedDiamonds: Int,
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
    val gameType: String,
    var isActive: Boolean
) : Parcelable

class MapDetailsWithAllLatLngValues {
    @Embedded
    var mapDetails: MapDetails? = null

    @Relation(parentColumn = "id", entityColumn = "mapDetailsId")
    var latLngValues: List<MapLatLng>? = null
}