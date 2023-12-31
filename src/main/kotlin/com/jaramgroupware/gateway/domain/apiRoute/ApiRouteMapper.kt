package com.jaramgroupware.gateway.domain.apiRoute

import com.jaramgroupware.gateway.domain.method.Method
import com.jaramgroupware.gateway.domain.role.Role
import com.jaramgroupware.gateway.domain.routeOption.RouteOption
import com.jaramgroupware.gateway.domain.serviceInfo.ServiceInfo
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import org.springframework.stereotype.Component
import java.util.function.BiFunction

@Component
class ApiRouteMapper : BiFunction<Row, RowMetadata, ApiRoute> {
    override fun apply(row: Row, rowMetadata: RowMetadata): ApiRoute {
        return ApiRoute(
            id = row.get("API_ROUTE_PK", Int::class.java)!!,
            path = row.get("API_ROUTE_PATH", String::class.java)!!,
            priority = row.get("PRIORITY", Int::class.java)!!,
            role = if (row.get("ROLE_PK") != null)
                Role(
                    id = row.get("ROLE_PK", Int::class.java)!!,
                    name = row.get("ROLE_NM", String::class.java)!!
                ) else null,
            service = ServiceInfo(
                id = row.get("SERVICE_PK", Int::class.java)!!,
                name = row.get("SERVICE_NM", String::class.java)!!,
                domain = row.get("SERVICE_DOMAIN", String::class.java)!!,
                index = row.get("SERVICE_INDEX", String::class.java)
            ),
            method = Method(
                id = row.get("METHOD_PK", Int::class.java)!!,
                name = row.get("METHOD_NM", String::class.java)!!
            ),
            routeOption = RouteOption(
                id = row.get("ROUTE_OPTION_PK", Int::class.java)!!,
                name = row.get("ROUTE_OPTION_NM", String::class.java)!!
            )
        )
    }
}