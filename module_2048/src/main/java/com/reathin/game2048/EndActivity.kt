package com.reathin.game2048

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EndActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end)

        val score = intent.getIntExtra("SCORE", 0)
        findViewById<TextView>(R.id.scoreTextView).text = "最终得分: $score"

        findViewById<Button>(R.id.restartButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}