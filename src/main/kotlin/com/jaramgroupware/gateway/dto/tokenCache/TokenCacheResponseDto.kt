package com.jaramgroupware.gateway.dto.tokenCache

import com.google.gson.Gson
import com.google.gson.JsonObject

data class TokenCacheResponseDto (
    var uid:String,
    var roleId:Int?,
    var ttl:Long?
) {
    constructor(json: String,ttl: Long?) : this("", null,null) {
        val gson = Gson()
        val jsonObject = gson.fromJson(json, TokenCacheResponseDto::class.java)
        this.uid = jsonObject.uid
        this.roleId = jsonObject.roleId
        this.ttl = ttl
    }

    fun toJson(): String {
        val gson = Gson()
        val jsonObject = JsonObject()
        jsonObject.addProperty("uid", uid)
        jsonObject.addProperty("roleId", roleId)
        return gson.toJson(jsonObject)
    }

    fun isNull(): Boolean {
        return uid == "" && roleId == null
    }

    fun getCacheType(): CacheType {
        return when {
            isNull() -> CacheType.NOT_VALID
            uid != "" && roleId == null -> CacheType.ONLY_TOKEN
            else -> CacheType.FULLY
        }
    }

    enum class CacheType {
        ONLY_TOKEN, FULLY, NOT_VALID
    }
}