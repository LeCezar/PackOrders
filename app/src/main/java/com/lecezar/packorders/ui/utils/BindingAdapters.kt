package com.lecezar.packorders.ui.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

object BindingAdapters {

    @BindingAdapter("visible")
    @JvmStatic
    fun setVisible(view: View, visible: Boolean?) {
        view.visibility = if (visible == true) View.VISIBLE else View.GONE
    }

    @BindingAdapter("items")
    @JvmStatic
    fun <T> setItems(rv: RecyclerView, items: List<T>?) {
        if (items != null) {
            (rv.adapter as? ListAdapter<T, RecyclerView.ViewHolder>)?.submitList(items)
        }
    }

    @BindingAdapter("loadImage")
    @JvmStatic
    fun loadImage(imageView: ImageView, url: String?) {
        if (url != null) {
            Glide.with(imageView.context)
                .load(url)
                .into(imageView)
        }
    }

}