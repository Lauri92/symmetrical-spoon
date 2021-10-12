package fi.lauriari.ar_project.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fi.lauriari.ar_project.*
import fi.lauriari.ar_project.Network.RewardsApiViewModel
import fi.lauriari.ar_project.Network.RewardsApiViewModelFactory
import fi.lauriari.ar_project.Network.RewardsRepository
import fi.lauriari.ar_project.activities.stepCounter
import fi.lauriari.ar_project.viewmodels.CollectedItemViewModel
import fi.lauriari.ar_project.viewmodels.InventoryViewModel
import fi.lauriari.ar_project.databinding.FragmentRewardListBinding

class RewardListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val inventoryViewModel: InventoryViewModel by viewModels()
        val collectedItemViewModel: CollectedItemViewModel by viewModels()
        val binding: FragmentRewardListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_reward_list, container, false)
        binding.viewmodel = inventoryViewModel
        val view = binding.root

        val collectedItems = collectedItemViewModel.getCollectedItems().map { it.name }

        Log.d("test", "${stepCounter.getTotalSteps()}")
        binding.textView2.text = stepCounter.getTotalSteps().toString()
        // display current amount of gems that the user has collected
        inventoryViewModel.getInventory().observe(viewLifecycleOwner, {
            binding.emeraldAmount.text = it.emeralds.toString()
            binding.rubyAmount.text = it.rubies.toString()
            binding.sapphireAmount.text = it.sapphires.toString()
            binding.topazAmount.text = it.topazes.toString()
            binding.diamondAmount.text = it.diamonds.toString()
        })

        view.findViewById<FloatingActionButton>(R.id.back_btn).setOnClickListener {
            findNavController().popBackStack()
        }

        val repo = RewardsRepository()
        val viewModelFactory = RewardsApiViewModelFactory(repo)

        val rewardsApiViewModel =
            ViewModelProvider(this, viewModelFactory).get(RewardsApiViewModel::class.java)
        rewardsApiViewModel.getItems()

        val itemList = binding.rewardsList
        itemList.layoutManager = GridLayoutManager(activity, 2)

        rewardsApiViewModel.response.observe(viewLifecycleOwner, {
            itemList.adapter = RewardListAdapter(it, collectedItems)
        })
        return view
    }

}