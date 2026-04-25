package com.example.willowwallet.ui.category
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.willowwallet.R
import com.example.willowwallet.data.entities.Category
import com.example.willowwallet.utils.DateUtils

class CategoryAdapter(
    private var items: List<Category>,
    private val onEdit: (Category) -> Unit,
    private val onDelete: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView = view.findViewById(R.id.tv_cat_icon)
        val tvName: TextView = view.findViewById(R.id.tv_cat_name)
        val tvBudget: TextView = view.findViewById(R.id.tv_cat_budget)
        val colorBar: View = view.findViewById(R.id.view_color_bar)
        val btnEdit: ImageButton = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cat = items[position]
        holder.tvIcon.text = cat.icon
        holder.tvName.text = cat.name
        holder.tvBudget.text = if (cat.budgetLimit > 0) "Limit: ${DateUtils.formatCurrency(cat.budgetLimit)}" else "No limit set"
        try { holder.colorBar.setBackgroundColor(Color.parseColor(cat.colorHex)) } catch (e: Exception) {}
        holder.btnEdit.setOnClickListener { onEdit(cat) }
        holder.btnDelete.setOnClickListener { onDelete(cat) }
    }

    override fun getItemCount() = items.size
    fun updateData(newItems: List<Category>) { items = newItems; notifyDataSetChanged() }
}