package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.generator

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kennyc.view.MultiStateView
import com.sixtyninefourtwenty.basefragments.BaseFragment
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.R
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.databinding.FragmentGeneratorBinding
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.databinding.FragmentGeneratorStateContentBinding
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.databinding.FragmentGeneratorStateEmptyBinding
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.databinding.FragmentGeneratorStateErrorBinding
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.ViewState
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.clipboardManager
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.preferenceRepository
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.setContentView
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.setEmptyView
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.setErrorView
import kotlinx.coroutines.launch

class GeneratorFragment : BaseFragment<FragmentGeneratorBinding>() {

    private val generatorViewModel: GeneratorViewModel by viewModels { GeneratorViewModel.Factory }
    private val prefs by preferenceRepository()

    private var _emptyBinding: FragmentGeneratorStateEmptyBinding? = null
    private val emptyBinding get() = _emptyBinding!!
    private var _contentBinding: FragmentGeneratorStateContentBinding? = null
    private val contentBinding get() = _contentBinding!!
    private var _errorBinding: FragmentGeneratorStateErrorBinding? = null
    private val errorBinding get() = _errorBinding!!

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentGeneratorBinding {
        return FragmentGeneratorBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(binding: FragmentGeneratorBinding, savedInstanceState: Bundle?) {
        _emptyBinding = FragmentGeneratorStateEmptyBinding.inflate(layoutInflater)
        _contentBinding = FragmentGeneratorStateContentBinding.inflate(layoutInflater)
        _errorBinding = FragmentGeneratorStateErrorBinding.inflate(layoutInflater)
        with(binding.stateView) {
            setEmptyView(emptyBinding.root)
            setContentView(contentBinding.root)
            setErrorView(errorBinding.root)
            viewState = MultiStateView.ViewState.EMPTY
        }
        emptyBinding.generate.setOnClickListener {
            generateInsult()
        }
        contentBinding.generate.setOnClickListener {
            generateInsult()
        }
        contentBinding.share.setOnClickListener {
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, contentBinding.insult.text)
                .setType("text/plain"), null))
        }
        contentBinding.copyToClipboard.setOnClickListener {
            requireContext().clipboardManager.setPrimaryClip(ClipData.newPlainText("generated_insult", contentBinding.insult.text))
            Toast.makeText(requireContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
        }
        errorBinding.retry.setOnClickListener {
            generateInsult()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                generatorViewModel.viewState.collect {
                    when (it) {
                        is ViewState.Empty -> binding.stateView.viewState = MultiStateView.ViewState.EMPTY
                        is ViewState.Error -> binding.stateView.viewState = MultiStateView.ViewState.ERROR
                        is ViewState.Loading -> binding.stateView.viewState = MultiStateView.ViewState.LOADING
                        is ViewState.Content -> {
                            binding.stateView.viewState = MultiStateView.ViewState.CONTENT
                            contentBinding.insult.text = it.data.insult
                        }
                    }
                }
            }
        }

    }

    private fun generateInsult() {
        generatorViewModel.generateRemoteInsult(prefs.generatorLanguage, prefs.generatorUrl)
    }

    override fun onDestroyView(binding: FragmentGeneratorBinding) {
        _emptyBinding = null
        _contentBinding = null
        _errorBinding = null
    }

}