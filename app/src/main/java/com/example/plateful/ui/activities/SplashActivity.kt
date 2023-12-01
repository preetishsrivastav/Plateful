package com.example.plateful.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.plateful.R
import com.example.plateful.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashBinding: ActivitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation)
        splashBinding.tvAppName.animation = splashAnimation

        val intent = Intent(this, MainActivity::class.java)

        splashAnimation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(intent)
                    finish()
                }, 1000)
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }
        })


    }
}