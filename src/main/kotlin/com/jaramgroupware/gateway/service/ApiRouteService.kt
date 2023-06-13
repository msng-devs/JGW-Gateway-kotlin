package com.jaramgroupware.gateway.service

import com.jaramgroupware.gateway.domain.jpa.apiRoute.ApiRoute
import com.jaramgroupware.gateway.domain.jpa.apiRoute.ApiRouteRepository
import com.jaramgroupware.gateway.dto.apiRoute.ApiRouteResponseDto
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Function

@Service
class ApiRouteService(
    val routeRepository: ApiRouteRepository
) {
    @Transactional(readOnly = true)
    fun findAllRoute(): Flux<ApiRouteResponseDto> {
        return routeRepository.findAllService().map { route -> ApiRouteResponseDto(route) };
    }
}
