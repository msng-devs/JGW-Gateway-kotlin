package com.jaramgroupware.gateway.route

import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.cloud.gateway.filter.factory.SetPathGatewayFilterFactory
import org.springframework.cloud.gateway.route.Route
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.*
import org.springframework.util.StringUtils
import reactor.core.publisher.Flux
import java.util.function.Function

@Slf4j
class RouteLocatorImpl : RouteLocator {
    private val apiRouteService: ApiRouteService? = null
    private val routeLocatorBuilder: RouteLocatorBuilder? = null
    private val authorizationFilterFactory: AuthorizationFilterFactory? = null
    private val authenticationFilterFactory: AuthenticationFilterFactory? = null
    private val gatewayRefreshFactory: GatewayRefreshFactory? = null
    private val cleanRequestFilterFactory: CleanRequestFilterFactory? = null
    private val setPathGatewayFilterFactory: SetPathGatewayFilterFactory? = null
    private val requestLoggingFilterFactory: RequestLoggingFilterFactory? = null
    private val responseLoggingFilterFactory: ResponseLoggingFilterFactory? = null

    override fun getRoutes(): Flux<Route> {
        val routesBuilder = routeLocatorBuilder!!.routes()
        return apiRouteService.findAllRoute()
            .map { route ->
                routesBuilder.route(
                    java.lang.String.valueOf(route.getId())
                ) { predicateSpec: PredicateSpec ->
                    setPredicateSpec(
                        route,
                        predicateSpec
                    )
                }
            }
            .collectList()
            .flatMapMany { builders ->
                routesBuilder.build()
                    .routes
            }
    }

    private fun setPredicateSpec(route: ApiRoute, predicateSpec: PredicateSpec): Buildable<Route?> {
        log.info(
            "SERVICE = {} [{}][{}] | {}",
            route.getService().getName(),
            route.getRouteOption().getName(),
            route.getMethod().getName(),
            route.getPath()
        )
        val booleanSpec: BooleanSpec = predicateSpec.path(route.getPath())
        booleanSpec.filters(Function { f: GatewayFilterSpec ->
            f.filters(requestLoggingFilterFactory.apply { config ->
                config.setEnable(
                    true
                )
            })
        })
        booleanSpec.filters(Function { f: GatewayFilterSpec ->
            f.filters(cleanRequestFilterFactory.apply { config ->
                config.setIsEnable(
                    true
                )
            })
        })
        if (!StringUtils.isEmpty(route.getMethod().getName())) {
            booleanSpec.and()
                .method(route.getMethod().getName())
        }
        when (route.getRouteOption().getName()) {
            "AUTH" -> booleanSpec.filters(Function { f: GatewayFilterSpec ->
                f.filters(authenticationFilterFactory.apply { config ->
                    config.setOnlyToken(false)
                    config.setOptional(false)
                })
            })

            "ONLY_TOKEN_AUTH" -> booleanSpec.filters(Function { f: GatewayFilterSpec ->
                f.filters(authenticationFilterFactory.apply { config ->
                    config.setOnlyToken(true)
                    config.setOptional(false)
                })
            })

            "RBAC" -> {
                booleanSpec.filters(Function { f: GatewayFilterSpec ->
                    f.filters(authenticationFilterFactory.apply { config ->
                        config.setOnlyToken(false)
                        config.setOptional(false)
                    })
                })
                if (!StringUtils.isEmpty(route.getRole().getName())) {
                    booleanSpec.filters(Function { f: GatewayFilterSpec ->
                        f.filters(authorizationFilterFactory.apply { config ->
                            config.setRole(
                                route.getRole().getId()
                            )
                        })
                    })
                }
            }

            "AUTH_OPTIONAL" -> booleanSpec.filters(Function { f: GatewayFilterSpec ->
                f.filters(authenticationFilterFactory.apply { config ->
                    config.setOnlyToken(false)
                    config.setOptional(true)
                })
            })

            "NO_AUTH" -> {}
            else -> {}
        }
        if (route.getPathVariable() != null) {
            val routePaths: Array<String> = route.getPathVariable().split(";")
            var routePathIndex = 0
            val newUrl = StringBuilder()
            for (path in route.getPath().split("/")) {
                newUrl.append("/")
                if (path.startsWith("{")) {
                    newUrl.append(routePaths[routePathIndex])
                    routePathIndex++
                } else {
                    newUrl.append(path)
                }
            }
            newUrl.append("/**")
            booleanSpec.filters { f: GatewayFilterSpec ->
                f.filters(
                    setPathGatewayFilterFactory!!.apply { config: SetPathGatewayFilterFactory.Config ->
                        config.template = newUrl.toString()
                    })
            }
        }
        //set domain and return route
        return booleanSpec.uri(route.getService().getDomain())
    }
}