package fi.lauriari.ar_project.network

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

@Parcelize
class Item(
    @SerializedName("id") val itemId: Long,
    @SerializedName("name") val itemName: String,
    @SerializedName("description") val description: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("objectUrl") val objectUrl: String,
    @SerializedName("isAnimated") val isAnimated: Boolean,
    @SerializedName("emerald") val itemEmerald: Int,
    @SerializedName("ruby") val itemRuby: Int,
    @SerializedName("sapphire") val itemSapphire: Int,
    @SerializedName("topaz") val itemTopaz: Int,
    @SerializedName("diamond") val itemDiamond: Int
) : Parcelable


object RewardsRetrofit {
    private const val REWARDS_URL = "https://users.metropolia.fi/~minjic/AR_project/data/"

    private val rewardsRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(REWARDS_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    val rewardsApi: RewardsApi by lazy {
        rewardsRetrofit.create(RewardsApi::class.java)
    }
}


interface RewardsApi {
    @GET("rewards.json")
    suspend fun getItems(): List<Item>
}

class RewardsRepository {
    suspend fun getItems(): List<Item> {
        return RewardsRetrofit.rewardsApi.getItems()
    }
}

class RewardsApiViewModel(private val repository: RewardsRepository) : ViewModel() {

    val response: MutableLiveData<List<Item>> = MutableLiveData()

    fun getItems() {
        viewModelScope.launch {
            response.value = repository.getItems()
            response.value?.forEach {
                Log.d("response", "${it.itemId}: ${it.itemName}")
            }
        }
    }
}

class RewardsApiViewModelFactory(private val repository: RewardsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RewardsApiViewModel(repository) as T
    }
}