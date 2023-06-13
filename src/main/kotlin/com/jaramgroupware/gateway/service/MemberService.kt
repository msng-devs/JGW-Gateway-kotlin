package com.jaramgroupware.gateway.service

import com.jaramgroupware.gateway.domain.jpa.member.Member
import com.jaramgroupware.gateway.domain.jpa.member.MemberRepository
import com.jaramgroupware.gateway.dto.member.MemberResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MemberService(
    @Autowired val memberRepository: MemberRepository
) {
    fun findMemberById(id: String): Mono<MemberResponseDto> {
        return memberRepository.findMemberById(id)
            .flatMap{ member ->
                if(member != null){
                    Mono.just(MemberResponseDto(member))
                } else{
                    Mono.just(MemberResponseDto(null, null))
                }
            }
    }
}