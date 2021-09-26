package fi.lauriari.ar_project.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import fi.lauriari.ar_project.R

class MainMenuFragment : Fragment() {

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

        return view
    }


}