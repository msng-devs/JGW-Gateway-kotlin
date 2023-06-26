package com.jaramgroupware.gateway.dto.member

import com.jaramgroupware.gateway.domain.member.Member

data class MemberResponseDto(
    var id: String?,
    var roleId: Int?,
    var isActive: Boolean?
) {
    constructor(member: Member) : this(
        id = member.id,
        roleId = member.role,
        isActive = member.isActive
    )

    fun isNull(): Boolean {
        return id == null && roleId == null
    }
}