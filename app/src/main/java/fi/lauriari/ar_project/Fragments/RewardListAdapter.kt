package fi.lauriari.ar_project.Fragments

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fi.lauriari.ar_project.Item
import fi.lauriari.ar_project.R

class RewardListAdapter(private val items: List<Item>?) :
    RecyclerView.Adapter<RewardListAdapter.RewardListViewHolder>() {

    class RewardListViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardListViewHolder {
        return RewardListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.reward_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RewardListViewHolder, position: Int) {
        val item = items?.get(position)

        holder.itemView.findViewById<TextView>(R.id.item_name).text = item?.itemName
        if (item?.emerald == 0) {
            holder.itemView.findViewById<LinearLayout>(R.id.emerald_counter).visibility = View.GONE
        } else {
            holder.itemView.findViewById<TextView>(R.id.emerald_amount).text =
                item?.emerald.toString()
        }
        Glide.with(holder.itemView.context).load(item?.thumbnail).placeholder(R.drawable.ic_reward_item_placeholder)
            .into(holder.itemView.findViewById(R.id.item_thumbnail))
//        holder.itemView.findViewById<ImageView>(R.id.game_money)
//            .setImageResource(currentItem.currency)
//        holder.itemView.findViewById<TextView>(R.id.item_price).text = currentItem.price.toString()
        holder.itemView.findViewById<View>(R.id.reward_item_layout).setOnClickListener {
//            val action =
//                RewardListFragmentDirections.actionRewardListFragmentToRewardItemDescription(
//                    currentItem
//                )
//            it.findNavController().navigate(action)
            Log.d("clicked", "clicked ${item?.itemName}")
        }
        holder.itemView.isEnabled = false
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

//    private fun hideCounter(counter:String){
//        if (item[counter] == 0) {
//            holder.itemView.findViewById<LinearLayout>(id[counter]).visibility = View.GONE
//        } else {
//            holder.itemView.findViewById<TextView>(id.emerald_amount).text =
//                item?.emerald.toString()
//        }
//
//        if (item?.(counter) == 0) {
//            holder.itemView.findViewById<LinearLayout>(id.topaz_counter).visibility = View.GONE
//        } else {
//            val selector = id._amount
//            holder.itemView.findViewById<TextView>(selector).text =
//                item?.topaz.toString()
//        }
//    }

}