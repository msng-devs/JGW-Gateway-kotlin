package com.jaramgroupware.gateway.service

import com.jaramgroupware.gateway.dto.tokenCache.TokenCacheAddRequestDto
import com.jaramgroupware.gateway.dto.tokenCache.TokenCacheResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class TokenCacheService(
    @Autowired val redisTemplate: RedisTemplate<String, String>
) {

    fun getCache(token: String): TokenCacheResponseDto {
        return redisTemplate.opsForValue()
            .get(token)
            ?.let {
                val ttl = redisTemplate.getExpire(token, TimeUnit.MILLISECONDS)
                TokenCacheResponseDto(it,ttl)
            }
            ?: TokenCacheResponseDto("", null,null)
    }

    fun addCache(dto: TokenCacheAddRequestDto) {
        val cacheValue = dto.toJson()
        redisTemplate.opsForValue().set(dto.token, cacheValue)
        redisTemplate.expire(dto.token, dto.ttl, TimeUnit.MILLISECONDS)
    }

    fun deleteCache(token: String) {
        redisTemplate.delete(token)
    }

}