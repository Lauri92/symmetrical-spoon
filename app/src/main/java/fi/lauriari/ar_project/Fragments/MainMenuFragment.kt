package fi.lauriari.ar_project.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import fi.lauriari.ar_project.Inventory
import fi.lauriari.ar_project.InventoryViewModel
import fi.lauriari.ar_project.R

class MainMenuFragment : Fragment() {

    private val mInventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_menu, container, false)

        view.findViewById<Button>(R.id.navigate_to_map_btn).setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_gameMapFragment)
        }

        view.findViewById<Button>(R.id.navigate_to_game_menu).setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_gameMenuFragment)
        }

        var inventory: Inventory? = mInventoryViewModel.getInventory()

        if (inventory == null) {
            Log.d("inventory", "No inventory, creating one")
            mInventoryViewModel.insertInventory(Inventory(0, 0, 0, 0, 0, 0))
            inventory = mInventoryViewModel.getInventory()
        } else {
            Log.d("inventory", "There was an inventory")
        }


        Log.d("inventory", "${inventory.diamonds}")


        return view
    }


}