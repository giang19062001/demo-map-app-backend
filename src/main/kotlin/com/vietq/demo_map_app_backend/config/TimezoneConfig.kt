package com.vietq.demo_map_app_backend.config
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.config")
class TimezoneConfig {
    lateinit var timezone: String
}
