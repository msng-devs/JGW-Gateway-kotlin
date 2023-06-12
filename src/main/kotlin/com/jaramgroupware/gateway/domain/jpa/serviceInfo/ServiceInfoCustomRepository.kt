package com.jaramgroupware.gateway.domain.jpa.serviceInfo

import reactor.core.publisher.Mono

interface ServiceInfoCustomRepository {
    fun findServiceById(id: Int): Mono<ServiceInfo>
}