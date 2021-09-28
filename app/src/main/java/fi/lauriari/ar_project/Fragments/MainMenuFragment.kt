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
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import fi.lauriari.ar_project.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.URL

class MainMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val IMG_URL = URL("https://users.metropolia.fi/~minjic/AR_project/emerald.png")

        val view = inflater.inflate(R.layout.fragment_main_menu, container, false)

        view.findViewById<Button>(R.id.navigate_to_map_btn).setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_gameMapFragment)
        }
        view.findViewById<Button>(R.id.reward_list).setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_rewardListFragment)
        }
        view.findViewById<Button>(R.id.daily_quest).setOnClickListener {
            findNavController().navigate(R.id.action_mainMenuFragment_to_dailyQuestFragment)
        }

        fun getImg(imgUrl: URL): Bitmap {
            val inputStream = imgUrl.openStream()
            Log.d("image", "${inputStream}")
            return BitmapFactory.decodeStream(inputStream)
        }

        fun showImg(serverImg: Bitmap) {
            view.findViewById<ImageView>(R.id.logo).setImageBitmap(serverImg)
        }

        lifecycleScope.launch(context = Dispatchers.Main) {
            val img = async(Dispatchers.IO) { getImg(IMG_URL) }
            //showImg(img.await())
        }

        return view
    }


}