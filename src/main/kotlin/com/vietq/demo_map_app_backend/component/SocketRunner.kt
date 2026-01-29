package com.vietq.demo_map_app_backend.component
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
class SocketRunner(private val server: SocketIOServer) {
    // method will run after the bean is initialized
     @PostConstruct fun start() = server.start()

    // method will run before the application shuts down
    @PreDestroy fun stop() = server.stop()
}
