package com.jaramgroupware.gateway.domain.jpa.routeOption

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "ROUTE_OPTION")
data class RouteOption (
    @Id
    @Column("ROUTE_OPTION_PK")
    val id: Int,

    @Column("ROUTE_OPTION_NM")
    val name: String
)