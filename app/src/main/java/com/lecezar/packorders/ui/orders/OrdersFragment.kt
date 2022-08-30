package com.lecezar.packorders.ui.orders

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lecezar.packorders.R
import com.lecezar.packorders.databinding.FragmentOrdersBinding
import com.lecezar.packorders.services.LocationService
import com.lecezar.packorders.ui.base.BaseFragment
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class OrdersFragment :
    BaseFragment<FragmentOrdersBinding, OrdersViewModel>(R.layout.fragment_orders) {

    override val viewModel: OrdersViewModel by inject()

    private val locationPermissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                checkIfLocationIsActivated()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appBar.addOnOffsetChangedListener { _, verticalOffset ->
            binding.refreshLayout.isEnabled = verticalOffset == 0
        }

        binding.refreshLayout.setOnRefreshListener {
            viewModel.refreshOrders()
        }

        binding.rvOrders.adapter = OrdersAdapter(requireContext()) { order ->
            OrdersFragmentDirections.actionOrdersFragmentToOrderDetailsFragment(order.order.id)
                .also {
                    findNavController().navigate(it)
                }
        }

        binding.collapsingToolbar.title = "Orders"

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loading.collect {
                binding.refreshLayout.isRefreshing = it
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkIfLocationIsActivated()
        } else {
            locationPermissionRequestLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun checkIfLocationIsActivated() {
        LocationService.checkLocationSettings(
            context = requireContext(),
            successCallback = {
                viewModel.startListeningForLocation()
            },
            failureCallback = {}
        )
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopListeningForLocation()
    }

}