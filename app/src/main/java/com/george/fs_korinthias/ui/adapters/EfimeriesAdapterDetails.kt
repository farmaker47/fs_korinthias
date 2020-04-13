package com.george.fs_korinthias.ui.adapters

import android.icu.text.IDNA
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.george.fs_korinthias.InfoOnoma
import com.george.fs_korinthias.MainEfimeries
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.databinding.CardEfimeriesDetailsBinding
import com.george.fs_korinthias.databinding.CardEfimeriesMainBinding

class EfimeriesAdapterDetails(
    val onClickListener: OnClickListener
    //val weatherViewModel: WeatherViewModel
) :
    ListAdapter<InfoOnoma, EfimeriesAdapterDetails.ImportantNewsViewHolder>(
        DiffCallback
    ) {

    class ImportantNewsViewHolder(private var binding: CardEfimeriesDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mainEfimeries: InfoOnoma) {
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
    companion object DiffCallback : DiffUtil.ItemCallback<InfoOnoma>() {
        override fun areItemsTheSame(oldItem: InfoOnoma, newItem: InfoOnoma): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: InfoOnoma, newItem: InfoOnoma): Boolean {
            return oldItem.imerominia == newItem.imerominia
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
            CardEfimeriesDetailsBinding.inflate(LayoutInflater.from(parent.context))
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
    class OnClickListener(val clickListener: (mainEfimeries: InfoOnoma) -> Unit) {
        fun onClick(mainEfimeries: InfoOnoma) =
            clickListener(mainEfimeries)
    }
}