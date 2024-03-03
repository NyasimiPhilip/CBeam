package com.android.cbeam

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
class SplashScreenActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if MainActivity is already in memory
        if (!isMainActivityInMemory()) {
            // Delay navigation to MainActivity
            Handler().postDelayed({
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // Finish this activity to prevent it from being on the back stack
            }, 7000) // Delay for 5 seconds (5000 milliseconds)
        } else {
            // If MainActivity is already in memory, directly navigate to it
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish this activity to prevent it from being on the back stack
        }
    }

    private fun isMainActivityInMemory(): Boolean {
        // Check if MainActivity is in the list of running tasks
        val activityManager = getSystemService(ACTIVITY_SERVICE) as? ActivityManager
        val runningTasks = activityManager?.getRunningTasks(Int.MAX_VALUE)
        return runningTasks?.any { it.baseActivity?.className == MainActivity::class.java.name } ?: false
    }
}
