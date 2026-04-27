package com.example.willowwallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.willowwallet.data.entities.BudgetGoal
import com.example.willowwallet.data.repository.WillowRepository
import com.example.willowwallet.utils.DateUtils
import kotlinx.coroutines.launch


class GoalsViewModel(private val repository: WillowRepository) : ViewModel() {

    val year  = DateUtils.currentYear()
    val month = DateUtils.currentMonth()


    fun observeGoal(userId: Int) =
        repository.observeGoalForMonth(userId, year, month).asLiveData()

    /**
     * Validates then saves the goal.
     * @param onResult  null = success, String = error message to show the user.
     */
    fun saveGoal(userId: Int, minGoal: Double, maxGoal: Double, onResult: (String?) -> Unit) {
        // Validation rules (mirrored in GoalsValidationTest.kt)
        when {
            minGoal < 0.0 || maxGoal < 0.0 ->
                return onResult("Goals cannot be negative.")
            maxGoal == 0.0 ->
                return onResult("Maximum budget must be above R 0.00. Drag the slider to set it.")
            maxGoal < minGoal ->
                return onResult("Maximum goal must be greater than or equal to the minimum goal.")
        }

        viewModelScope.launch {
            try {
                repository.saveGoal(
                    BudgetGoal(
                        userId      = userId,
                        year        = year,
                        month       = month,
                        minimumGoal = minGoal,
                        maximumGoal = maxGoal
                    )
                )
                android.util.Log.i("GoalsViewModel",
                    "Goal saved userId=$userId min=$minGoal max=$maxGoal ($month/$year)")
                onResult(null)
            } catch (e: Exception) {
                android.util.Log.e("GoalsViewModel", "Save failed: ${e.message}", e)
                onResult("Could not save goal. Please try again.")
            }
        }
    }
}