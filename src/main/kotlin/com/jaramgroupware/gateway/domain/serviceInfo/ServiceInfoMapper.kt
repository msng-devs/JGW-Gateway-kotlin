package com.jaramgroupware.gateway.domain.serviceInfo

import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import org.springframework.stereotype.Component
import java.util.function.BiFunction

@Component
class ServiceInfoMapper : BiFunction<Row, RowMetadata, ServiceInfo> {
    override fun apply(row: Row, rowMetadata: RowMetadata): ServiceInfo {
        return ServiceInfo(
            id = row.get("SERVICE_PK", Int::class.java)!!,
            name = row.get("SERVICE_NM", String::class.java)!!,
            domain = row.get("SERVICE_DOMAIN", String::class.java)!!,
            index = row.get("SERVICE_INDEX", String::class.java)!!
        )
    }
}