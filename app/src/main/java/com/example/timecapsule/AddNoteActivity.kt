package com.example.timecapsule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class AddNoteActivity : ComponentActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getInstance(this)

        setContent {
            MaterialTheme {
                AddNoteScreen(
                    db = db,
                    onClose = { finish() },
                    onNoteAdded = {
                        setResult(RESULT_OK)
                        finish()
                    }
                )
            }
        }

        // Optionally, set window height as before:
        val window = this.window
        val params = window.attributes
        params.height = (resources.displayMetrics.heightPixels * 0.8).toInt()
        window.attributes = params
    }
}
