package com.example.instagram

import android.os.Bundle
import android.util.Log

class PersonActivity : BaseActivity(4){
    private val TAG = "PersonActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        Log.d(TAG, "onCreate")
        setupBottomNavigation()
    }
}
