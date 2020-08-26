package com.example.instagram

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity: AppCompatActivity() {
    private val TAG = "EditProfileActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate")
    }
}