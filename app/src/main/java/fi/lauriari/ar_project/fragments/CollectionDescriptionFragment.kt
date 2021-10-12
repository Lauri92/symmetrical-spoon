package fi.lauriari.ar_project.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.lauriari.ar_project.R
import java.text.SimpleDateFormat
import java.util.*

class CollectionDescriptionFragment : Fragment() {
    private val args by navArgs<CollectionDescriptionFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collection_description, container, false)
        val item = args.collectedItem
        Log.d("current item", "$item")
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val collectedTimeString = simpleDateFormat.format(item.collectedTime)

        view.findViewById<FloatingActionButton>(R.id.back_btn).setOnClickListener {
            findNavController().popBackStack()
        }
        Glide.with(this).load(item.thumbnail)
            .placeholder(R.drawable.ic_reward_item_placeholder)
            .error(R.drawable.ic_reward_item_placeholder)
            .into(view.findViewById(R.id.thumbnail))

        view.findViewById<TextView>(R.id.name).text = item.name
        view.findViewById<TextView>(R.id.description).text = item.description
        view.findViewById<TextView>(R.id.saved_time).text =
            getString(R.string.collected_time, collectedTimeString)

        val playBtn = view.findViewById<Button>(R.id.play_btn)
        playBtn.text = getString(R.string.play_with, item.name)
        playBtn.setOnClickListener {
            val action =
                CollectionDescriptionFragmentDirections.actionCollectionDescriptionFragmentToCollectedItemARFragment(
                    item.objectUrl
                )
            it.findNavController().navigate(action)
        }
        return view
    }
}