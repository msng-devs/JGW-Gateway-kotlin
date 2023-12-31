package com.jaramgroupware.gateway.domain.member

import reactor.core.publisher.Mono

interface MemberCustomRepository {
    fun findMemberById(id: String): Mono<Member>
}