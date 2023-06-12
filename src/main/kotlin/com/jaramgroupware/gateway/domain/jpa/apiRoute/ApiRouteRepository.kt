package com.jaramgroupware.gateway.domain.jpa.apiRoute

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ApiRouteRepository : ReactiveCrudRepository<ApiRoute?, Int?>, ApiRouteCustomRepository