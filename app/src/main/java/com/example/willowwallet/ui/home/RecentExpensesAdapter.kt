package com.example.willowwallet.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.willowwallet.R
import com.example.willowwallet.data.entities.ExpenseWithCategory
import com.example.willowwallet.utils.DateUtils


class RecentExpensesAdapter :
    ListAdapter<ExpenseWithCategory, RecentExpensesAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon:   TextView = view.findViewById(R.id.tv_recent_icon)
        val tvDesc:   TextView = view.findViewById(R.id.tv_recent_desc)
        val tvDate:   TextView = view.findViewById(R.id.tv_recent_date)
        val tvAmount: TextView = view.findViewById(R.id.tv_recent_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_expense, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvIcon.text   = item.category?.icon ?: "\uD83D\uDCB8"
        holder.tvDesc.text   = item.expense.description.ifBlank { "Expense" }
        holder.tvDate.text   = DateUtils.formatDate(item.expense.date)
        holder.tvAmount.text = "-${DateUtils.formatCurrency(item.expense.amount)}"
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ExpenseWithCategory>() {
            override fun areItemsTheSame(a: ExpenseWithCategory, b: ExpenseWithCategory) =
                a.expense.id == b.expense.id
            override fun areContentsTheSame(a: ExpenseWithCategory, b: ExpenseWithCategory) =
                a == b
        }
    }
}