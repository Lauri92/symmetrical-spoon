package fi.lauriari.ar_project.Fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import fi.lauriari.ar_project.Entities.Inventory
import fi.lauriari.ar_project.viewmodels.InventoryViewModel
import fi.lauriari.ar_project.R
import java.net.URL

class MainMenuFragment : Fragment() {

    private val mInventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val IMG_URL = URL("https://users.metropolia.fi/~minjic/AR_project/emerald.png")

        val view = inflater.inflate(R.layout.fragment_main_menu, container, false)

        view.findViewById<Button>(R.id.navigate_to_map_btn).setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_gameMapFragment)
        }
        view.findViewById<Button>(R.id.reward_list).setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_rewardListFragment)
        }
        view.findViewById<Button>(R.id.graphs_btn).setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_graphsFragment)
        }
        view.findViewById<Button>(R.id.my_collections).setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_collectedItemList)
        }

        fun getImg(imgUrl: URL): Bitmap {
            val inputStream = imgUrl.openStream()
            Log.d("image", "${inputStream}")
            return BitmapFactory.decodeStream(inputStream)
        }

        fun showImg(serverImg: Bitmap) {
            view.findViewById<ImageView>(R.id.logo).setImageBitmap(serverImg)
        }

//        lifecycleScope.launch(context = Dispatchers.Main) {
//            val img = async(Dispatchers.IO) { getImg(IMG_URL) }
//            //showImg(img.await())
//        }

        // var inventory: LiveData<Inventory>? = mInventoryViewModel.getInventory()

        var list: List<Inventory>? = mInventoryViewModel.getInventoryList()

        if (list?.size == 0) {
            Log.d("inventory", "No inventory, creating one")
            mInventoryViewModel.insertInventory(Inventory(0, 2, 3, 4, 5, 0))

        } else{
            Log.d("inventory", "There was an inventory, $list")
        }

        return view
    }


}