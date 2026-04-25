package com.example.willowwallet.data.db
import androidx.room.*
import com.example.willowwallet.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Transaction
    @Query("SELECT * FROM expenses WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesInPeriod(userId: Int, startDate: Long, endDate: Long): Flow<List<ExpenseWithCategory>>

    @Query("""
        SELECT e.categoryId, COALESCE(c.name,'Uncategorized') AS categoryName,
        COALESCE(c.colorHex,'#7a9ab8') AS colorHex, COALESCE(c.icon,'❓') AS icon,
        SUM(e.amount) AS totalSpent
        FROM expenses e LEFT JOIN categories c ON e.categoryId = c.id
        WHERE e.userId = :userId AND e.date >= :startDate AND e.date <= :endDate
        GROUP BY e.categoryId ORDER BY totalSpent DESC
    """)
    fun getCategoryTotalsInPeriod(userId: Int, startDate: Long, endDate: Long): Flow<List<CategoryTotal>>

    @Query("SELECT COALESCE(SUM(amount),0.0) FROM expenses WHERE userId = :userId AND date >= :startDate AND date <= :endDate")
    fun getTotalSpentInPeriod(userId: Int, startDate: Long, endDate: Long): Flow<Double>

    @Transaction
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC, createdAt DESC LIMIT :limit")
    fun getRecentExpenses(userId: Int, limit: Int = 5): Flow<List<ExpenseWithCategory>>
}