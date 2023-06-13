package com.jaramgroupware.gateway.dto.apiRoute

import com.jaramgroupware.gateway.domain.jpa.apiRoute.ApiRoute


data class ApiRouteResponseDto(
    val id: Int,
    val path: String,
    val pathVariable: String,
    val roleId: Int,
    val serviceName: String,
    val serviceDomain: String,
    val methodName: String,
    val routeOptionId: Int,
    val routeOptionName: String
){
    constructor(apiRoute: ApiRoute):this(
        id = apiRoute.id,
        path = apiRoute.path,
        pathVariable = apiRoute.pathVariable ?: "",
        roleId = apiRoute.role.id,
        serviceName = apiRoute.service.name,
        serviceDomain = apiRoute.service.domain,
        methodName = apiRoute.method.name,
        routeOptionId = apiRoute.routeOption.id,
        routeOptionName = apiRoute.routeOption.name
    )
}