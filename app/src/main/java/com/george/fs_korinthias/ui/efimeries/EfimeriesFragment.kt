package com.george.fs_korinthias.ui.efimeries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.george.fs_korinthias.MainActivity
import com.george.fs_korinthias.R
import com.george.fs_korinthias.databinding.FragmentEfimeriesBinding
import com.george.fs_korinthias.ui.adapters.EfimeriesAdapterMain
import com.george.fs_korinthias.ui.adapters.NewsAdapter
import org.koin.android.viewmodel.ext.android.viewModel

class EfimeriesFragment : Fragment() {

    //private lateinit var notificationsViewModel: EfimeriesViewModel
    private lateinit var binding: FragmentEfimeriesBinding
    private val notificationsViewModel:EfimeriesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEfimeriesBinding.inflate(inflater)
        binding.lifecycleOwner = this
        //notificationsViewModel =ViewModelProvider(this).get(EfimeriesViewModel::class.java)

        binding.viewModel=notificationsViewModel

        binding.efimeriesRecyclerView.adapter =
            EfimeriesAdapterMain(
                EfimeriesAdapterMain.OnClickListener { mainEfimeries ->

                    (activity as MainActivity?)?.fragmentMethodTransitionEfimeries(mainEfimeries)
                })

        //running the animation at the beggining of showing the list
        notificationsViewModel.titlePerioxes.observe(viewLifecycleOwner, Observer {
            runLayoutAnimation(binding.efimeriesRecyclerView)
        })



        /*val textView: TextView = root.findViewById(R.id.text_notifications)
        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return binding.root
    }

    //Animation when recyclerview enters screen
    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller =
            AnimationUtils.loadLayoutAnimation(
                context,
                R.anim.layout_animation_fall_down
            )
        recyclerView.layoutAnimation = controller
/*
        Objects.requireNonNull(recyclerView.adapter)?.notifyDataSetChanged()
*/
        recyclerView.scheduleLayoutAnimation()
    }
}
