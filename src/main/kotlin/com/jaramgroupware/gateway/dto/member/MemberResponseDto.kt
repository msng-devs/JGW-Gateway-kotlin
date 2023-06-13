package com.jaramgroupware.gateway.dto.member

import com.jaramgroupware.gateway.domain.jpa.member.Member

data class MemberResponseDto(
    var id: String?,
    var roleId: Int?
){
    constructor(member: Member) : this(
        id = member.id?: null,
        roleId = member.role?:null
    )

    fun isNull(): Boolean{
        return id == null && roleId == null
    }
}