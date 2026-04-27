package com.example.willowwallet.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.willowwallet.MainActivity
import com.example.willowwallet.R
import com.example.willowwallet.utils.DateUtils
import com.example.willowwallet.viewmodel.GoalsViewModel
import com.example.willowwallet.viewmodel.ViewModelFactory
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class GoalsFragment : Fragment() {

    private lateinit var vm: GoalsViewModel

    private lateinit var seekMin:       SeekBar
    private lateinit var seekMax:       SeekBar
    private lateinit var tvMinValue:    TextView
    private lateinit var tvMaxValue:    TextView
    private lateinit var tvCurrentGoal: TextView
    private lateinit var tvMonthTitle:  TextView
    private lateinit var btnSave:       MaterialButton

    // 500 steps × R100 = R50 000 maximum
    private val STEP      = 100
    private val MAX_STEPS = 500

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_goals, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupSliders()

        val activity = requireActivity() as MainActivity
        val factory  = ViewModelFactory(activity.repository)
        vm           = ViewModelProvider(this, factory)[GoalsViewModel::class.java]
        val userId   = activity.sessionManager.getUserId()

        android.util.Log.d("GoalsFragment", "userId=$userId")


        tvMonthTitle.text = "Budget for " +
                SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                    .format(Calendar.getInstance().time)

        // Load saved goal and pre-fill sliders
        vm.observeGoal(userId).observe(viewLifecycleOwner) { goal ->
            if (goal != null) {
                seekMin.progress = (goal.minimumGoal / STEP).toInt().coerceIn(0, MAX_STEPS)
                seekMax.progress = (goal.maximumGoal / STEP).toInt().coerceIn(0, MAX_STEPS)
                tvMinValue.text  = DateUtils.formatCurrency(goal.minimumGoal)
                tvMaxValue.text  = DateUtils.formatCurrency(goal.maximumGoal)
                tvCurrentGoal.text =
                    "Saved: ${DateUtils.formatCurrency(goal.minimumGoal)} \u2013 ${DateUtils.formatCurrency(goal.maximumGoal)}"
            } else {
                tvCurrentGoal.text = "No goal set for this month yet."
            }
        }

        btnSave.setOnClickListener {
            val min = (seekMin.progress * STEP).toDouble()
            val max = (seekMax.progress * STEP).toDouble()
            vm.saveGoal(userId, min, max) { error ->
                if (error == null) {
                    android.util.Log.i("GoalsFragment", "Goal saved min=$min max=$max")
                    Toast.makeText(requireContext(), "Goal saved!", Toast.LENGTH_SHORT).show()
                } else {
                    android.util.Log.w("GoalsFragment", "Validation: $error")
                    Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun bindViews(v: View) {
        seekMin       = v.findViewById(R.id.seek_min_goal)
        seekMax       = v.findViewById(R.id.seek_max_goal)
        tvMinValue    = v.findViewById(R.id.tv_min_value)
        tvMaxValue    = v.findViewById(R.id.tv_max_value)
        tvCurrentGoal = v.findViewById(R.id.tv_current_goal_summary)
        tvMonthTitle  = v.findViewById(R.id.tv_goal_month)
        btnSave       = v.findViewById(R.id.btn_save_goal)
    }

    private fun setupSliders() {
        seekMin.max = MAX_STEPS
        seekMax.max = MAX_STEPS
        tvMinValue.text = DateUtils.formatCurrency(0.0)
        tvMaxValue.text = DateUtils.formatCurrency(0.0)

        seekMin.setOnSeekBarChangeListener(listener { steps ->
            tvMinValue.text = DateUtils.formatCurrency((steps * STEP).toDouble())
        })
        seekMax.setOnSeekBarChangeListener(listener { steps ->
            tvMaxValue.text = DateUtils.formatCurrency((steps * STEP).toDouble())
        })
    }

    private fun listener(onProgress: (Int) -> Unit) =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, fromUser: Boolean) = onProgress(p)
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        }
}