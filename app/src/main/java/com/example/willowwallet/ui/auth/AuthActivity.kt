package com.example.willowwallet.ui.auth
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.willowwallet.R
import com.example.willowwallet.data.db.WillowDatabase
import com.example.willowwallet.data.repository.WillowRepository
import com.example.willowwallet.MainActivity
import com.example.willowwallet.utils.SessionManager

class AuthActivity : AppCompatActivity() {
    lateinit var repository: WillowRepository
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        val db = WillowDatabase.getInstance(this)
        repository = WillowRepository(db.userDao(), db.categoryDao(), db.expenseDao(), db.budgetGoalDao())
        sessionManager = SessionManager(this)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.auth_container, LoginFragment()).commit()
        }
    }

    fun onAuthSuccess(userId: Int, username: String, displayName: String) {
        sessionManager.saveSession(userId, username, displayName)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}