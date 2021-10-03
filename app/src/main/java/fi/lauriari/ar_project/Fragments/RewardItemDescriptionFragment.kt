package fi.lauriari.ar_project.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import fi.lauriari.ar_project.CollectedItemViewModel
import fi.lauriari.ar_project.Entities.CollectedItem
import fi.lauriari.ar_project.Entities.Inventory
import fi.lauriari.ar_project.InventoryViewModel
import fi.lauriari.ar_project.R
import fi.lauriari.ar_project.databinding.FragmentRewardItemDescriptionBinding
import java.text.SimpleDateFormat
import java.util.*

class RewardItemDescriptionFragment : Fragment() {
    private val args by navArgs<RewardItemDescriptionFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inventoryViewModel: InventoryViewModel by viewModels()
        val collectedItemViewModel: CollectedItemViewModel by viewModels()

        val binding: FragmentRewardItemDescriptionBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reward_item_description,
            container,
            false
        )
        binding.viewmodel = inventoryViewModel
        val view = binding.root
        val currentItem = args.rewardItem

        val emeraldCost = currentItem.itemEmerald
        val rubyCost = currentItem.itemRuby
        val sapphireCost = currentItem.itemSapphire
        val topazCost = currentItem.itemTopaz
        val diamondCost = currentItem.itemDiamond

        Glide.with(activity!!).load(currentItem.thumbnail)
            .placeholder(R.drawable.ic_reward_item_placeholder)
            .error(R.drawable.ic_reward_item_placeholder)
            .into(view.findViewById(R.id.thumbnail))

        inventoryViewModel.getInventory().observe(viewLifecycleOwner, { inventory ->

            val currentEmeralds = inventory.emeralds
            val currentRubies = inventory.rubies
            val currentSapphires = inventory.sapphires
            val currentTopazes = inventory.topazes
            val currentDiamonds = inventory.diamonds

            binding.emeraldAmount.text = currentEmeralds.toString()
            binding.rubyAmount.text = currentRubies.toString()
            binding.sapphireAmount.text = currentSapphires.toString()
            binding.topazAmount.text = currentTopazes.toString()
            binding.diamondAmount.text = currentDiamonds.toString()

            binding.buyBtn.setOnClickListener {

                val alertBuilder = AlertDialog.Builder(activity)
                alertBuilder.setPositiveButton("Buy") { _, _ ->
                    Toast.makeText(
                        activity,
                        "you got ${currentItem.itemName}!!!",
                        Toast.LENGTH_LONG
                    ).show()

                    val dateNow = Calendar.getInstance().timeInMillis

                    collectedItemViewModel.insertCollectedItem(
                        CollectedItem(
                            0,
                            currentItem.itemName,
                            currentItem.thumbnail,
                            currentItem.description,
                            dateNow
                        )
                    )
//                    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
//                    val dateNowString = simpleDateFormat.format(dateNow)

                    inventoryViewModel.updateInventory(
                        Inventory(
                            inventory.id,
                            currentEmeralds - emeraldCost,
                            currentRubies - rubyCost,
                            currentSapphires - sapphireCost,
                            currentTopazes - topazCost,
                            currentDiamonds - diamondCost
                        )
                    )
                    findNavController().popBackStack()
                }
                alertBuilder.setNegativeButton("Cancel") { _, _ -> }
                alertBuilder.setTitle("Purchase confirmation")
                alertBuilder.setMessage("Are you sure that you would buy ${currentItem.itemName}?")
                alertBuilder.create().show()
            }
        })

        val gems = arrayOf(
            Gems(emeraldCost, R.id.emerald_price_counter, R.id.emerald_price_amount),
            Gems(rubyCost, R.id.ruby_price_counter, R.id.ruby_price_amount),
            Gems(sapphireCost, R.id.sapphire_price_counter, R.id.sapphire_price_amount),
            Gems(topazCost, R.id.topaz_price_counter, R.id.topaz_price_amount),
            Gems(diamondCost, R.id.diamond_price_counter, R.id.diamond_price_amount)
        )
        gems.forEach { gem -> gem.initPriceText(view) }
        binding.name.text = currentItem.itemName
        binding.description.text = currentItem.description
        binding.backBtn.setOnClickListener { findNavController().popBackStack() }

        return view
    }

    private fun makeAlert(itemName: String) {
        val alertBuilder = AlertDialog.Builder(activity)
        alertBuilder.setPositiveButton("Buy") { _, _ ->
            Toast.makeText(activity, "you got ${itemName}!!!", Toast.LENGTH_LONG).show()
        }
        alertBuilder.setNegativeButton("Cancel") { _, _ -> }
        alertBuilder.setTitle("Purchase confirmation")
        alertBuilder.setMessage("Are you sure that you would buy ${itemName}?")
        alertBuilder.create().show()
    }
}