package com.jaramgroupware.gateway.utlis.exception.authentication

class AuthenticationException(
    message:String,
    errorCode: AuthenticationErrorCode
)
    : Exception(message) {
}