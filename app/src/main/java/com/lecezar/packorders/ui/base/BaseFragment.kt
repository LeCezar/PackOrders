package com.lecezar.packorders.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.lecezar.packorders.BR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class BaseFragment<Binding : ViewDataBinding, VM : BaseViewModel<*>>(@LayoutRes val layoutId: Int) :
    Fragment(), CoroutineScope {

    override val coroutineContext = SupervisorJob() + Dispatchers.Main

    protected lateinit var binding: Binding
        private set

    abstract val viewModel: VM

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