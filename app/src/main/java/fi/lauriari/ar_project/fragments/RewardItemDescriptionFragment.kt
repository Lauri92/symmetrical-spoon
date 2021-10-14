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

// Fragment for detailed information and purchase of each item from the reward shop
class RewardItemDescriptionFragment : Fragment() {
    // get the information of selected item from the recyclerview through navigation argument
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
        // selected item from the recyclerview
        val currentItem = args.rewardItem

        // set the item's price
        val emeraldCost = currentItem.itemEmerald
        val rubyCost = currentItem.itemRuby
        val sapphireCost = currentItem.itemSapphire
        val topazCost = currentItem.itemTopaz
        val diamondCost = currentItem.itemDiamond

        // set image using Glide
        Glide.with(activity!!).load(currentItem.thumbnail)
            .placeholder(R.drawable.ic_reward_item_placeholder)
            .error(R.drawable.ic_reward_item_placeholder)
            .into(view.findViewById(R.id.thumbnail))

        // prepare an array of the price to display each value on the view
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

            // display the amount of gems that the user has
            binding.emeraldAmount.text = userEmeralds.toString()
            binding.rubyAmount.text = userRubies.toString()
            binding.sapphireAmount.text = userSapphires.toString()
            binding.topazAmount.text = userTopazes.toString()
            binding.diamondAmount.text = userDiamonds.toString()

            binding.buyBtn.setOnClickListener {
                if (!checkEnoughGems(userGems, itemGemsPrice)) {
                    makeGemNotification(currentItem.itemName)
                } else {
                    // show a confirmation dialog before verifying the purchase
                    val purchaseDialog = setDialog(R.layout.purchase_confirmation)
                    purchaseDialog.findViewById<TextView>(R.id.confirmation_msg).text =
                        getString(R.string.purchase_message, currentItem.itemName)
                    purchaseDialog.findViewById<Button>(R.id.cancel_btn)
                        .setOnClickListener { purchaseDialog.dismiss() }
                    // when the purchase is verified
                    purchaseDialog.findViewById<Button>(R.id.purchase_btn).setOnClickListener {
                        // purchased date
                        val dateNow = Calendar.getInstance().timeInMillis

                        // insert a row for the purchased item on the collectedItem table
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
                        // subtract the number of price from the user's gems and then update values on the database
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

        //display the price of the item
        itemGemsPrice.forEach { gem -> gem.initPriceText(view) }
        binding.name.text = currentItem.itemName
        binding.description.text = currentItem.description
        binding.backBtn.setOnClickListener { findNavController().popBackStack() }

        return view
    }

    // compare gems that the user has to the price of the item to check if the user has enough gems to buy the item
    private fun checkEnoughGems(userGems: Array<Int>, priceGems: Array<Gems>): Boolean {
        for (i in userGems.indices) {
            if (userGems[i] < priceGems[i].gemValue!!) {
                return false
            }
        }
        return true
    }

    // If the user doesn't have enough gems to buy the item, show a warning dialog
    private fun makeGemNotification(itemName: String) {
        val priceWarningDialog = setDialog(R.layout.price_warning_dialog)
        priceWarningDialog.findViewById<Button>(R.id.cancel_btn)
            .setOnClickListener { priceWarningDialog.dismiss() }
        priceWarningDialog.findViewById<TextView>(R.id.warning_msg).text =
            getString(R.string.warning_message, itemName)
        priceWarningDialog.show()
    }

    // After the user buy the item, show a snackbar message which makes the user be able to move to the collection fragment
    private fun makeConfirmationSnackBar(itemName: String, activity: Activity) {
        val snackbar = Snackbar.make(view!!, "", 3000)
        val nullParent: ViewGroup? = null
        val navSnackView = layoutInflater.inflate(R.layout.navigation_snackbar, nullParent, false)
        snackbar.setBackgroundTint(Color.TRANSPARENT)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        with(snackbarLayout) {
            setPadding(0, 0, 0, 0)
            elevation = 0f
            addView(navSnackView.rootView, 0)
        }
        navSnackView.findViewById<TextView>(R.id.notification).text =
            getString(R.string.purchase_confirmation_msg, itemName)
        navSnackView.findViewById<Button>(R.id.go_to_collection_btn).setOnClickListener {
            Navigation.findNavController(activity, R.id.nav_host_fragment)
                .navigate(R.id.collectedItemList)
        }
        snackbar.show()
    }

    // generate a dialog with a custom layout
    private fun setDialog(layoutId: Int): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layoutId)
        return dialog
    }
}