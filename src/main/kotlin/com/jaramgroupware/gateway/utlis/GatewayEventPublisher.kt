package com.jaramgroupware.gateway.utlis

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.event.RefreshRoutesEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class GatewayEventPublisher(
    @Autowired val applicationEventPublisher: ApplicationEventPublisher
) {
    fun refreshRoute() {
        applicationEventPublisher.publishEvent(RefreshRoutesEvent(this))
    }
}