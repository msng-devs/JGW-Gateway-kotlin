package com.jaramgroupware.gateway.route

import com.jaramgroupware.gateway.dto.apiRoute.ApiRouteResponseDto
import com.jaramgroupware.gateway.route.filter.*
import com.jaramgroupware.gateway.service.ApiRouteService
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.factory.SetPathGatewayFilterFactory
import org.springframework.cloud.gateway.route.Route
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.*
import org.springframework.util.StringUtils
import reactor.core.publisher.Flux
import java.util.function.Function


class RouteLocatorImpl(
    @Autowired val apiRouteService: ApiRouteService,
    @Autowired val routeLocatorBuilder: RouteLocatorBuilder,
    @Autowired val setPathGatewayFilterFactory: SetPathGatewayFilterFactory,
    @Autowired val cleanRequestFilterFactory: CleanRequestFilterFactory,
    @Autowired val authenticationFilterFactory: AuthenticationFilterFactory,
    @Autowired val rbacFilterFactory: RBACFilterFactory,
    @Autowired val gatewayRefreshFilterFactory: GatewayRefreshFilterFactory,
) : RouteLocator {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun getRoutes(): Flux<Route> {
        logger.info("start setup routes.")

        val routesBuilder = routeLocatorBuilder.routes()

        return apiRouteService.findAllRoute()
            .map { route ->
                routesBuilder.route(route.id.toString())
                { predicateSpec -> setPredicateSpec(route, predicateSpec) }
            }
            .collectList()
            .flatMapMany { routesBuilder.build().routes }
    }

    /**
     * route에 옵션 및 path를 설정하는 함수.
     *
     * @param route
     * @param predicateSpec
     * @return
     */
    private fun setPredicateSpec(route: ApiRouteResponseDto, predicateSpec: PredicateSpec): Buildable<Route?> {

        logger.info("SET {} | {} | {} : {}", route.serviceName, route.routeOptionName, route.methodName, route.path)
        logger.debug("{} {}", route.routeOptionId, route.routeOptionName)
        //set route path. ex) /api/v1/member/{id} ...
        val booleanSpec = predicateSpec.path(route.path)

        //set http method. ex) GET, POST, PUT, DELETE etc.
        booleanSpec.and().method(route.methodName)


        booleanSpec.filters {
            //add clean filter.
            //기본적으로 CORS 설정상 내부 로직에 필수적인 role_pk, 및 user_pk는 무시 되지만, 혹시 모를 버그를 방지 하기위해 삭제 처리
            it.filters(cleanRequestFilterFactory.apply { config ->
                config.isEnable = true
            })

//            //add loggin filter
//            it.filters(requestFilterFactory.apply { config ->
//                config.isEnable = true
//            })

        }

        //process route options.
        when (route.routeOptionId) {

            //NO_AUTH
            1 -> {
                //nothing
                logger.debug("apply NO_AUTH")
            }

            //AUTH
            2 -> {
                logger.debug("apply AUTH")
                booleanSpec.filters {
                    it.filters(
                        authenticationFilterFactory.apply { config ->
                            config.mode = AuthenticationFilterFactory.AuthFilterMode.FULLY
                        })

                }
            }

            //ONLY_TOKEN_AUTH
            3 -> {
                logger.debug("apply ONLY_TOKEN_AUTH")
                booleanSpec.filters {
                    it.filters(
                        authenticationFilterFactory.apply { config ->
                            config.mode = AuthenticationFilterFactory.AuthFilterMode.TOKEN_ONLY
                        })

                }
            }

            //RBAC
            4 -> {
                logger.debug("apply RBAC")
                booleanSpec.filters {
                    it.filters(
                        authenticationFilterFactory.apply { config ->
                            config.mode = AuthenticationFilterFactory.AuthFilterMode.FULLY
                        })

                    it.filters(
                        rbacFilterFactory.apply { config ->
                            config.role = route.roleId
                        }
                    )
                }


            }

            //AUTH_OPTIONAL
            5 -> {
                logger.debug("apply AUTH_OPTIONAL")
                booleanSpec.filters {
                    it.filters(
                        authenticationFilterFactory.apply { config: AuthenticationFilterFactory.Config ->
                            config.mode = AuthenticationFilterFactory.AuthFilterMode.OPTIONAL
                        })
                }

            }
        }
        if (route.path == "/api/v1/refresh/**") {
            booleanSpec.filters {
                it.filters(
                    gatewayRefreshFilterFactory.apply { config: GatewayRefreshFilterFactory.Config ->
                        config.isEnable = true
                    })
            }
        }

        //set domain and return route
        return booleanSpec.uri(route.serviceDomain)
    }

//    /**
//     * path param을 적용한 path를 생성하는 함수.
//     *
//     * @param pathParam 적용할 path params. ;로 구분되어야 합니다. ex) {id};{name}
//     * @param routePath 적용할 path. ex) /api/v1/member/{id}
//     * @return
//     */
//    private fun createPathParamPath(pathParam:String,routePath:String):String{
//        val pathParams = pathParam.split(";")
//
//        var pathParamIndex = 0
//
//        val newUrl = StringBuilder()
//
//        for (path in routePath.split("/")) {
//
//            if (path != "") newUrl.append("/")
//
//            if (path.startsWith("{")) {
//                newUrl.append(pathParams[pathParamIndex])
//                pathParamIndex++
//            } else {
//                newUrl.append(path)
//            }
//
//        }
//
//        return newUrl.toString()
//    }


}