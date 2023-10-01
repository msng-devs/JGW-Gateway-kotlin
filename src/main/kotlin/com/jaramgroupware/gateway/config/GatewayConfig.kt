package com.jaramgroupware.gateway.config

import com.jaramgroupware.gateway.route.RouteLocatorImpl
import com.jaramgroupware.gateway.route.filter.*
import com.jaramgroupware.gateway.service.ApiRouteService
import org.springframework.cloud.gateway.filter.factory.SetPathGatewayFilterFactory
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Gateway 설정을 위한 클래스
 */
@Configuration
class GatewayConfig {

    /**
     * 사용자 정의 RouteLocator를 생성하는 메소드
     */
    @Bean
    fun routeLocator(
        apiRouteService: ApiRouteService,
        routeLocatorBuilder: RouteLocatorBuilder,
        setPathGatewayFilterFactory: SetPathGatewayFilterFactory,
        cleanRequestFilterFactory: CleanRequestFilterFactory,
        requestFilterFactory: RequestLoggingFilterFactory,
        authenticationFilterFactory: AuthenticationFilterFactory,
        rbacFilterFactory: RBACFilterFactory,
        gatewayRefreshFilterFactory: GatewayRefreshFilterFactory
    ): RouteLocator {
        return RouteLocatorImpl(
            apiRouteService = apiRouteService,
            routeLocatorBuilder = routeLocatorBuilder,
            setPathGatewayFilterFactory = setPathGatewayFilterFactory,
            cleanRequestFilterFactory = cleanRequestFilterFactory,
            requestFilterFactory = requestFilterFactory,
            authenticationFilterFactory = authenticationFilterFactory,
            rbacFilterFactory = rbacFilterFactory,
            gatewayRefreshFilterFactory = gatewayRefreshFilterFactory
        )
    }

}