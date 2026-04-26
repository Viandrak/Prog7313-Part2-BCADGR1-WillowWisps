package com.example.willowwallet.data.repository
import com.example.willowwallet.data.db.*
import com.example.willowwallet.data.entities.*
import com.example.willowwallet.utils.HashUtils
import kotlinx.coroutines.flow.Flow

class WillowRepository(
    private val userDao: UserDao,
    private val categoryDao: CategoryDao,
    private val expenseDao: ExpenseDao,
    private val budgetGoalDao: BudgetGoalDao
) {
    suspend fun registerUser(username: String, password: String, displayName: String): Long {
        if (userDao.usernameExists(username) > 0) return -1L
        return userDao.insertUser(User(username = username, passwordHash = HashUtils.sha256(password), displayName = displayName))
    }

    suspend fun login(username: String, password: String): User? {
        val user = userDao.getUserByUsername(username) ?: return null
        return if (HashUtils.sha256(password) == user.passwordHash) user else null
    }

    suspend fun getUserById(userId: Int) = userDao.getUserById(userId)

    fun getCategoriesForUser(userId: Int) = categoryDao.getCategoriesForUser(userId)
    suspend fun getCategoriesOnce(userId: Int) = categoryDao.getCategoriesForUserOnce(userId)
    suspend fun saveCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
    suspend fun isCategoryNameDuplicate(userId: Int, name: String, excludeId: Int = 0) =
        categoryDao.countDuplicateName(userId, name, excludeId) > 0

    suspend fun saveExpense(expense: Expense) = expenseDao.insertExpense(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
    fun getExpensesInPeriod(userId: Int, startDate: Long, endDate: Long) = expenseDao.getExpensesInPeriod(userId, startDate, endDate)
    fun getCategoryTotalsInPeriod(userId: Int, startDate: Long, endDate: Long) = expenseDao.getCategoryTotalsInPeriod(userId, startDate, endDate)
    fun getTotalSpentInPeriod(userId: Int, startDate: Long, endDate: Long) = expenseDao.getTotalSpentInPeriod(userId, startDate, endDate)
    fun getRecentExpenses(userId: Int) = expenseDao.getRecentExpenses(userId)

    suspend fun saveGoal(goal: BudgetGoal) = budgetGoalDao.insertOrUpdateGoal(goal)
    suspend fun getGoalForMonth(userId: Int, year: Int, month: Int) = budgetGoalDao.getGoalForMonth(userId, year, month)
    fun observeGoalForMonth(userId: Int, year: Int, month: Int) = budgetGoalDao.observeGoalForMonth(userId, year, month)
}