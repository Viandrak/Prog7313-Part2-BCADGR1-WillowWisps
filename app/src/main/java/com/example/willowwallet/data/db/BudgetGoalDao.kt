package com.example.willowwallet.data.db
import androidx.room.*
import com.example.willowwallet.data.entities.BudgetGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGoal(goal: BudgetGoal): Long

    @Query("SELECT * FROM budget_goals WHERE userId = :userId AND year = :year AND month = :month LIMIT 1")
    suspend fun getGoalForMonth(userId: Int, year: Int, month: Int): BudgetGoal?

    @Query("SELECT * FROM budget_goals WHERE userId = :userId AND year = :year AND month = :month LIMIT 1")
    fun observeGoalForMonth(userId: Int, year: Int, month: Int): Flow<BudgetGoal?>
}