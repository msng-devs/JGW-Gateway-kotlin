package com.jaramgroupware.gateway.utlis.exception.application

class ApplicationException(message:String,
                           val errorCode: ApplicationErrorCode
) : Exception(message)  {
}