package com.example.willowwallet.data.db
import androidx.room.*
import com.example.willowwallet.data.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY name ASC")
    fun getCategoriesForUser(userId: Int): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY name ASC")
    suspend fun getCategoriesForUserOnce(userId: Int): List<Category>

    @Query("SELECT COUNT(*) FROM categories WHERE userId = :userId AND LOWER(name) = LOWER(:name) AND id != :excludeId")
    suspend fun countDuplicateName(userId: Int, name: String, excludeId: Int = 0): Int
}