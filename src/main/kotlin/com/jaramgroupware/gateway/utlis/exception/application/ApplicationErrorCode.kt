package com.jaramgroupware.gateway.utlis.exception.application

enum class ApplicationErrorCode (val httpCode:Int ,val errorCode:String,val title:String){
    UNKNOWN_ERROR(500,"GW_APP_001","Unknown Error"),
    FIREBASE_ERROR(500,"GW_APP_002","Firebase Error"),
    EMAIL_NOT_VERIFIED(403,"GW_APP_003","Email Not Verified"),
}