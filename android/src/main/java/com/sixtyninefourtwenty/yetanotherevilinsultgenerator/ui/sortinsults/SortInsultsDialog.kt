package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.sortinsults

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.sixtyninefourtwenty.bottomsheetalertdialog.BottomSheetAlertDialogFragmentViewBuilder
import com.sixtyninefourtwenty.bottomsheetalertdialog.DialogButtonProperties
import com.sixtyninefourtwenty.bottomsheetalertdialog.misc.BaseBottomSheetAlertDialogFragment
import com.sixtyninefourtwenty.bottomsheetalertdialog.misc.createBottomSheetAlertDialog
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.R
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.databinding.DialogSortInsultsBinding
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.InsultSortOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.offline.OfflineViewModel

class SortInsultsDialog : BaseBottomSheetAlertDialogFragment<DialogSortInsultsBinding>() {

    private val offlineViewModel: OfflineViewModel by activityViewModels { OfflineViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogSortInsultsBinding {
        return DialogSortInsultsBinding.inflate(inflater, container, false)
    }

    override fun initDialog(binding: DialogSortInsultsBinding): BottomSheetAlertDialogFragmentViewBuilder {
        return createBottomSheetAlertDialog(
            view = binding.root,
            titleText = getString(R.string.sort_insults),
            positiveButtonProperties = DialogButtonProperties(
                textRes = android.R.string.ok,
                listener = {
                    val isFavoriteFirst = binding.favoritesFirst.isChecked
                    offlineViewModel.insultSortOptions = when (binding.toggleGroup.checkedChipId) {
                        binding.alphabeticalAscending.id -> InsultSortOptions(isFavoriteFirst, InsultSortOptions.Mode.CONTENT_ALPHABETICAL_ASCENDING)
                        binding.alphabeticalDescending.id -> InsultSortOptions(isFavoriteFirst, InsultSortOptions.Mode.CONTENT_ALPHABETICAL_DESCENDING)
                        binding.none.id -> InsultSortOptions(isFavoriteFirst, null)
                        else -> throw AssertionError("Unexpected checked chip id: ${binding.toggleGroup.checkedChipId}")
                    }
                }
            ),
            neutralButtonProperties = DialogButtonProperties(
                textRes = R.string.reset,
                listener = { offlineViewModel.insultSortOptions = null }
            ),
            negativeButtonProperties = DialogButtonProperties(textRes = android.R.string.cancel)
        )
    }

    override fun onViewCreated(binding: DialogSortInsultsBinding, savedInstanceState: Bundle?) {
        binding.favoritesFirst.isChecked = offlineViewModel.insultSortOptions?.favoritesFirst ?: false
        binding.toggleGroup.check(when (offlineViewModel.insultSortOptions?.mode) {
            null -> binding.none.id
            InsultSortOptions.Mode.CONTENT_ALPHABETICAL_ASCENDING -> binding.alphabeticalAscending.id
            InsultSortOptions.Mode.CONTENT_ALPHABETICAL_DESCENDING -> binding.alphabeticalDescending.id
        })
    }
}