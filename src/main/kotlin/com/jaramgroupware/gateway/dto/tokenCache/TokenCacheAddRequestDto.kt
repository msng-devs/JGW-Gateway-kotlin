package com.jaramgroupware.gateway.dto.tokenCache

import com.google.gson.Gson
import com.google.gson.JsonObject

data class TokenCacheAddRequestDto(
    val token:String,
    val uid:String,
    val roleId:Int?,
    val ttl:Long
){
    fun toJson(): String {
        val gson = Gson()
        val jsonObject = JsonObject()
        jsonObject.addProperty("uid", uid)
        jsonObject.addProperty("roleId", roleId)
        return gson.toJson(jsonObject)
    }
}