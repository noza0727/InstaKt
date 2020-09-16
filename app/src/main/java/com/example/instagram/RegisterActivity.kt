package com.example.instagram

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_email.email_input
import kotlinx.android.synthetic.main.fragment_register_namepass.*

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener{
    private val TAG = "RegisterActivity"
    private var mEmail: String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, EmailFragment())
                .commit()
        }
    }

    override fun onNext(email: String) {
        if(email.isNotEmpty()){
            mEmail = email
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, NamePassFragment())
                .addToBackStack(null).commit()
        }
        else {
            showToast("Please enter email")
        }
    }

    override fun onRegister(name: String, password: String) {
        if(name.isNotEmpty() && password.isNotEmpty()){
            val email = mEmail
            if(email != null){
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val user = makeUser(name, email)
                        val reference = mDatabase.child("users").child(it.result!!.user!!.uid)
                        reference.setValue((user))
                            .addOnCompleteListener{ profile ->
                                if(profile.isSuccessful){
                                    startHomeActivity()
                                }else{
                                    Log.e(TAG, "onRegister: failed to create a profile", profile.exception)
                                    showToast("Something went wrong while creating a profile. Please try again.")
                                }
                        }
                    }else{
                        Log.e(TAG, "onRegister: failed to create a user", it.exception)
                        showToast("Something went wrong. Please try again.")
                    }
                }
            }else{
                Log.e(TAG, "onRegister: email is null" )
                showToast("Please enter email")
                supportFragmentManager.popBackStack()
            }
        }else{
            showToast("Please enter full name and password")
        }
    }

    private fun startHomeActivity(){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun makeUsername(fullname: String) = fullname.toLowerCase().replace(" ", ".")

    private fun makeUser(name: String, email: String): User{
        val username = makeUsername(name)
        return User(name,username,email)
    }
}

class EmailFragment : Fragment() {
    private lateinit var mListener: Listener
    interface Listener{
        fun onNext(email: String){

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        next_button.setOnClickListener{
            val email = email_input.text.toString()
            mListener.onNext(email)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }
}


class NamePassFragment : Fragment() {
    private lateinit var mListener: Listener
    interface Listener{
        fun onRegister(name: String, password: String){

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_namepass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       register_button.setOnClickListener{
            val name = name_input.text.toString()
            val password = password_input.text.toString()
            mListener.onRegister(name, password)
        }
    }

override fun onAttach(context: Context) {
    super.onAttach(context)
    mListener = context as Listener
}
}