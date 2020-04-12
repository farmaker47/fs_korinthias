package com.george.fs_korinthias.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.george.fs_korinthias.MainEfimeries
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.databinding.CardEfimeriesMainBinding

class EfimeriesAdapterMain(
    val onClickListener: OnClickListener
    //val weatherViewModel: WeatherViewModel
) :
    ListAdapter<MainEfimeries, EfimeriesAdapterMain.ImportantNewsViewHolder>(
        DiffCallback
    ) {

    class ImportantNewsViewHolder(private var binding: CardEfimeriesMainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mainEfimeries: MainEfimeries) {
            binding.info = mainEfimeries
            //binding.viewModel = weatherViewModel
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    /**
     * Allows the RecyclerView to determine which items have changed when the [List] of [MainInfo]
     * has been updated.
     */
    companion object DiffCallback : DiffUtil.ItemCallback<MainEfimeries>() {
        override fun areItemsTheSame(oldItem: MainEfimeries, newItem: MainEfimeries): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MainEfimeries, newItem: MainEfimeries): Boolean {
            return oldItem.titlePerioxi == newItem.titlePerioxi
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImportantNewsViewHolder {
        return ImportantNewsViewHolder(
            CardEfimeriesMainBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ImportantNewsViewHolder, position: Int) {

        val mainInfo = getItem(position)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(
                mainInfo
            )
        }


        //bind info
        holder.bind(mainInfo)
    }

    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [MainInfo]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [MainInfo]
     */
    class OnClickListener(val clickListener: (mainEfimeries: MainEfimeries) -> Unit) {
        fun onClick(mainEfimeries: MainEfimeries) =
            clickListener(mainEfimeries)
    }
}