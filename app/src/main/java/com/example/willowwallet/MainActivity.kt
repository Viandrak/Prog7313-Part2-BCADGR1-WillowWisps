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

/**
 *
 * REFERENCES:
 *
 * 1. Room Database for data persistence:
 *    Android Developers, 2025. Room persistence library. [Online]
 *    Available at: https://developer.android.com/training/data-storage/room
 *    [Accessed 25 April 2026].
 *
 * 2. ViewModel for managing UI data:
 *    Android Developers, 2024. ViewModel overview. [Online]
 *    Available at: https://developer.android.com/topic/libraries/architecture/viewmodel
 *    [Accessed 25 April 2026].
 *
 * 3. Glide for image loading:
 *    Bumptech, 2025. Glide image loading library. [Online]
 *    Available at: https://bumptech.github.io/glide/
 *    [Accessed 26 April 2026].
 *
 * 4. Camera functionality for receipt photos:
 *    Android Developers, 2024. Take photos. [Online]
 *    Available at: https://developer.android.com/media/camera/get-started-with-camera
 *    [Accessed 26 April 2026].
 *
 * 5. Kotlin programming language:
 *    JetBrains, 2025. Kotlin documentation. [Online]
 *    Available at: https://blog.jetbrains.com/kotlin/2025/12/kotlin-2-3-0-released/
 *    [Accessed 27 April 2026].
 *
 * 6. Kotlin Coroutines for asynchronous operations:
 *    Android Developers, 2024. Kotlin coroutines on Android. [Online]
 *    Available at: https://developer.android.com/kotlin/coroutines
 *    [Accessed 27 April 2026].
 *
 * 7. GitHub Actions for automated testing:
 *    GitHub Actions, 2025. Automating builds and tests. [Online]
 *    Available at: https://docs.github.com/en/actions/tutorials/build-and-test-code
 *    [Accessed 27 April 2026].
 *
 * 8. SeekBar for budget goal sliders:
 *    GeeksforGeeks, 2019. SeekBar in Kotlin. [Online]
 *    Available at: https://www.geeksforgeeks.org/kotlin/seekbar-in-kotlin/
 *    [Accessed 28 April 2026].
 *
 * 9. LinearLayout for button navigation bar:
 *    Android Developers, n.d. LinearLayout. [Online]
 *    Available at: https://developer.android.com/reference/android/widget/LinearLayout
 *    [Accessed 28 April 2026].
 *
 * 10. MaterialDatePicker for date range selection:
 *     Android Developers, 2025. MaterialDatePicker.Builder API reference. [Online]
 *     Available at: https://developer.android.com/reference/com/google/android/material/datepicker/MaterialDatePicker.Builder
 *     [Accessed 28 April 2026].
 *
 * 11. Fragment transactions for screen navigation:
 *     Android Developers, 2024. Fragments. [Online]
 *     Available at: https://developer.android.com/guide/fragments
 *     [Accessed 28 April 2026].
 */
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