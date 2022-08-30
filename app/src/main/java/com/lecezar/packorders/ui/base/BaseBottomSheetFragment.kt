package com.lecezar.packorders.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lecezar.packorders.BR
import com.lecezar.packorders.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class BaseBottomSheetFragment<Binding : ViewDataBinding, VM : BaseViewModel<*>>(@LayoutRes val layoutId: Int) :
    BottomSheetDialogFragment(), CoroutineScope {

    override val coroutineContext = SupervisorJob() + Dispatchers.Main

    protected lateinit var binding: Binding
        private set

    abstract val viewModel: VM

    protected open val cancelable = true

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.Theme_PackOrders_BottomSheetDialog
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).also { dialog ->
            dialog.setOnShowListener {
                val bottomSheetDialog = it as BottomSheetDialog
                val bottomSheet: FrameLayout =
                    bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
                        ?: return@setOnShowListener
                BottomSheetBehavior.from(bottomSheet).run {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    skipCollapsed = true
                    isHideable = cancelable
                    isDraggable = cancelable
                }
                isCancelable = cancelable
            }
        }
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)
        binding.lifecycleOwner = this
        binding.setVariable(BR.host, this)
        binding.setVariable(BR.viewModel, viewModel)

        viewModel.errorHandler { onViewModelError(it) }
        return binding.root
    }

    open fun onViewModelError(exception: Throwable) {
        context?.apply {
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
        }
    }

}