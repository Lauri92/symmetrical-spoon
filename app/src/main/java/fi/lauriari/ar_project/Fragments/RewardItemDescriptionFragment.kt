package fi.lauriari.ar_project.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.lauriari.ar_project.R

class RewardItemDescriptionFragment : Fragment() {
    private val args by navArgs<RewardItemDescriptionFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reward_item_description, container, false)
        val currentItem = args.rewardItem

        view.findViewById<TextView>(R.id.name).text = currentItem.name
        view.findViewById<TextView>(R.id.description).text = currentItem.descriptions
view.findViewById<FloatingActionButton>(R.id.back_btn).setOnClickListener {  findNavController().popBackStack() }
        view.findViewById<Button>(R.id.buy_btn).setOnClickListener {
            Toast.makeText(activity,"you got ${currentItem.name}!!!",Toast.LENGTH_LONG).show()
        }
        return view
    }

}