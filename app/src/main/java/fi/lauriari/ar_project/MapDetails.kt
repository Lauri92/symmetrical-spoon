package fi.lauriari.ar_project

import androidx.room.*

@Entity(tableName = "mapdetails_table")
data class MapDetails(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val time: Long
)

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
    val reward: String
)

class MapDetailsWithAllLatLngValues {
    @Embedded
    var mapDetails: MapDetails? = null

    @Relation(parentColumn = "id", entityColumn = "mapDetailsId")
    var latLngValues: List<MapLatLng>? = null
}