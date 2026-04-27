package com.example.willowwallet

import android.app.Application
import com.example.willowwallet.data.db.WillowDatabase
import com.example.willowwallet.data.repository.WillowRepository

/**
 * Application class that holds a single WillowRepository instance.
 * This lets ViewModels access the repository without depending on MainActivity.
 *
 * Register in AndroidManifest.xml:
 *   android:name=".WillowApp"
 */
class WillowApp : Application() {

    lateinit var repository: WillowRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = WillowDatabase.getInstance(this)
        repository = WillowRepository(
            db.userDao(),
            db.categoryDao(),
            db.expenseDao(),
            db.budgetGoalDao()
        )
    }
}