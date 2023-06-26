package com.jaramgroupware.gateway.domain.serviceInfo

import reactor.core.publisher.Mono

interface ServiceInfoCustomRepository {
    fun findServiceById(id: Int): Mono<ServiceInfo>
}