package com.jaramgroupware.gateway.domain.jpa.serviceInfo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class ServiceInfoCustomRepositoryImpl(
    @Autowired val client: DatabaseClient,
    @Autowired val serviceInfoMapper: ServiceInfoMapper
) : ServiceInfoCustomRepository {

    override fun findServiceById(id: Int): Mono<ServiceInfo> {
        val query = "SELECT * FROM SERVICE WHERE SERVICE_PK = ?"
        return client.sql(query)
            .bind(0, id)
            .map(serviceInfoMapper)
            .one()
    }
}