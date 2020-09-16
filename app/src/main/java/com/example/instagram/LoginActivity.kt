package com.example.instagram

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity: AppCompatActivity(), KeyboardVisibilityEventListener, TextWatcher,
    View.OnClickListener {
    private val TAG = "LoginActivity"
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        KeyboardVisibilityEvent.setEventListener(this,this)
        login_btn.isEnabled = false
        email_input.addTextChangedListener(this)
        password_input.addTextChangedListener(this)
        login_btn.setOnClickListener(this)
        sign_up_text.setOnClickListener(this)

        auth = FirebaseAuth.getInstance()
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if(isKeyboardOpen){
            scrollView_login.scrollTo(0, scrollView_login.bottom)
            sign_up_text.visibility = View.GONE
        }else {
            scrollView_login.scrollTo(0, scrollView_login.top)
            sign_up_text.visibility = View.VISIBLE
        }
    }

    override fun afterTextChanged(s: Editable?) {
        login_btn.isEnabled = validate(email_input.text.toString(), password_input.text.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.login_btn -> {
                var email = email_input.text.toString()
                var password = password_input.text.toString()
                if(validate(email, password)){
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful){
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                    }
                }else{
                    showToast("Please enter email and password")
                }
            }

            R.id.sign_up_text -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }


    }

    private fun validate(email: String, password: String) = email.isNotEmpty() && password.isNotEmpty()

}