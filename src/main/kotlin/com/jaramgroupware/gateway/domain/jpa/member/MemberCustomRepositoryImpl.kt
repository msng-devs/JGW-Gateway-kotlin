package com.jaramgroupware.gateway.domain.jpa.member

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono

class MemberCustomRepositoryImpl(
    @Autowired val client: DatabaseClient,
    @Autowired val memberMapper: MemberMapper
) : MemberCustomRepository {
    override fun findMemberById(id: String): Mono<Member> {

        val query = "SELECT * FROM MEMBER AS M WHERE M.MEMBER_PK = ?"
        return client.sql(query)
            .bind(0, id)
            .map(memberMapper)
            .one()

    }
}