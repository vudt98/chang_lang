package com.example.storagetest.first

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.storagetest.R
import com.example.storagetest.dataStore
import com.example.storagetest.databinding.FirstFragmentBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

const val KEY_LANG = "lang"

class FirstFragment : Fragment() {

    private lateinit var binding: FirstFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FirstFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lang: Flow<String>? = context?.dataStore?.data?.map { preferences ->
                preferences[stringPreferencesKey(KEY_LANG)] ?: ""
            }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                lang?.collect {
                    binding.textView.text = getString(R.string.text_temp)                }
            }
        }

        bindAction()
    }

    private fun bindAction() {
        binding.btnEn.setOnClickListener {
            lifecycleScope.launch {
                saveLang("en", KEY_LANG)
            }
        }

        binding.btnVn.setOnClickListener {
            lifecycleScope.launch {
                saveLang("vi", KEY_LANG)
            }
        }
    }

    private suspend fun saveLang(lang: String, key: String) {
        context?.dataStore?.edit {
            it[stringPreferencesKey(key)] = lang
        }
    }
}