package com.quackback.example
import android.app.Application
import com.quackback.sdk.*

class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Quackback.configure(this, QuackbackConfig(appId = "example", baseURL = "http://localhost:3000"))
        Quackback.identify(userId = "user_example", email = "demo@example.com", name = "Demo User")
        Quackback.on(QuackbackEvent.VOTE) { println("[Quackback] vote: $it") }
        Quackback.on(QuackbackEvent.SUBMIT) { println("[Quackback] submit: $it") }
    }
}
