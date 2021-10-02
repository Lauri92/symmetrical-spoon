package fi.lauriari.ar_project

import com.google.gson.annotations.SerializedName

data class ImageQuestion(
    @SerializedName("question") val question : String,
    @SerializedName("imageSource") val imageSource : String,
    @SerializedName("answer1") val answer1 : String,
    @SerializedName("answer2") val answer2 : String,
    @SerializedName("answer3") val answer3 : String,
    @SerializedName("correctAnswer") val correctAnswer : String
) {
}