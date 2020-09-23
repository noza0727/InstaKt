package com.example.instagram

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.instagram.views.PasswordDialog
import com.google.firebase.auth.*
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.email_input
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class EditProfileActivity: AppCompatActivity(), PasswordDialog.Listener {
    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mStorage: StorageReference
    private lateinit var mImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        Log.d(TAG, "onCreate")

        close_image.setOnClickListener{ finish() }
        save_image.setOnClickListener{ updateProfile() }
        change_photo_text.setOnClickListener{takeCameraPic()}
        profile_image_edit.setOnClickListener{takeCameraPic()}

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mStorage = FirebaseStorage.getInstance().reference

        mDatabase.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addListenerForSingleValueEvent( ValueEventListenerAdapter{
                mUser = it.getValue(User::class.java)!!
                name_input.setText(mUser!!.name, TextView.BufferType.EDITABLE)
                username_input.setText(mUser!!.username, TextView.BufferType.EDITABLE)
                website_input.setText(mUser!!.website, TextView.BufferType.EDITABLE)
                email_input.setText(mUser!!.email, TextView.BufferType.EDITABLE)
                phone_input.setText(mUser!!.phone.toString(), TextView.BufferType.EDITABLE)
                bio_input.setText(mUser!!.bio, TextView.BufferType.EDITABLE)

        })
    }


    private fun updateProfile(){
       mPendingUser = readInputs()

        val error = validate(mPendingUser)
        if(error == null){ //no errors
            //save details:
            if(mPendingUser.email == mUser.email){
                updateUser(mPendingUser)
            }else{
                PasswordDialog().show(supportFragmentManager, "password_dialog")
            }
        }else {
            showToast(error)
        }
    }

    val REQUEST_IMAGE_CAPTURE = 1

    private fun takeCameraPic(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val imageFile = createImageFile()
                mImageUri = FileProvider.getUriForFile(
                    this, "com.example.instagram.fileprovider", imageFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    lateinit var currentPhotoPath: String
    private val simpleDataFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    @Throws(IOException::class)
    private fun createImageFile(): File{
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${simpleDataFormat}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val uid = mAuth.currentUser!!.uid
            mStorage.child("users/$uid/photo").putFile(mImageUri).addOnCompleteListener {
                if (it.isSuccessful) {
                    val ref = mStorage.child("users/$uid/photo")
                    ref.putFile(mImageUri).addOnCompleteListener { put ->
                        if (put.isSuccessful) {
                            ref.downloadUrl.addOnCompleteListener { download ->
                                if (download.isSuccessful) {
                                    val photoUrl = it.result.toString()
                                    mDatabase.child("users/$uid/photo").setValue(photoUrl)
                                } else showToast(it.exception!!.message!!)
                            }
                        } else showToast(it.exception!!.message!!)
                    }
                }
            }
        }
    }

    private fun readInputs(): User{
        val phoneStr = phone_input.text.toString()
        return User(
            name = name_input.text.toString(),
            username = username_input.text.toString(),
            website = website_input.text.toString(),
            email = email_input.text.toString(),
            phone = if(phoneStr == "") 0 else phoneStr.toLong(),
            bio = bio_input.text.toString()
        )
    }

    private fun validate(user: User): String? = when{
            user.name.isEmpty() -> "Please enter name"
            user.username.isEmpty() -> "Please enter username"
            user.email.isEmpty() -> "Please enter email"
            else -> null
        }

    private fun updateUser(user : User){
        val updatesMap = mutableMapOf<String, Any>()
        if(user.name != mUser.name) updatesMap["name"] = user.name
        if(user.username != mUser.username) updatesMap["username"] = user.username
        if(user.website != mUser.website) updatesMap["website"] = user.website
        if(user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if(user.email != mUser.email) updatesMap["email"] = user.email
        if(user.phone != mUser.phone) updatesMap["phone"] = user.phone

        mDatabase.updateUser(mAuth.currentUser!!.uid, updatesMap){
            showToast("Profile saved")
            finish()
        }
    }

    private fun DatabaseReference.updateUser(uid: String, updates: Map<String, Any>,
                                             onSuccess: () -> Unit){
        child("users").child(uid).updateChildren(updates)
            .addOnCompleteListener{
                if(it.isSuccessful){
                   onSuccess()
                }else{
                    showToast(it.exception!!.message!!)
                }
            }
    }

    override fun onPasswordConfirm(password: String) {
        if(password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mAuth.currentUser!!.reauthenticate(credential) {
                mAuth.currentUser!!.updateEmail(mPendingUser.email) {
                    updateUser(mPendingUser)
                }
            }
        }
        else showToast("You should enter password")
    }

    private fun FirebaseUser.reauthenticate(credential: AuthCredential, onSuccess: () -> Unit){
        this.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else showToast(it.exception!!.message!!)
        }
    }

    private fun FirebaseUser.updateEmail(email: String, onSuccess: () -> Unit){
        updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else showToast(it.exception!!.message!!)
        }
    }
}