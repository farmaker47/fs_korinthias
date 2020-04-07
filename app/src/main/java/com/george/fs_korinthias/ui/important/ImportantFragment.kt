package com.george.fs_korinthias.ui.important

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
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

        /*val linearSnapHelper: LinearSnapHelper = SnapHelperOneByOne()
        linearSnapHelper.attachToRecyclerView(binding.importantRecyclerView)*/



        return binding.root
    }

    private class SnapHelperOneByOne : LinearSnapHelper() {
        override fun findTargetSnapPosition(
            layoutManager: RecyclerView.LayoutManager,
            velocityX: Int,
            velocityY: Int
        ): Int {
            if (layoutManager !is ScrollVectorProvider) {
                return RecyclerView.NO_POSITION
            }
            val currentView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
            val myLayoutManager = layoutManager as LinearLayoutManager
            val position1 = myLayoutManager.findFirstVisibleItemPosition()
            val position2 = myLayoutManager.findLastVisibleItemPosition()
            var currentPosition = layoutManager.getPosition(currentView)
            if (velocityX > 400) {
                currentPosition = position2
            } else if (velocityX < 400) {
                currentPosition = position1
            }
            if (currentPosition == RecyclerView.NO_POSITION) {
                return RecyclerView.NO_POSITION
            }
            Log.e("PositionA", currentPosition.toString())
            //changeWheelsFunction(currentPosition)
            return currentPosition
        }
    }
}
