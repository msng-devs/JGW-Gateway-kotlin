package com.jaramgroupware.gateway.domain.jpa.member

import reactor.core.publisher.Mono

interface MemberCustomRepository {
    fun findMemberById(id: String): Mono<Member>
}