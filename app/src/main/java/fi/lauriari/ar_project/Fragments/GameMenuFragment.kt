package fi.lauriari.ar_project.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.lauriari.ar_project.R

class GameMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_menu, container, false)

        view.findViewById<Button>(R.id.item_list).setOnClickListener {
            findNavController().navigate(R.id.action_gameMenuFragment_to_itemListFragment)
        }
        view.findViewById<Button>(R.id.reward_list).setOnClickListener {
            findNavController().navigate(R.id.action_gameMenuFragment_to_rewardListFragment)
        }
        view.findViewById<Button>(R.id.daily_quest).setOnClickListener {
            findNavController().navigate(R.id.action_gameMenuFragment_to_dailyQuestFragment)
        }
//        view.findViewById<Button>(R.id.back_to_main_menu).setOnClickListener {
//            findNavController().popBackStack()
//        }
        view.findViewById<FloatingActionButton>(R.id.back_btn).setOnClickListener {
            findNavController().popBackStack()
        }
        return view
    }

}