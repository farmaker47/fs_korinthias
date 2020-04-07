package com.george.fs_korinthias.ui.important

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.george.fs_korinthias.databinding.FragmentImportantBinding
import com.george.fs_korinthias.ui.news_adapter.NewsAdapter

class ImportantFragment : Fragment() {

    private lateinit var importantViewModel: ImportantViewModel
    private lateinit var binding: FragmentImportantBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentImportantBinding.inflate(inflater)
        binding.setLifecycleOwner(this)
        importantViewModel = ViewModelProvider(this).get(ImportantViewModel::class.java)

        binding.viewModel = importantViewModel

        /*val handler = Handler()
        handler.postDelayed(
            { binding.importantRecyclerView.adapter =
                NewsAdapter(NewsAdapter.OnClickListener {
                    //viewModel.displayPropertyDetails(it)
                }) },
            5000
        )*/
        binding.importantRecyclerView.adapter =
            NewsAdapter(
                NewsAdapter.OnClickListener {
                    //viewModel.displayPropertyDetails(it)
                })








        return binding.root
    }
}
