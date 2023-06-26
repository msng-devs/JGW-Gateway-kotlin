package com.jaramgroupware.gateway.service

import com.jaramgroupware.gateway.domain.apiRoute.ApiRouteRepository
import com.jaramgroupware.gateway.dto.apiRoute.ApiRouteResponseDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux

@Service
class ApiRouteService(
    val routeRepository: ApiRouteRepository
) {
    @Transactional(readOnly = true)
    fun findAllRoute(): Flux<ApiRouteResponseDto> {
        return routeRepository.findAllService().map { route -> ApiRouteResponseDto(route) }
    }
}
