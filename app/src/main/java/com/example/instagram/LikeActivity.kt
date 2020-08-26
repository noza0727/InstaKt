package com.example.instagram

import android.os.Bundle
import android.util.Log

class LikeActivity : BaseActivity(3){
    private val TAG = "LikeActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()

    }
}