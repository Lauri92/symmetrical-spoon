package fi.lauriari.ar_project.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.lauriari.ar_project.CollectedItemViewModel
import fi.lauriari.ar_project.R

class CollectedItemListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val collectItemViewModel: CollectedItemViewModel by viewModels()
        val view = inflater.inflate(R.layout.fragment_collected_item_list, container, false)
        view.findViewById<FloatingActionButton>(R.id.back_btn).setOnClickListener {
            findNavController().popBackStack()
        }
        val itemList = collectItemViewModel.getCollectedItems()
        Log.d("collection","$itemList")
        return view
    }

}