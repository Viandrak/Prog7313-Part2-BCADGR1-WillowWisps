package com.example.willowwallet.ui.reports

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.willowwallet.R
import com.example.willowwallet.data.entities.CategoryTotal
import com.example.willowwallet.utils.DateUtils


class ReportsCategoryAdapter :
    ListAdapter<CategoryTotal, ReportsCategoryAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon:   TextView = view.findViewById(R.id.tv_rep_cat_icon)
        val tvName:   TextView = view.findViewById(R.id.tv_rep_cat_name)
        val tvAmount: TextView = view.findViewById(R.id.tv_rep_cat_amount)
        val vStripe:  View     = view.findViewById(R.id.view_rep_stripe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_category, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvIcon.text   = item.icon
        holder.tvName.text   = item.categoryName
        holder.tvAmount.text = DateUtils.formatCurrency(item.totalSpent)

        try {
            holder.vStripe.setBackgroundColor(Color.parseColor(item.colorHex))
        } catch (e: Exception) {
            holder.vStripe.setBackgroundColor(Color.parseColor("#2dd4bf"))
        }


        holder.tvAmount.setTextColor(
            if (position == 0 && currentList.size > 1) Color.parseColor("#f87171")
            else                                        Color.parseColor("#e2edf5")
        )
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CategoryTotal>() {
            override fun areItemsTheSame(a: CategoryTotal, b: CategoryTotal) =
                a.categoryId == b.categoryId
            override fun areContentsTheSame(a: CategoryTotal, b: CategoryTotal) = a == b
        }
    }
}