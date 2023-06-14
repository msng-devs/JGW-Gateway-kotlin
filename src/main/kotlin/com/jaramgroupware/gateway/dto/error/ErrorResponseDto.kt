package com.jaramgroupware.gateway.dto.error

import com.jaramgroupware.gateway.utlis.exception.application.ApplicationErrorCode
import com.jaramgroupware.gateway.utlis.exception.application.ApplicationException
import com.jaramgroupware.gateway.utlis.exception.authentication.AuthenticationException
import org.springframework.http.HttpStatus
import java.time.LocalDateTime


data class ErrorResponseDto(
    var timestamp: LocalDateTime,
    var status: HttpStatus,
    var error: String,
    var message: String,
    var errorCode: String,
    var path: String
){
    constructor(authenticationException: AuthenticationException, path: String) : this(
        LocalDateTime.now(),
        HttpStatus.valueOf(authenticationException.errorCode.httpCode),
        authenticationException.errorCode.title,
        authenticationException.message ?: "상세 메시지가 없습니다.",
        authenticationException.errorCode.errorCode,
        path
    )

    constructor(applicationException: ApplicationException, path: String) : this(
        LocalDateTime.now(),
        HttpStatus.valueOf(applicationException.errorCode.httpCode),
        applicationException.errorCode.title,
        applicationException.message ?: "상세 메시지가 없습니다.",
        applicationException.errorCode.errorCode,
        path
    )
}