package com.jaramgroupware.gateway.domain.member

import jakarta.validation.constraints.Email
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "MEMBER")
data class Member(
    @Id
    @Column("MEMBER_PK")
    val id: String,

    @Column("MEMBER_EMAIL")
    val email: String,

    @Column("MEMBER_NM")
    val name: String,

    @Column("ROLE_ROLE_PK")
    val role: Int,

    @Column("MEMBER_STATUS")
    val isActive:Boolean
)