package com.quackback.example
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.quackback.sdk.Quackback

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(48, 48, 48, 48) }
        layout.addView(Button(this).apply { text = "Open Feedback"; setOnClickListener { Quackback.open() } })
        layout.addView(Button(this).apply { text = "Feature Requests"; setOnClickListener { Quackback.open(board = "feature-requests") } })
        layout.addView(Button(this).apply { text = "Show Trigger"; setOnClickListener { Quackback.showTrigger() } })
        setContentView(layout)
    }
}
