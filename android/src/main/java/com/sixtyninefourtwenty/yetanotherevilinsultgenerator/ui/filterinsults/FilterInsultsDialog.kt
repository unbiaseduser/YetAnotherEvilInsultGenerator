package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.filterinsults

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.sixtyninefourtwenty.bottomsheetalertdialog.BottomSheetAlertDialogFragmentViewBuilder
import com.sixtyninefourtwenty.bottomsheetalertdialog.DialogButtonProperties
import com.sixtyninefourtwenty.bottomsheetalertdialog.misc.BaseBottomSheetAlertDialogFragment
import com.sixtyninefourtwenty.bottomsheetalertdialog.misc.createBottomSheetAlertDialog
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.R
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.databinding.DialogFilterInsultsBinding
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Language
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.InsultFilterOptions
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.sortandfilter.TextFilter
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.offline.OfflineViewModel
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.getInput
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.isInputEmpty
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.languageLabelResId

class FilterInsultsDialog : BaseBottomSheetAlertDialogFragment<DialogFilterInsultsBinding>() {

    private val offlineViewModel: OfflineViewModel by activityViewModels { OfflineViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogFilterInsultsBinding {
        return DialogFilterInsultsBinding.inflate(inflater, container, false)
    }

    override fun initDialog(binding: DialogFilterInsultsBinding): BottomSheetAlertDialogFragmentViewBuilder {
        return createBottomSheetAlertDialog(
            view = binding.root,
            titleText = getString(R.string.filter_insults),
            positiveButtonProperties = DialogButtonProperties(
                textRes = android.R.string.ok,
                listener = {
                    offlineViewModel.insultFilterOptions = InsultFilterOptions(
                        language = binding.languagePicker.itemSelectedPosition.takeIf { it != 0 }?.let { Language.values()[it - 1] },
                        textFilter = if (binding.searchInsultContentsInput.isInputEmpty()) null else TextFilter(
                            binding.searchInsultContentsInput.getInput(), TextFilter.Mode.CONTAINS
                        )
                    )
                }
            ),
            neutralButtonProperties = DialogButtonProperties(
                textRes = R.string.reset,
                listener = { offlineViewModel.insultFilterOptions = null }
            ),
            negativeButtonProperties = DialogButtonProperties(textRes = android.R.string.cancel)
        )
    }

    override fun onViewCreated(binding: DialogFilterInsultsBinding, savedInstanceState: Bundle?) {
        binding.languagePicker.setItemList((intArrayOf(R.string.none) + Language.values().map { it.languageLabelResId }).map(::getString))
        val existingFilterOptions = offlineViewModel.insultFilterOptions
        if (existingFilterOptions != null) {
            binding.languagePicker.itemSelectedPosition = existingFilterOptions.language?.ordinal?.plus(1) ?: 0
            binding.searchInsultContentsInput.setText(existingFilterOptions.textFilter?.textToSearch ?: "")
        }
    }
}