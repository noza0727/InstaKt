package com.example.instagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.forEach
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlin.math.log


class HomeActivity : BaseActivity(0) {
    private val TAG = "HomeActivity"
    private lateinit var authentication: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG,"onCreate")
        setupBottomNavigation()
        authentication = FirebaseAuth.getInstance()
//        authentication.signOut()
//        authentication.signInWithEmailAndPassword("shahnoza0727@gmail.com", "password")
//            .addOnCompleteListener{
//                if(it.isSuccessful){
//                    Log.d(TAG, "sign in is successful")
//                }else{
//                    Log.d(TAG, "sign in is unsuccessful", it.exception)
//                }
//        }

        sign_out_text.setOnClickListener{
            authentication.signOut()
        }
        authentication.addAuthStateListener {
            if(it.currentUser == null){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }



    override fun onStart(){
        super.onStart()
        if(authentication.currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}