package com.example.instagram

data class User (val name: String = "", val username: String = "", val email: String = "",
            val website: String? = null, val phone: Long? = null,
            val bio: String? = null, val photo: String? = null) {
}