package com.jaramgroupware.gateway.domain.jpa.apiRoute

import reactor.core.publisher.Flux

interface ApiRouteCustomRepository {
    fun findAllService(): Flux<ApiRoute>
}