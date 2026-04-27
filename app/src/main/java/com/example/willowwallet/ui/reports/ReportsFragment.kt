package com.example.willowwallet.ui.reports

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.willowwallet.MainActivity
import com.example.willowwallet.R
import com.example.willowwallet.utils.DateUtils
import com.example.willowwallet.viewmodel.ReportsViewModel
import com.example.willowwallet.viewmodel.ViewModelFactory
import com.google.android.material.button.MaterialButton
import java.util.Calendar


class ReportsFragment : Fragment() {

    private lateinit var vm:           ReportsViewModel
    private lateinit var adapter:      ReportsCategoryAdapter
    private lateinit var tvDateRange:  TextView
    private lateinit var tvTotal:      TextView
    private lateinit var rvCategories: RecyclerView
    private lateinit var btnStart:     MaterialButton
    private lateinit var btnEnd:       MaterialButton
    private lateinit var tvEmpty:      TextView

    // Keep selected calendars so each picker opens at the last-chosen date
    private val calStart = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
    }
    private val calEnd = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_reports, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)

        adapter = ReportsCategoryAdapter()
        rvCategories.layoutManager = LinearLayoutManager(requireContext())
        rvCategories.adapter = adapter

        val activity = requireActivity() as MainActivity
        val factory  = ViewModelFactory(activity.repository)
        vm           = ViewModelProvider(this, factory)[ReportsViewModel::class.java]
        val userId   = activity.sessionManager.getUserId()

        android.util.Log.d("ReportsFragment", "userId=$userId")


        vm.init(userId)

        vm.dateRange.observe(viewLifecycleOwner) { (start, end) ->
            tvDateRange.text = "${DateUtils.formatDate(start)}  \u2192  ${DateUtils.formatDate(end)}"
        }

        vm.categoryTotals.observe(viewLifecycleOwner) { cats ->
            val list = cats ?: emptyList()
            tvEmpty.visibility      = if (list.isEmpty()) View.VISIBLE else View.GONE
            rvCategories.visibility = if (list.isEmpty()) View.GONE   else View.VISIBLE
            adapter.submitList(list)
        }

        vm.totalSpent.observe(viewLifecycleOwner) { total ->
            tvTotal.text = "Total: ${DateUtils.formatCurrency(total ?: 0.0)}"
        }

        btnStart.setOnClickListener { openPicker(isStart = true) }
        btnEnd.setOnClickListener   { openPicker(isStart = false) }
    }

    private fun bindViews(v: View) {
        tvDateRange  = v.findViewById(R.id.tv_report_date_range)
        tvTotal      = v.findViewById(R.id.tv_report_total)
        rvCategories = v.findViewById(R.id.rv_report_categories)
        btnStart     = v.findViewById(R.id.btn_pick_start)
        btnEnd       = v.findViewById(R.id.btn_pick_end)
        tvEmpty      = v.findViewById(R.id.tv_report_empty)
    }

    private fun openPicker(isStart: Boolean) {
        val cal = if (isStart) calStart else calEnd
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                cal.set(year, month, day)
                if (isStart) {
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                } else {
                    cal.set(Calendar.HOUR_OF_DAY, 23)
                    cal.set(Calendar.MINUTE, 59)
                    cal.set(Calendar.SECOND, 59)
                }
                val ok = vm.setCustomRange(calStart.timeInMillis, calEnd.timeInMillis)
                if (!ok) Toast.makeText(
                    requireContext(),
                    "Start date must be before or equal to end date",
                    Toast.LENGTH_SHORT
                ).show()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}