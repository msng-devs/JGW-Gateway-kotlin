package com.jaramgroupware.gateway.domain.jpa.apiRoute

import com.jaramgroupware.gateway.domain.jpa.method.Method
import com.jaramgroupware.gateway.domain.jpa.role.Role
import com.jaramgroupware.gateway.domain.jpa.routeOption.RouteOption
import com.jaramgroupware.gateway.domain.jpa.serviceInfo.ServiceInfo
import lombok.*
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table


@Table(name = "API_ROUTE")
data class ApiRoute(
    @Id
    @Column("API_ROUTE_PK")
    val id: Int,

    @Column("API_ROUTE_PATH")
    val path: String,

    @Column("PATH_VARIABLE")
    val pathVariable: String,

    @Transient
    val role: Role,

    @Transient
    val service: ServiceInfo,

    @Transient
    val method: Method,

    @Transient
    val routeOption: RouteOption
)

