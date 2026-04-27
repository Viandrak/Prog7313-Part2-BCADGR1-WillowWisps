package com.example.willowwallet.ui.expense

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.willowwallet.R
import java.io.File

class FullscreenPhotoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PHOTO_PATH = "extra_photo_path"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_photo)

        supportActionBar?.apply {
            title = "Photo"
            setDisplayHomeAsUpEnabled(true)
        }

        val path = intent.getStringExtra(EXTRA_PHOTO_PATH)
        val imageView: ImageView = findViewById(R.id.ivFullscreen)

        if (!path.isNullOrBlank() && File(path).exists()) {
            Glide.with(this)
                .load(File(path))
                .fitCenter()
                .into(imageView)
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        imageView.setOnClickListener {
            val ab = supportActionBar
            if (ab?.isShowing == true) {
                ab.hide()
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )
            } else {
                ab?.show()
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}