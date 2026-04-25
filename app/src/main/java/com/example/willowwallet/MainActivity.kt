package com.example.willowwallet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.willowwallet.data.db.WillowDatabase
import com.example.willowwallet.data.repository.WillowRepository
import com.example.willowwallet.ui.category.CategoriesFragment
import com.example.willowwallet.utils.SessionManager

class MainActivity : AppCompatActivity() {
    lateinit var repository: WillowRepository
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sessionManager = SessionManager(this)
        val db = WillowDatabase.getInstance(this)
        repository = WillowRepository(
            db.userDao(),
            db.categoryDao(),
            db.expenseDao(),
            db.budgetGoalDao()
        )

        // Temporary — show categories so Person 1 can test
        // Person 3 will replace this with bottom navigation
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, CategoriesFragment())
                .commit()
        }
    }
}