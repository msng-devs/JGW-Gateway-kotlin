package com.jaramgroupware.gateway.utlis.exception.authentication

import org.springframework.http.HttpStatusCode

enum class AuthenticationErrorCode (val httpCode:Int ,val errorCode:String,val title:String) {
    TOKEN_NOT_FOUND(400 ,"GW_AUTH_001","Token Not Found"),
    INVALID_TOKEN(403,"GW_AUTH_002","Invalid Token"),

}