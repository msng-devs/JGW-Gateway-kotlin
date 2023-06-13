package com.jaramgroupware.gateway.route.filter

import com.jaramgroupware.gateway.utlis.logging.TemplateLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
import org.springframework.stereotype.Component

@Component
class RequestLoggingFilterFactory: GatewayFilterFactory<RequestLoggingFilterFactory.Config>{

    private val logger = LoggerFactory.getLogger(RequestLoggingFilterFactory::class.java)

    data class Config(
        var isEnable: Boolean? = null
    )

    override fun apply(config: Config): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            val request = exchange.request
            val requestLog = TemplateLogger.createRequestLog(request)
            logger.info("Received request -$requestLog")

            chain.filter(exchange)
        }
    }
}