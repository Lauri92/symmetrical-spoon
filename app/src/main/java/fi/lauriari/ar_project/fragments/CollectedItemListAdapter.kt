package fi.lauriari.ar_project.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fi.lauriari.ar_project.entities.CollectedItem
import fi.lauriari.ar_project.R


class CollectedItemListAdapter(private val items: List<CollectedItem>) :
    RecyclerView.Adapter<CollectedItemListAdapter.CollectedItemListViewHolder>() {

    class CollectedItemListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectedItemListViewHolder {
        return CollectedItemListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.collected_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CollectedItemListViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.findViewById<TextView>(R.id.item_name).text = item.name

        Glide.with(holder.itemView.context).load(item.thumbnail)
            .placeholder(R.drawable.ic_reward_item_placeholder)
            .error(R.drawable.ic_reward_item_placeholder)
            .into(holder.itemView.findViewById(R.id.item_thumbnail))

        holder.itemView.findViewById<View>(R.id.collected_item_layout).setOnClickListener {
            val action =
                CollectedItemListFragmentDirections.actionCollectedItemListToCollectionDescriptionFragment(
                    item
                )
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
