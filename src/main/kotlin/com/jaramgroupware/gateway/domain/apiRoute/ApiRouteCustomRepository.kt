package com.jaramgroupware.gateway.domain.apiRoute

import reactor.core.publisher.Flux

interface ApiRouteCustomRepository {
    fun findAllService(): Flux<ApiRoute>

    fun findServiceById(id: Int): Flux<ApiRoute>
    fun findAllOrderServiceAndPriority(): Flux<ApiRoute>
}