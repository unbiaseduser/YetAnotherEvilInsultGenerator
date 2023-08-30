package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.insultinfo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sixtyninefourtwenty.bottomsheetalertdialog.BottomSheetAlertDialogFragmentViewBuilder
import com.sixtyninefourtwenty.bottomsheetalertdialog.DialogButtonProperties
import com.sixtyninefourtwenty.bottomsheetalertdialog.misc.BaseBottomSheetAlertDialogFragment
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.R
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.databinding.DialogInsultInfoBinding
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.*
import kotlinx.coroutines.launch

class InsultInfoDialog : BaseBottomSheetAlertDialogFragment<DialogInsultInfoBinding>() {

    private val args: InsultInfoDialogArgs by navArgs()
    private val insult: Insult by lazy { Insult.fromJson(args.insultJson) }
    private val insultInfoViewModel: InsultInfoViewModel by viewModels { InsultInfoViewModel.Factory }
    
    private val addInsultToFavorites = View.OnClickListener {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_to_favorites)
            .setMessage(R.string.add_insult_to_favorites_prompt)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                insultInfoViewModel.updateFavoriteStatus(insult, true) {
                    Toast.makeText(requireContext(), R.string.added_to_favorites, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
    
    private val removeInsultFromFavorites = View.OnClickListener {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_from_favorites)
            .setMessage(R.string.remove_insult_from_favorites_prompt)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                insultInfoViewModel.updateFavoriteStatus(insult, false) {
                    Toast.makeText(requireContext(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
    
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogInsultInfoBinding {
        return DialogInsultInfoBinding.inflate(inflater, container, false)
    }

    override fun initDialog(binding: DialogInsultInfoBinding): BottomSheetAlertDialogFragmentViewBuilder {
        return BottomSheetAlertDialogFragmentViewBuilder(binding.root, this)
            .setTitle(R.string.insult_info)
            .setPositiveButton(DialogButtonProperties(textRes = android.R.string.ok))
    }

    override fun onViewCreated(binding: DialogInsultInfoBinding, savedInstanceState: Bundle?) {
        val insult = insult.also(insultInfoViewModel::setCurrentlyDisplayedInsult)
        binding.delete.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_insult)
                .setMessage(R.string.delete_insult_confirmation)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    insultInfoViewModel.deleteLocalInsult(insult) {
                        Toast.makeText(requireContext(), R.string.insult_deleted, Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                insultInfoViewModel.getFavoriteStatus(insult).collect {
                    if (it) {
                        with(binding.favorite) {
                            setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(requireContext(), R.drawable.favorite)?.tint(Color.RED), null, null, null)
                            text = getString(R.string.currently_favorited)
                            setOnClickListener(removeInsultFromFavorites)
                        }
                        binding.delete.visibility = View.GONE
                    } else {
                        with(binding.favorite) {
                            setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite, 0, 0, 0)
                            text = getString(R.string.not_favorited)
                            setOnClickListener(addInsultToFavorites)
                        }
                        binding.delete.visibility = View.VISIBLE
                    }
                }
            }
        }
        binding.share.setOnClickListener {
            insult.share(requireContext())
        }
        binding.copyToClipboard.setOnClickListener {
            insult.copyToClipboard(requireContext())
        }
        if (insult.hasCreatedBy()) {
            binding.createdBy.text = insult.createdBy
        } else {
            binding.createdBy.visibility = View.GONE
        }
        if (insult.hasComment()) {
            binding.comment.text = insult.comment
        } else {
            binding.comment.visibility = View.GONE
        }
        binding.language.setText(insult.getLanguage().languageLabelResId)
        binding.createdTime.text = insult.created
    }
}