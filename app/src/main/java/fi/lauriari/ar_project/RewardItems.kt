package fi.lauriari.ar_project


// object for testing a reward list
object RewardItems {
    val rewards: MutableList<RewardItem> = java.util.ArrayList()

    init {
        rewards.add(RewardItem("King Lion",R.drawable.diamond,"King Lion? Lion King?",R.drawable.ic_baseline_pets_24,20))
        rewards.add(RewardItem("ice cream",R.drawable.diamond,"Vanilla flavored ice cream",R.drawable.ic_baseline_pets_24,10))
        rewards.add(RewardItem("Lollipop",R.drawable.diamond,"Lollipop!",R.drawable.ic_baseline_pets_24,4))
        rewards.add(RewardItem("Pikachu",R.drawable.diamond,"Can you be here?",R.drawable.ic_baseline_pets_24,30))
        rewards.add(RewardItem("Artic Fox",R.drawable.diamond,"Artic Fox item description",R.drawable.ic_baseline_pets_24,15))
    }
}