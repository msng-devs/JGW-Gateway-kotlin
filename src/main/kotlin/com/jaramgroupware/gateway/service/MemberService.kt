package com.jaramgroupware.gateway.service

import com.jaramgroupware.gateway.domain.jpa.member.Member
import com.jaramgroupware.gateway.domain.jpa.member.MemberRepository
import com.jaramgroupware.gateway.dto.member.MemberResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class MemberService(
    @Autowired val memberRepository: MemberRepository
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)
    fun findMemberById(id: String): Mono<MemberResponseDto> {

        return memberRepository.findMemberById(id)
            .map { member -> MemberResponseDto(member) }
            .switchIfEmpty{ Mono.just(MemberResponseDto(null, null,null)) }
    }
}