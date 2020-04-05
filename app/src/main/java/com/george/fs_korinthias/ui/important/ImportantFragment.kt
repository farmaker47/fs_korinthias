package com.george.fs_korinthias.ui.important

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.george.fs_korinthias.R
import com.george.fs_korinthias.databinding.FragmentImportantBinding

class ImportantFragment : Fragment() {

    private lateinit var importantViewModel: ImportantViewModel
    private lateinit var binding:FragmentImportantBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding=FragmentImportantBinding.inflate(inflater)
        binding.setLifecycleOwner(this)
        importantViewModel = ViewModelProvider(this).get(ImportantViewModel::class.java)








        return binding.root
    }
}
