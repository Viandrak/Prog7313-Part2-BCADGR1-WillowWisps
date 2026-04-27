package com.example.willowwallet

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.willowwallet.data.db.WillowDatabase
import com.example.willowwallet.data.repository.WillowRepository
import com.example.willowwallet.ui.category.CategoriesFragment
import com.example.willowwallet.ui.expense.ExpenseListFragment
import com.example.willowwallet.ui.goals.GoalsFragment
import com.example.willowwallet.ui.home.HomeFragment
import com.example.willowwallet.ui.reports.ReportsFragment
import com.example.willowwallet.utils.SessionManager

class MainActivity : AppCompatActivity() {

    // Make these public so fragments can access them
    lateinit var repository: WillowRepository
    lateinit var sessionManager: SessionManager

    private lateinit var btnHome: Button
    private lateinit var btnAdd: Button
    private lateinit var btnCategories: Button
    private lateinit var btnReports: Button
    private lateinit var btnGoals: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize sessionManager and repository
        sessionManager = SessionManager(this)
        val db = WillowDatabase.getInstance(this)
        repository = WillowRepository(
            db.userDao(),
            db.categoryDao(),
            db.expenseDao(),
            db.budgetGoalDao()
        )

        btnHome = findViewById(R.id.btn_nav_home)
        btnAdd = findViewById(R.id.btn_nav_add)
        btnCategories = findViewById(R.id.btn_nav_categories)
        btnReports = findViewById(R.id.btn_nav_reports)
        btnGoals = findViewById(R.id.btn_nav_goals)

        btnHome.setOnClickListener { loadFragment(HomeFragment()) }
        btnAdd.setOnClickListener { loadFragment(ExpenseListFragment()) }
        btnCategories.setOnClickListener { loadFragment(CategoriesFragment()) }
        btnReports.setOnClickListener { loadFragment(ReportsFragment()) }
        btnGoals.setOnClickListener { loadFragment(GoalsFragment()) }

        loadFragment(HomeFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .commit()
    }
}