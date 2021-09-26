package fi.lauriari.ar_project.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.lauriari.ar_project.R

class RewardListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reward_list, container, false)
        val itemList = view.findViewById<RecyclerView>(R.id.rewards_list)
        itemList.layoutManager = GridLayoutManager(activity,2)
        itemList.adapter = RewardListAdapter()
        return view
    }

}