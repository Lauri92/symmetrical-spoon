package fi.lauriari.ar_project.Fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import fi.lauriari.ar_project.R
import fi.lauriari.ar_project.RewardItems

class RewardListAdapter : RecyclerView.Adapter<RewardListAdapter.RewardListViewHolder>() {

    class RewardListViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardListViewHolder {
        return RewardListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.reward_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RewardListViewHolder, position: Int) {
        val currentItem = RewardItems.rewards[position]
        holder.itemView.findViewById<ImageView>(R.id.item_thumbnail)
            .setImageResource(currentItem.img)
        holder.itemView.findViewById<TextView>(R.id.item_name).text = currentItem.name
        holder.itemView.findViewById<ImageView>(R.id.game_money)
            .setImageResource(currentItem.currency)
        holder.itemView.findViewById<TextView>(R.id.item_price).text = currentItem.price.toString()
        holder.itemView.findViewById<View>(R.id.reward_item_layout).setOnClickListener {
            val action =
                RewardListFragmentDirections.actionRewardListFragmentToRewardItemDescription(
                    currentItem
                )
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return RewardItems.rewards.size
    }

}