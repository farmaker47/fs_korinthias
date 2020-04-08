package com.george.fs_korinthias.ui.allNews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.george.fs_korinthias.MainActivity
import com.george.fs_korinthias.databinding.FragmentAllNewsBinding
import com.george.fs_korinthias.ui.news_adapter.NewsAdapter

class AllNewsFragment : Fragment() {

    private lateinit var allNewsVewModel: AllNewsVewModel
    private lateinit var binding: FragmentAllNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAllNewsBinding.inflate(inflater)
        binding.setLifecycleOwner(this)
        allNewsVewModel = ViewModelProvider(this).get(AllNewsVewModel::class.java)

        binding.viewModel = allNewsVewModel

        binding.allNewsRecyclerView.adapter = NewsAdapter(
            NewsAdapter.OnClickListener {mainInfo,imageView->
                //viewModel.displayPropertyDetails(it)
                (activity as MainActivity?)?.fragmentMethodTransition(mainInfo, imageView)
            })




        return binding.root
    }
}
