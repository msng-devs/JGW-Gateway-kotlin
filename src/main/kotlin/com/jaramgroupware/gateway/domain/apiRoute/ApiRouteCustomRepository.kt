package com.jaramgroupware.gateway.domain.apiRoute

import reactor.core.publisher.Flux

interface ApiRouteCustomRepository {
    fun findAllService(): Flux<ApiRoute>
}