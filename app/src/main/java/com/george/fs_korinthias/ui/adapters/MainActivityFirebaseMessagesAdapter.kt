package com.george.fs_korinthias.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.george.fs_korinthias.FirebaseMainActivityMessages
import com.george.fs_korinthias.InfoOnoma
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.databinding.CardEfimeriesDetailsBinding
import com.george.fs_korinthias.databinding.FirebaseMainActivityListItemBinding

class MainActivityFirebaseMessagesAdapter(
    val onClickListener: OnClickListener
    //val weatherViewModel: WeatherViewModel
) :
    ListAdapter<FirebaseMainActivityMessages, MainActivityFirebaseMessagesAdapter.ImportantNewsViewHolder>(
        DiffCallback
    ) {

    class ImportantNewsViewHolder(private var binding: FirebaseMainActivityListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mainMessage: FirebaseMainActivityMessages) {
            binding.viewmodel = mainMessage
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
    companion object DiffCallback : DiffUtil.ItemCallback<FirebaseMainActivityMessages>() {
        override fun areItemsTheSame(
            oldItem: FirebaseMainActivityMessages,
            newItem: FirebaseMainActivityMessages
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: FirebaseMainActivityMessages,
            newItem: FirebaseMainActivityMessages
        ): Boolean {
            return oldItem.message == newItem.message
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
            FirebaseMainActivityListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
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
    class OnClickListener(val clickListener: (mainMessages: FirebaseMainActivityMessages) -> Unit) {
        fun onClick(mainMessages: FirebaseMainActivityMessages) =
            clickListener(mainMessages)
    }
}