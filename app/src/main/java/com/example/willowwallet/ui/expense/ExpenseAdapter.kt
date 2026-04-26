package com.example.willowwallet.ui.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.willowwallet.data.entities.ExpenseWithCategory
import com.example.willowwallet.databinding.ItemExpenseBinding
import com.example.willowwallet.utils.DateUtils
import java.io.File

class ExpenseAdapter(
    private val onPhotoClick: (ExpenseWithCategory) -> Unit
) : ListAdapter<ExpenseWithCategory, ExpenseAdapter.ExpenseViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ExpenseWithCategory>() {
            override fun areItemsTheSame(a: ExpenseWithCategory, b: ExpenseWithCategory) =
                a.expense.id == b.expense.id
            override fun areContentsTheSame(a: ExpenseWithCategory, b: ExpenseWithCategory) =
                a == b
        }
    }

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val item    = getItem(position)
        val expense = item.expense
        val cat     = item.category
        val b       = holder.binding

        b.tvAmount.text      = DateUtils.formatCurrency(expense.amount)
        b.tvDescription.text = expense.description
        b.tvDate.text        = DateUtils.formatDate(expense.date)
        b.tvTime.text        = "${expense.startTime} – ${expense.endTime}"
        b.tvCategory.text    = if (cat != null) "${cat.icon} ${cat.name}" else "No Category"

        val path = expense.photoPath
        if (!path.isNullOrBlank() && File(path).exists()) {
            b.ivThumbnail.visibility = android.view.View.VISIBLE
            b.ivPhotoIcon.visibility = android.view.View.GONE
            Glide.with(b.ivThumbnail.context)
                .load(File(path))
                .centerCrop()
                .thumbnail(0.25f)
                .into(b.ivThumbnail)
            b.ivThumbnail.setOnClickListener { onPhotoClick(item) }
        } else {
            b.ivThumbnail.visibility = android.view.View.GONE
            b.ivPhotoIcon.visibility = android.view.View.GONE
        }
    }
}