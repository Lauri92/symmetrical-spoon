package fi.lauriari.ar_project

//data class RewardItemData(val items:)

// object for testing a reward list
object RewardItems {
    val rewards: MutableList<RewardItemDummy> = java.util.ArrayList()

    init {
        rewards.add(RewardItemDummy("King Lion",R.drawable.diamond,"King Lion? Lion King?",R.drawable.ic_baseline_pets_24,20))
        rewards.add(RewardItemDummy("ice cream",R.drawable.diamond,"Vanilla flavored ice cream",R.drawable.ic_baseline_pets_24,10))
        rewards.add(RewardItemDummy("Lollipop",R.drawable.diamond,"Lollipop!",R.drawable.ic_baseline_pets_24,4))
        rewards.add(RewardItemDummy("Pikachu",R.drawable.diamond,"Can you be here?",R.drawable.ic_baseline_pets_24,30))
        rewards.add(RewardItemDummy("Artic Fox",R.drawable.diamond,"Artic Fox item description",R.drawable.ic_baseline_pets_24,15))
    }

    const val url = "http://users.metropolia.fi/~minjic/AR_project/data/"

//    interface ApiService{
//        @GET("items.json")
//        fun getItems():Call<ResponseData<List<RewardItem>>>
//    }
}