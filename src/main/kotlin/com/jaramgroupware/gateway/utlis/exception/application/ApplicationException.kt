package com.jaramgroupware.gateway.utlis.exception.application

class ApplicationException(message:String,
                           errorCode: ApplicationErrorCode
) : Exception(message)  {
}