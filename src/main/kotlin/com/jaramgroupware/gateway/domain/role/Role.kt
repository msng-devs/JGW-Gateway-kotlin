package com.jaramgroupware.gateway.domain.role

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "ROLE")
data class Role(
    @Id
    @Column("ROLE_PK")
    val id: Int,

    @Column("ROLE_NM")
    val name: String
)