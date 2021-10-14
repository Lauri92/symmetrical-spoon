package fi.lauriari.ar_project.datamodels

import com.google.gson.annotations.SerializedName

class QuizQuestion(
    @SerializedName("response_code") val response_code: Int,
    @SerializedName("results") val results: List<Results>
)

data class Results(
    @SerializedName("category") val category: String,
    @SerializedName("type") val type: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("question") val question: String,
    @SerializedName("correct_answer") val correct_answer: String,
    @SerializedName("incorrect_answers") val incorrect_answers: List<String>
)