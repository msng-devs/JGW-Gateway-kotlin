package com.jaramgroupware.gateway.domain.jpa.member

import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import org.springframework.stereotype.Component
import java.util.function.BiFunction

@Component
class MemberMapper : BiFunction<Row, RowMetadata, Member> {
    override fun apply(row: Row, rowMetadata: RowMetadata): Member {
        return Member(
            id = row.get("MEMBER_PK", String::class.java)!!,
            email = row.get("MEMBER_EMAIL", String::class.java)!!,
            name = row.get("MEMBER_NM", String::class.java)!!,
            role = row.get("ROLE_ROLE_PK", Int::class.java)!!,
            isActive = row.get("MEMBER_STATUS", Boolean::class.java)!!
        )
    }
}