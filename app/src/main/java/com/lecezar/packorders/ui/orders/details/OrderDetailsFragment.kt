package com.lecezar.packorders.ui.orders.details

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.navigation.fragment.navArgs
import com.lecezar.packorders.R
import com.lecezar.packorders.databinding.FragmentOrderDetailsBinding
import com.lecezar.packorders.models.OrderStatus
import com.lecezar.packorders.ui.base.BaseBottomSheetFragment
import org.koin.android.ext.android.inject

class OrderDetailsFragment :
    BaseBottomSheetFragment<FragmentOrderDetailsBinding, OrderDetailsViewModel>(
        R.layout.fragment_order_details
    ) {

    override val viewModel: OrderDetailsViewModel by inject()

    private val args by navArgs<OrderDetailsFragmentArgs>()
    private var statusMenu: PopupMenu? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadOrder(args.orderId)
    }

    fun onChangeStatusClicked() {
        if (statusMenu == null) {
            statusMenu = buildPopupMenu().apply { show() }
        } else {
            statusMenu?.show()
        }
    }

    private fun buildPopupMenu() = PopupMenu(requireContext(), binding.tvFlower).also {
        it.menuInflater.inflate(R.menu.menu_order_status, it.menu)
        it.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.newStatus -> {
                    viewModel.updateOrderStatus(OrderStatus.NEW)
                }
                R.id.pendingStatus -> {
                    viewModel.updateOrderStatus(OrderStatus.PENDING)
                }
                R.id.deliveredStatus -> {
                    viewModel.updateOrderStatus(OrderStatus.DELIVERED)
                }
            }
            true
        }
    }


}