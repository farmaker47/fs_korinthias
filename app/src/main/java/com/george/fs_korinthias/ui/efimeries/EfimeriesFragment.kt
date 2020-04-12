package com.george.fs_korinthias.ui.efimeries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.george.fs_korinthias.MainActivity
import com.george.fs_korinthias.databinding.FragmentEfimeriesBinding
import com.george.fs_korinthias.ui.adapters.EfimeriesAdapterMain
import com.george.fs_korinthias.ui.adapters.NewsAdapter

class EfimeriesFragment : Fragment() {

    private lateinit var notificationsViewModel: EfimeriesViewModel
    private lateinit var binding: FragmentEfimeriesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEfimeriesBinding.inflate(inflater)
        binding.lifecycleOwner = this
        notificationsViewModel =
            ViewModelProvider(this).get(EfimeriesViewModel::class.java)

        binding.viewModel=notificationsViewModel

        binding.efimeriesRecyclerView.adapter =
            EfimeriesAdapterMain(
                EfimeriesAdapterMain.OnClickListener { mainEfimeries ->

                    //(activity as MainActivity?)?.fragmentMethodTransition(mainInfo, imageView)
                })


        /*val textView: TextView = root.findViewById(R.id.text_notifications)
        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        return binding.root
    }
}
