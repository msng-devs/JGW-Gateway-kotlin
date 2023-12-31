package com.jaramgroupware.gateway.domain.member

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : ReactiveCrudRepository<Member, Int>, MemberCustomRepository