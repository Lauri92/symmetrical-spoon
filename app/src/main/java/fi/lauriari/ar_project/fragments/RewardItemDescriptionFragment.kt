package fi.lauriari.ar_project.fragments


import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import fi.lauriari.ar_project.viewmodels.CollectedItemViewModel
import fi.lauriari.ar_project.entities.CollectedItem
import fi.lauriari.ar_project.entities.Inventory
import fi.lauriari.ar_project.Gems
import fi.lauriari.ar_project.viewmodels.InventoryViewModel
import fi.lauriari.ar_project.R
import fi.lauriari.ar_project.databinding.FragmentRewardItemDescriptionBinding
import java.util.*

class RewardItemDescriptionFragment : Fragment() {
    private val args by navArgs<RewardItemDescriptionFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val inventoryViewModel: InventoryViewModel by viewModels()
        val collectedItemViewModel: CollectedItemViewModel by viewModels()
        val binding: FragmentRewardItemDescriptionBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_reward_item_description, container, false
        )
        binding.viewmodel = inventoryViewModel
        val view = binding.root
        val currentItem = args.rewardItem

        val emeraldCost = currentItem.itemEmerald
        val rubyCost = currentItem.itemRuby
        val sapphireCost = currentItem.itemSapphire
        val topazCost = currentItem.itemTopaz
        val diamondCost = currentItem.itemDiamond

        // set image
        Glide.with(activity!!).load(currentItem.thumbnail)
            .placeholder(R.drawable.ic_reward_item_placeholder)
            .error(R.drawable.ic_reward_item_placeholder)
            .into(view.findViewById(R.id.thumbnail))

        val itemGemsPrice = arrayOf(
            Gems(emeraldCost, R.id.emerald_price_counter, R.id.emerald_price_amount),
            Gems(rubyCost, R.id.ruby_price_counter, R.id.ruby_price_amount),
            Gems(sapphireCost, R.id.sapphire_price_counter, R.id.sapphire_price_amount),
            Gems(topazCost, R.id.topaz_price_counter, R.id.topaz_price_amount),
            Gems(diamondCost, R.id.diamond_price_counter, R.id.diamond_price_amount)
        )

        inventoryViewModel.getInventory().observe(viewLifecycleOwner, { inventory ->

            val userEmeralds = inventory.emeralds
            val userRubies = inventory.rubies
            val userSapphires = inventory.sapphires
            val userTopazes = inventory.topazes
            val userDiamonds = inventory.diamonds
            val userGems =
                arrayOf(userEmeralds, userRubies, userSapphires, userTopazes, userDiamonds)

            binding.emeraldAmount.text = userEmeralds.toString()
            binding.rubyAmount.text = userRubies.toString()
            binding.sapphireAmount.text = userSapphires.toString()
            binding.topazAmount.text = userTopazes.toString()
            binding.diamondAmount.text = userDiamonds.toString()

            binding.buyBtn.setOnClickListener {
                if (!checkEnoughGems(userGems, itemGemsPrice)) {
                    makeGemNotification(currentItem.itemName)
                } else {
                    val purchaseDialog = setDialog(R.layout.purchase_confirmation)
                    purchaseDialog.findViewById<TextView>(R.id.confirmation_msg).text =
                        getString(R.string.purchase_message, currentItem.itemName)
                    purchaseDialog.findViewById<Button>(R.id.cancel_btn)
                        .setOnClickListener { purchaseDialog.dismiss() }
                    purchaseDialog.findViewById<Button>(R.id.purchase_btn).setOnClickListener {
                        val dateNow = Calendar.getInstance().timeInMillis

                        collectedItemViewModel.insertCollectedItem(
                            CollectedItem(
                                0,
                                currentItem.itemName,
                                currentItem.thumbnail,
                                currentItem.objectUrl,
                                currentItem.description,
                                dateNow
                            )
                        )
                        inventoryViewModel.updateInventory(
                            Inventory(
                                inventory.id,
                                userEmeralds - emeraldCost,
                                userRubies - rubyCost,
                                userSapphires - sapphireCost,
                                userTopazes - topazCost,
                                userDiamonds - diamondCost
                            )
                        )
                        purchaseDialog.dismiss()
                        findNavController().popBackStack()
                        makeConfirmationSnackBar(currentItem.itemName, activity!!)
                    }
                    purchaseDialog.show()
                }
            }
        })

        itemGemsPrice.forEach { gem -> gem.initPriceText(view) }
        binding.name.text = currentItem.itemName
        binding.description.text = currentItem.description
        binding.backBtn.setOnClickListener { findNavController().popBackStack() }

        return view
    }

    private fun checkEnoughGems(userGems: Array<Int>, priceGems: Array<Gems>): Boolean {
        for (i in userGems.indices) {
            if (userGems[i] < priceGems[i].gemValue!!) {
                return false
            }
        }
        return true
    }

    private fun makeGemNotification(itemName: String) {
        val priceWarningDialog = setDialog(R.layout.price_warning_dialog)
        priceWarningDialog.findViewById<Button>(R.id.cancel_btn)
            .setOnClickListener { priceWarningDialog.dismiss() }
        priceWarningDialog.findViewById<TextView>(R.id.warning_msg).text =
            getString(R.string.warning_message, itemName)
        priceWarningDialog.show()
    }

    private fun makeConfirmationSnackBar(itemName: String, activity: Activity) {
        val snackbar = Snackbar.make(view!!, "", 3000)
        val navSnackView = layoutInflater.inflate(R.layout.navigation_snackbar, null, false)
        snackbar.setBackgroundTint(Color.TRANSPARENT)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        with(snackbarLayout) {
            setPadding(0, 0, 0, 0)
            elevation = 0f
            addView(navSnackView.rootView, 0)
        }
        navSnackView.findViewById<TextView>(R.id.notification).text = "You got $itemName"
        navSnackView.findViewById<Button>(R.id.go_to_collection_btn).setOnClickListener {
            Navigation.findNavController(activity, R.id.nav_host_fragment)
                .navigate(R.id.collectedItemList)
        }
        snackbar.show()
    }

    private fun setDialog(layoutId: Int): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layoutId)
        return dialog
    }
}