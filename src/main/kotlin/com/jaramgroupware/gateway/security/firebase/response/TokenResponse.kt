package com.jaramgroupware.gateway.security.firebase.response

import java.time.LocalDateTime

data class TokenResponse(
    val uid: String?,
    val exp: LocalDateTime?,
    val email: String?

){
    fun isNull(): Boolean {
        return uid == null && exp == null
    }
}