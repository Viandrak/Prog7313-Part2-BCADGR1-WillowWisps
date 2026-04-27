package com.example.willowwallet.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.willowwallet.R
import com.example.willowwallet.data.entities.CategoryTotal
import com.example.willowwallet.utils.DateUtils


class CategoryProgressAdapter :
    ListAdapter<CategoryTotal, CategoryProgressAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon:   TextView    = view.findViewById(R.id.tv_cat_icon)
        val tvName:   TextView    = view.findViewById(R.id.tv_cat_name)
        val tvAmount: TextView    = view.findViewById(R.id.tv_cat_amount)
        val progress: ProgressBar = view.findViewById(R.id.progress_cat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_progress, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item     = getItem(position)
        val maxSpent = currentList.maxOfOrNull { it.totalSpent } ?: 1.0

        holder.tvIcon.text   = item.icon
        holder.tvName.text   = item.categoryName
        holder.tvAmount.text = DateUtils.formatCurrency(item.totalSpent)

        val pct = if (maxSpent > 0.0) ((item.totalSpent / maxSpent) * 100).toInt() else 0
        holder.progress.progress = pct

        try {
            holder.progress.progressTintList =
                ColorStateList.valueOf(Color.parseColor(item.colorHex))
        } catch (e: Exception) {
            holder.progress.progressTintList =
                ColorStateList.valueOf(Color.parseColor("#2dd4bf"))
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CategoryTotal>() {
            override fun areItemsTheSame(a: CategoryTotal, b: CategoryTotal) =
                a.categoryId == b.categoryId
            override fun areContentsTheSame(a: CategoryTotal, b: CategoryTotal) = a == b
        }
    }
}