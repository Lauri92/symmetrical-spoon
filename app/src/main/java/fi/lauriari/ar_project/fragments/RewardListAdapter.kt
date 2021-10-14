package fi.lauriari.ar_project.fragments


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fi.lauriari.ar_project.Gems
import fi.lauriari.ar_project.network.Item
import fi.lauriari.ar_project.R

class RewardListAdapter(private val items: List<Item>?, private val collectedItems: List<String>) :
    RecyclerView.Adapter<RewardListAdapter.RewardListViewHolder>() {

    class RewardListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardListViewHolder {
        return RewardListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.reward_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RewardListViewHolder, position: Int) {
        // reward item list fetched from the JSON file
        val item = items?.get(position)

        // set a preview image of the reward item by using Glide library
        Glide.with(holder.itemView.context).load(item?.thumbnail)
            .placeholder(R.drawable.ic_reward_item_placeholder)
            .error(R.drawable.ic_reward_item_placeholder)
            .into(holder.itemView.findViewById(R.id.item_thumbnail))

        // set a name of the reward item
        holder.itemView.findViewById<TextView>(R.id.item_name).text = item?.itemName

        // set price of the item with gems
        // If the value of a gem is 0, a price container of the gem is gone from the view holder.
        val gemValues = arrayOf(
            Gems(item?.itemEmerald, R.id.emerald_counter, R.id.emerald_amount),
            Gems(item?.itemRuby, R.id.ruby_counter, R.id.ruby_amount),
            Gems(item?.itemSapphire, R.id.sapphire_counter, R.id.sapphire_amount),
            Gems(item?.itemTopaz, R.id.topaz_counter, R.id.topaz_amount),
            Gems(item?.itemDiamond, R.id.diamond_counter, R.id.diamond_amount)
        )
        gemValues.forEach { gem -> gem.initPriceText(holder.itemView) }

        // move to the detailed description(purchase) view for each items
        holder.itemView.findViewById<View>(R.id.reward_item_layout).setOnClickListener {
            val action = RewardListFragmentDirections.actionRewardListFragmentToRewardItemDescription(item!!)
            it.findNavController().navigate(action)
        }

        // to prevent showing wrong data when the recyclerview recycles its views
        holder.itemView.findViewById<ConstraintLayout>(R.id.reward_item_layout).foreground = null
        // If there are items that the user already purchased, disable clicking those items with the foreground image
        collectedItems.forEach {
            if (it == item?.itemName) {
                holder.itemView.isClickable = false
                holder.itemView.findViewById<ConstraintLayout>(R.id.reward_item_layout).foreground =
                    ContextCompat.getDrawable(holder.itemView.context, R.drawable.sold_out_filter)
            }
        }
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }
}