package com.george.fs_korinthias.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.databinding.CardImportantListItemBinding
import kotlinx.android.synthetic.main.card_important_list_item.view.*


class NewsAdapter(
    val onClickListener: OnClickListener
    //val weatherViewModel: WeatherViewModel
) :
    ListAdapter<MainInfo, NewsAdapter.ImportantNewsViewHolder>(
        DiffCallback
    ) {

    class ImportantNewsViewHolder(private var binding: CardImportantListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mainInfo: MainInfo) {
            binding.info = mainInfo
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
    companion object DiffCallback : DiffUtil.ItemCallback<MainInfo>() {
        override fun areItemsTheSame(oldItem: MainInfo, newItem: MainInfo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MainInfo, newItem: MainInfo): Boolean {
            return oldItem.link == newItem.link
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
            CardImportantListItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: ImportantNewsViewHolder, position: Int) {

        val mainInfo = getItem(position)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(
                mainInfo,
                holder.itemView.imageImportant
            )
        }
        //Animation
        val fadeIn: Animation = AlphaAnimation(0.0f, 1.0f)
        fadeIn.duration = 700
        fadeIn.startOffset = 300
        fadeIn.fillAfter = true
        holder.itemView.textTitleImportant.startAnimation(fadeIn)
        holder.itemView.textDateImportant.startAnimation(fadeIn)

        //bind info
        holder.bind(mainInfo)
    }

    /**
     * Custom listener that handles clicks on [RecyclerView] items.  Passes the [MainInfo]
     * associated with the current item to the [onClick] function.
     * @param clickListener lambda that will be called with the current [MainInfo]
     */
    class OnClickListener(val clickListener: (mainInfo: MainInfo, imageView: ImageView) -> Unit) {
        fun onClick(mainInfo: MainInfo, imageView: ImageView) =
            clickListener(mainInfo, imageView)
    }
}