package fi.lauriari.ar_project.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.lauriari.ar_project.*

class DailyQuestFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_daily_quest, container, false)

        view.findViewById<FloatingActionButton>(R.id.back_btn).setOnClickListener {
            findNavController().popBackStack()
        }

//        val repo = RewardsRepository()
//        val viewModelFactory = RewardsApiViewModelFactory(repo)
//
//        val rewardsApiViewModel =
//            ViewModelProvider(this, viewModelFactory).get(RewardsApiViewModel::class.java)
//        rewardsApiViewModel.getItems()
//
//        rewardsApiViewModel.response.observe(viewLifecycleOwner,{
//            Log.d("DQF","${it.get(0).description}")
//        })

        return view
    }
}