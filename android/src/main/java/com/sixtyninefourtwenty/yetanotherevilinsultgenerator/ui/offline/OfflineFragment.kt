package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.offline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.SelectionTracker.SelectionObserver
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kennyc.view.MultiStateView
import com.sixtyninefourtwenty.basefragments.BaseFragment
import com.sixtyninefourtwenty.customactionmode.AbstractActionMode
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.R
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.databinding.FragmentOfflineBinding
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.adapter.InsultAdapter
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.actionMode
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.addMenuProvider

class OfflineFragment : BaseFragment<FragmentOfflineBinding>() {

    private val offlineViewModel: OfflineViewModel by activityViewModels { OfflineViewModel.Factory }
    private val insultsAdapter = InsultAdapter {
        findNavController().navigate(OfflineFragmentDirections.actionNavigationOfflineToNavigationInsultInfo(it.toJson()))
    }
    private lateinit var selectionTracker: SelectionTracker<Long>

    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.offline_fragment_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.filter_insults -> {
                    findNavController().navigate(OfflineFragmentDirections.actionNavigationOfflineToNavigationFilterInsults())
                    true
                }
                R.id.sort_insults -> {
                    findNavController().navigate(OfflineFragmentDirections.actionNavigationOfflineToNavigationSortInsults())
                    true
                }
                else -> false
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): FragmentOfflineBinding {
        return FragmentOfflineBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(binding: FragmentOfflineBinding, savedInstanceState: Bundle?) {
        addMenuProvider(menuProvider)
        with(binding.offlineInsults) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = insultsAdapter
        }
        selectionTracker = SelectionTracker.Builder(
            "selected_insults",
            binding.offlineInsults,
            insultsAdapter.KeyProvider(),
            InsultAdapter.DetailsLookup(binding.offlineInsults),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build().also { tracker ->
            tracker.addObserver(object : SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    val selection = tracker.selection
                    if (selection.isEmpty) {
                        actionMode.finish()
                    } else {
                        with(actionMode) {
                            start(object : AbstractActionMode.Callback {
                                override fun onMenuItemClicked(mode: AbstractActionMode, item: MenuItem): Boolean {
                                    when (item.itemId) {
                                        R.id.delete -> {
                                            val insults = insultsAdapter.currentList
                                            val insultsToDelete = selection.filter { number ->
                                                insultsAdapter.currentList.single {
                                                    it.number.toLong() == number
                                                }.isFavorite.not()
                                            }.map { number -> insults.first { it.number.toLong() == number } }
                                            if (insultsToDelete.isEmpty()) {
                                                Toast.makeText(requireContext(), R.string.all_insults_selected_are_favorites, Toast.LENGTH_SHORT).show()
                                            } else {
                                                MaterialAlertDialogBuilder(requireContext())
                                                    .setTitle(R.string.delete)
                                                    .setMessage(getString(R.string.delete_confirmation, resources.getQuantityString(R.plurals.insults, insultsToDelete.size, insultsToDelete.size)))
                                                    .setPositiveButton(android.R.string.ok) { _, _ ->
                                                        offlineViewModel.deleteLocalInsults(insultsToDelete) {
                                                            Toast.makeText(requireContext(), getString(R.string.deleted, resources.getQuantityString(R.plurals.insults, selection.size(), selection.size())), Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    .show()
                                            }

                                            return true
                                        }
                                    }
                                    return false
                                }

                                override fun onActionModeFinished(mode: AbstractActionMode) {
                                    tracker.clearSelection()
                                }
                            }, R.menu.insults_selection_action_mode_menu)
                            title = resources.getQuantityString(R.plurals.insults, selection.size(), selection.size())
                        }
                    }
                }
            })
            tracker.onRestoreInstanceState(savedInstanceState)
            insultsAdapter.selectionTracker = tracker
        }

        offlineViewModel.insultsAfterSearchAndFilter.observe(viewLifecycleOwner) {
            binding.stateView.viewState = if (it.isEmpty()) MultiStateView.ViewState.EMPTY else MultiStateView.ViewState.CONTENT
            insultsAdapter.submitList(it)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::selectionTracker.isInitialized) {
            selectionTracker.onSaveInstanceState(outState)
        }
    }

}