package com.lecezar.packorders.ui.orders

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.lecezar.packorders.R
import com.lecezar.packorders.databinding.ItemOrderBinding
import com.lecezar.packorders.ui.base.BaseAdapter


class OrdersAdapter(
    val context: Context,
    private val onOrderClicked: (order: OrderViewModel) -> Unit
) : BaseAdapter<OrderViewModel, ItemOrderBinding>(
    R.layout.item_order,
    diffUtil = object : DiffUtil.ItemCallback<OrderViewModel>() {
        override fun areItemsTheSame(oldItem: OrderViewModel, newItem: OrderViewModel): Boolean =
            oldItem.order.id == newItem.order.id

        override fun areContentsTheSame(oldItem: OrderViewModel, newItem: OrderViewModel): Boolean =
            oldItem == newItem

    }) {

    override fun bind(
        binding: ItemOrderBinding,
        item: OrderViewModel,
        holder: BaseViewHolder<ItemOrderBinding>
    ) {
        binding.cvOrder.setOnClickListener {
            onOrderClicked.invoke(item)
        }
    }

}