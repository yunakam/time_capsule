package com.example.timecapsule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.compose.AppTheme

class AddNoteActivity : ComponentActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFinishOnTouchOutside(true)

        db = AppDatabase.getInstance(this)

        setContent {
            AppTheme(dynamicColor = false) {
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

        val window = this.window
        val params = window.attributes
        params.height = (resources.displayMetrics.heightPixels * 0.8).toInt()
        window.attributes = params
    }
}
