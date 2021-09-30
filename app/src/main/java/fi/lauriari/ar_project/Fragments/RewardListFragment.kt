package fi.lauriari.ar_project.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.lauriari.ar_project.Inventory
import fi.lauriari.ar_project.InventoryViewModel
import fi.lauriari.ar_project.R
import fi.lauriari.ar_project.databinding.FragmentRewardListBinding
import java.util.Observer

class RewardListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inventoryViewModel: InventoryViewModel by viewModels()
        //val view = inflater.inflate(R.layout.fragment_reward_list, container, false)
        val binding: FragmentRewardListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_reward_list, container, false)
        binding.viewmodel = inventoryViewModel
//       binding.lifecycleOwner = viewLifecycleOwner
        val view = binding.root

        val inventory = inventoryViewModel.getInventory()
        inventoryViewModel.getInventory().observe(viewLifecycleOwner, {
            binding.emeraldAmount.text = it.emeralds.toString()
            binding.rubyAmount.text = it.rubies.toString()
            binding.sapphireAmount.text = it.sapphires.toString()
            binding.topazAmount.text = it.topazes.toString()
            binding.diamondAmount.text = it.diamonds.toString()
        })

        view.findViewById<FloatingActionButton>(R.id.back_btn).setOnClickListener {
            findNavController().popBackStack()
        }

        val itemList = view.findViewById<RecyclerView>(R.id.rewards_list)
        itemList.layoutManager = GridLayoutManager(activity, 2)
        itemList.adapter = RewardListAdapter()
        return view
    }

}