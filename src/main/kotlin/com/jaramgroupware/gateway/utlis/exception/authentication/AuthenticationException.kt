package com.jaramgroupware.gateway.utlis.exception.authentication

class AuthenticationException(
    message:String,
    val errorCode: AuthenticationErrorCode
)
    : Exception(message) {

}