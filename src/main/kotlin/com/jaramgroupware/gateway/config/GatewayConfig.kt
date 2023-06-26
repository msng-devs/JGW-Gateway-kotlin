package com.jaramgroupware.gateway.config

import com.jaramgroupware.gateway.route.RouteLocatorImpl
import com.jaramgroupware.gateway.route.filter.AuthenticationFilterFactory
import com.jaramgroupware.gateway.route.filter.CleanRequestFilterFactory
import com.jaramgroupware.gateway.route.filter.GatewayRefreshFilterFactory
import com.jaramgroupware.gateway.route.filter.RBACFilterFactory
import com.jaramgroupware.gateway.service.ApiRouteService
import org.springframework.cloud.gateway.filter.factory.SetPathGatewayFilterFactory
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayConfig {
    @Bean
    fun routeLocator(
        apiRouteService: ApiRouteService,
        routeLocatorBuilder: RouteLocatorBuilder,
        setPathGatewayFilterFactory: SetPathGatewayFilterFactory,
        cleanRequestFilterFactory: CleanRequestFilterFactory,
        authenticationFilterFactory: AuthenticationFilterFactory,
        rbacFilterFactory: RBACFilterFactory,
        gatewayRefreshFilterFactory: GatewayRefreshFilterFactory
    ): RouteLocator {
        return RouteLocatorImpl(
            apiRouteService = apiRouteService,
            routeLocatorBuilder = routeLocatorBuilder,
            setPathGatewayFilterFactory = setPathGatewayFilterFactory,
            cleanRequestFilterFactory = cleanRequestFilterFactory,
            requestFilterFactory = cleanRequestFilterFactory,
            authenticationFilterFactory = authenticationFilterFactory,
            rbacFilterFactory = rbacFilterFactory,
            gatewayRefreshFilterFactory = gatewayRefreshFilterFactory
        )
    }

}