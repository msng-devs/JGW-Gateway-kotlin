package com.jaramgroupware.gateway.utlis.logging

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component

@Component
class TemplateLogger {
    companion object {
        @JvmStatic
        fun createRequestLog(request: ServerHttpRequest) : String{
            val method = request.method
            val path = request.uri.path
            val queryParams = request.queryParams
            val headers = request.headers
            val remoteAddress = request.remoteAddress?.address?.hostAddress?: "Unknown"

            return "[$method] Path=($path) RemoteAddress=($remoteAddress), QueryParams=($queryParams), Headers=($headers)"

        }
    }

}