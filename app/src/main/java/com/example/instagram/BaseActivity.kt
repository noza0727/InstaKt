package com.example.instagram

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.bottom_navigation_view.*

abstract class BaseActivity(val navnum: Int) : AppCompatActivity() {
    private val TAG = "BaseActivity"
    fun setupBottomNavigation(){
        bottom_navigation_view.setIconSize(29f,29f)
        bottom_navigation_view.setTextVisibility(false)
        bottom_navigation_view.enableItemShiftingMode(false)
        bottom_navigation_view.enableShiftingMode(false)
        bottom_navigation_view.enableAnimation(false)
        for(i in 0 until bottom_navigation_view.menu.size()){
            bottom_navigation_view.setIconTintList(i,null)
        }

        bottom_navigation_view.setOnNavigationItemSelectedListener {
            val nextActivity =
                when(it.itemId){
                    R.id.nav_item_home -> HomeActivity::class.java
                    R.id.nav_item_search -> SearchActivity::class.java
                    R.id.nav_item_add -> AddActivity::class.java
                    R.id.nav_item_like -> LikeActivity::class.java
                    R.id.nav_item_person -> PersonActivity::class.java
                    else ->{
                        Log.e(TAG, "Unknown item selection clicked: $it")
                        null
                    }
                }
            if(nextActivity != null) {
                val intent = Intent(this, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                overridePendingTransition(0,0)
                true
            }else false
        }
        bottom_navigation_view.menu.getItem(navnum).isChecked = true
    }
}