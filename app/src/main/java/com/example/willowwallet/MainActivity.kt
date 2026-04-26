package com.example.willowwallet

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.willowwallet.data.db.WillowDatabase
import com.example.willowwallet.data.repository.WillowRepository
import com.example.willowwallet.ui.category.CategoriesFragment
import com.example.willowwallet.ui.expense.ExpenseListFragment
import com.example.willowwallet.utils.SessionManager

class MainActivity : AppCompatActivity() {

    lateinit var repository: WillowRepository
    lateinit var sessionManager: SessionManager

    // Temp nav buttons — Person 3 will remove these when adding bottom nav
    private lateinit var btnCategories: Button
    private lateinit var btnExpenses:   Button

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

        btnCategories = findViewById(R.id.btnNavCategories)
        btnExpenses   = findViewById(R.id.btnNavExpenses)

        btnCategories.setOnClickListener { showFragment(CategoriesFragment(), isExpenses = false) }
        btnExpenses.setOnClickListener   { showFragment(ExpenseListFragment(), isExpenses = true) }

        // Start on Categories (Person 1's default)
        if (savedInstanceState == null) {
            showFragment(CategoriesFragment(), isExpenses = false)
        }
    }

    private fun showFragment(fragment: Fragment, isExpenses: Boolean) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()

        // Highlight active tab
        btnCategories.setTextColor(
            if (!isExpenses) getColor(android.R.color.holo_green_light)
            else android.graphics.Color.parseColor("#9BA8B5")
        )
        btnExpenses.setTextColor(
            if (isExpenses) getColor(android.R.color.holo_green_light)
            else android.graphics.Color.parseColor("#9BA8B5")
        )
    }
}