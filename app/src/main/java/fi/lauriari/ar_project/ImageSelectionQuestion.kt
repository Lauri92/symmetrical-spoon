package fi.lauriari.ar_project

import com.google.gson.annotations.SerializedName

class ImageSelectionQuestion(
    @SerializedName("question") val question : String,
    @SerializedName("image1") val image1 : String,
    @SerializedName("image2") val image2 : String,
    @SerializedName("image3") val image3 : String,
    @SerializedName("correctAnswer") val correctAnswer : String
) {
}