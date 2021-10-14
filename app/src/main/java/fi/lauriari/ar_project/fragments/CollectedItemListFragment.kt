package fi.lauriari.ar_project.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import fi.lauriari.ar_project.viewmodels.CollectedItemViewModel
import fi.lauriari.ar_project.R
import fi.lauriari.ar_project.databinding.FragmentCollectedItemListBinding

// fragment for the list of purchased items
class CollectedItemListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val collectItemViewModel: CollectedItemViewModel by viewModels()
        val itemList = collectItemViewModel.getCollectedItems()

        val binding: FragmentCollectedItemListBinding =
            DataBindingUtil.inflate(
                inflater, R.layout.fragment_collected_item_list,
                container, false
            )

        binding.viewmodel = collectItemViewModel
        val view = binding.root
        binding.backBtn.setOnClickListener { findNavController().popBackStack() }

        val collectedItemList = binding.collectedItemList
        collectedItemList.layoutManager = GridLayoutManager(activity, 2)
        collectedItemList.adapter = CollectedItemListAdapter(itemList)

        return view
    }
}