package com.jaramgroupware.gateway.domain.jpa.method

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "METHOD")
data class Method(
    @Id
    @Column("METHOD_PK")
    val id: Int,

    @Column("METHOD_NM")
    val name: String

)