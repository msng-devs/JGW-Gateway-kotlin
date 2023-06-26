package com.jaramgroupware.gateway.domain.apiRoute

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux

class ApiRouteCustomRepositoryImpl(
    @Autowired val client: DatabaseClient,
    @Autowired val apiRouteMapper: ApiRouteMapper
) : ApiRouteCustomRepository {

    override fun findAllService(): Flux<ApiRoute> {
        val query = """
               SELECT * FROM API_ROUTE
               LEFT JOIN METHOD
               ON METHOD.METHOD_PK = API_ROUTE.METHOD_METHOD_PK
               LEFT JOIN ROLE
               ON ROLE.ROLE_PK = API_ROUTE.ROLE_ROLE_PK
               LEFT JOIN SERVICE
               ON SERVICE.SERVICE_PK = API_ROUTE.SERVICE_SERVICE_PK
               LEFT JOIN ROUTE_OPTION
               ON ROUTE_OPTION.ROUTE_OPTION_PK = API_ROUTE.ROUTE_OPTION_ROUTE_OPTION_PK
               
               """.trimIndent()
        return client.sql(query)
            .map(apiRouteMapper)
            .all()
    }
}