package fi.lauriari.ar_project.Fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import fi.lauriari.ar_project.InventoryViewModel
import fi.lauriari.ar_project.R
import fi.lauriari.ar_project.databinding.FragmentRewardItemDescriptionBinding

class RewardItemDescriptionFragment : Fragment() {
    private val args by navArgs<RewardItemDescriptionFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inventoryViewModel: InventoryViewModel by viewModels()
        val binding: FragmentRewardItemDescriptionBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reward_item_description,
            container,
            false
        )
        binding.viewmodel = inventoryViewModel
        val view = binding.root
        val currentItem = args.rewardItemDummy

        inventoryViewModel.getInventory().observe(viewLifecycleOwner, { inventory ->
            binding.emeraldAmount.text = inventory.emeralds.toString()
            binding.rubyAmount.text = inventory.rubies.toString()
            binding.sapphireAmount.text = inventory.sapphires.toString()
            binding.topazAmount.text = inventory.topazes.toString()
            binding.diamondAmount.text = inventory.diamonds.toString()

            binding.buyBtn.setOnClickListener {
                val alertBuilder = AlertDialog.Builder(activity)
                alertBuilder.setPositiveButton("Buy"){_,_->
                    Toast.makeText(activity, "you got ${currentItem.name}!!!", Toast.LENGTH_LONG).show()
                    inventoryViewModel.updateEmeralds(inventory.emeralds - 1)}
                alertBuilder.setNegativeButton("Cancel"){_,_->}
                alertBuilder.setTitle("Purchase confirmation")
                alertBuilder.setMessage("Are you sure that you would buy ${currentItem.name} (-1 Emerald) ?")
                alertBuilder.create().show()
               // Toast.makeText(activity, "you got ${currentItem.name}!!!", Toast.LENGTH_LONG).show()
//                inventoryViewModel.updateInventory(
//                    Inventory(
//                        inventory.id,
//                        inventory.emeralds + 1,
//                        inventory.rubies + 1,
//                        inventory.sapphires + 1,
//                        inventory.topazes + 1,
//                        inventory.diamonds + 1
//                    )
//                )
                //inventoryViewModel.updateEmeralds(inventory.emeralds +1)
            }
        })
        binding.name.text = currentItem.name
        binding.description.text = currentItem.descriptions
        binding.backBtn.setOnClickListener { findNavController().popBackStack() }

        return view
    }

}