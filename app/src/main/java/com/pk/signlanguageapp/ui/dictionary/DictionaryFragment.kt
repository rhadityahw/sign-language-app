package com.pk.signlanguageapp.ui.dictionary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pk.signlanguageapp.ViewModelFactory
import com.pk.signlanguageapp.data.result.Result
import com.pk.signlanguageapp.databinding.FragmentDictionaryBinding

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.iconExpandLetter?.setOnClickListener {
            val intent = Intent(it.context, ListDictionaryActivity::class.java)
            intent.putExtra(ListDictionaryActivity.EXTRA_LIST_DICTIONARY, "Letter")
            it.context.startActivity(intent)
        }

        binding?.iconExpandWord?.setOnClickListener {
            val intent = Intent(it.context, ListDictionaryActivity::class.java)
            intent.putExtra(ListDictionaryActivity.EXTRA_LIST_DICTIONARY, "Word")
            it.context.startActivity(intent)
        }
    }


}