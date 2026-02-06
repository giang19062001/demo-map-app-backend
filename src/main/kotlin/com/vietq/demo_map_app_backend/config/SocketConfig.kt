package com.vietq.demo_map_app_backend.config

import com.corundumstudio.socketio.SocketIOServer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SocketConfig() {

    @Bean
    fun socketIOServer(): SocketIOServer {
        val config = com.corundumstudio.socketio.Configuration().apply {
            hostname = "0.0.0.0"
            this.port = 8503
            context="/socket"
        }
        return SocketIOServer(config)
    }
}

