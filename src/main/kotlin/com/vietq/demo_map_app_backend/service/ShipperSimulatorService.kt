package com.vietq.demo_map_app_backend.service

import com.corundumstudio.socketio.SocketIOServer
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.vietq.demo_map_app_backend.utils.LatLngRouter
import com.vietq.demo_map_app_backend.utils.routersSimulatorDefault
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.*

@Service
class ShipperSimulatorService(
    private val socket: SocketIOServer,
    private val martService: MartService
) {

    private data class ShipperResponseDto(
        val orderId: Long,
        val lat: Double,
        val lon: Double,
        val remain: Int,
        val arrived: Boolean
    )

    private data class SimulationContext(
        val future: ScheduledFuture<*>,
        var currentLat: Double,
        var currentLon: Double,
        val targetLat: Double,
        val targetLon: Double,
        val routersSimulator: List<LatLngRouter>,
        var currentRouterIndex: Int = 0
    )

    private data class JoinRoomRequest @JsonCreator constructor(
        @JsonProperty("orderId") val orderId: Long,
        @JsonProperty("lat") val lat: Double,
        @JsonProperty("lon") val lon: Double
    )

    private companion object {
        const val SPEED_PER_TICK = 50.0 // meters
        const val ARRIVAL_THRESHOLD = 50.0 // meters
        const val UPDATE_INTERVAL = 3L // seconds
        const val EARTH_RADIUS = 6371000.0 // meters
        const val ROOM_PREFIX = "tracking_room_"
    }

    private val log = LoggerFactory.getLogger(ShipperSimulatorService::class.java)
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private val activeSimulations = ConcurrentHashMap<Long, SimulationContext>()
    private fun getRoomName(orderId: Long) = "$ROOM_PREFIX$orderId"

    @PostConstruct
    fun setupSocketEvents() {
        socket.addEventListener("join_tracking_room", JoinRoomRequest::class.java) { client, request, _ ->
            handleJoinRoom(client, request)
        }

        socket.addEventListener("leave_tracking_room", Long::class.java) { client, orderId, _ ->
            handleLeaveRoom(client, orderId)
        }

        socket.addDisconnectListener { client ->
            handleDisconnect(client)
        }

        socket.addConnectListener { client ->
            val handshake = client.handshakeData
            val url = handshake.url
            val origin = handshake.getHttpHeaders()["Origin"]
            val params = handshake.urlParams
            log.info(
                "Socket connected | sessionId={} | url={} | origin={} | params={}",
                client.sessionId, url, origin, params
            )
        }
    }

    @PreDestroy
    fun shutdown() {
        log.info("Shutting down shipper simulator service")
        activeSimulations.values.forEach { it.future.cancel(false) }
        activeSimulations.clear()

        executor.shutdown()
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
        }
    }

    private fun handleJoinRoom(client: com.corundumstudio.socketio.SocketIOClient, request: JoinRoomRequest) {
        val roomName = getRoomName(request.orderId)
        val martInfoOnOrder = martService.getMartLocationFromOrder(request.orderId)

        if (martInfoOnOrder == null) {
            log.warn("Order {} not found, client cannot join room", request.orderId)
            return
        }

        client.joinRoom(roomName)
        log.info("Client {} joined room {}", client.sessionId, roomName)

        if (activeSimulations.containsKey(request.orderId)) {
            log.info("Simulation already running for order {}", request.orderId)
            return
        }

        // hardcode
        val routersSimulator = routersSimulatorDefault

        log.info(
            "Starting simulation for order {} from ({}, {}) to ({}, {}) with {} router points",
            request.orderId, martInfoOnOrder.lat, martInfoOnOrder.lon, request.lat, request.lon, routersSimulator.size
        )

        startSimulation(
            orderId = request.orderId,
            startLat = martInfoOnOrder.lat,
            startLon = martInfoOnOrder.lon,
            targetLat = request.lat,
            targetLon = request.lon,
            routersSimulator = routersSimulator
        )
    }

    private fun handleLeaveRoom(client: com.corundumstudio.socketio.SocketIOClient, orderId: Long) {
        val roomName = getRoomName(orderId)
        client.leaveRoom(roomName)
        log.info("Client {} left room {}", client.sessionId, roomName)

        if (socket.getRoomOperations(roomName).clients.isEmpty()) {
            log.info("No more clients in room {}, stopping simulation", roomName)
            stopSimulation(orderId)
        }
    }

    private fun handleDisconnect(client: com.corundumstudio.socketio.SocketIOClient) {
        log.info("Client {} disconnected", client.sessionId)

        client.allRooms
            .filter { it.startsWith(ROOM_PREFIX) }
            .forEach { roomName ->
                val orderId = roomName.removePrefix(ROOM_PREFIX).toLongOrNull()
                orderId?.let {
                    if (socket.getRoomOperations(roomName).clients.isEmpty()) {
                        log.info("Room {} empty after disconnect, stopping simulation", roomName)
                        stopSimulation(it)
                    }
                }
            }
    }

    private fun startSimulation(
        orderId: Long,
        startLat: Double,
        startLon: Double,
        targetLat: Double,
        targetLon: Double,
        routersSimulator: List<LatLngRouter>
    ) {
        val roomName = getRoomName(orderId)

        val future = executor.scheduleAtFixedRate({
            try {
                val context = activeSimulations[orderId] ?: run {
                    log.warn("Context not found for order {}, stopping simulation", orderId)
                    stopSimulation(orderId)
                    return@scheduleAtFixedRate
                }

                if (socket.getRoomOperations(roomName).clients.isEmpty()) {
                    log.info("No clients in room {}, stopping simulation", roomName)
                    stopSimulation(orderId)
                    return@scheduleAtFixedRate
                }

                val distance = haversineMeters(context.currentLat, context.currentLon, targetLat, targetLon)

                if (distance <= ARRIVAL_THRESHOLD) {
                    handleArrival(orderId, roomName, targetLat, targetLon, distance)
                    return@scheduleAtFixedRate
                }

                updateLocation(context, targetLat, targetLon, distance, orderId, roomName)
            } catch (e: Exception) {
                log.error("Error in simulation for order {}", orderId, e)
            }
        }, 0, UPDATE_INTERVAL, TimeUnit.SECONDS)

        val context = SimulationContext(
            future = future,
            currentLat = startLat,
            currentLon = startLon,
            targetLat = targetLat,
            targetLon = targetLon,
            routersSimulator = routersSimulator,
            currentRouterIndex = 0
        )

        activeSimulations[orderId] = context
    }

    private fun handleArrival(orderId: Long, roomName: String, lat: Double, lon: Double, distance: Double) {
        log.info("Simulation completed for order {}, distance={}m", orderId, distance)

        val response = ShipperResponseDto(orderId, lat, lon, distance.toInt(), true)
        socket.getRoomOperations(roomName).sendEvent("shipper_location", response)

        stopSimulation(orderId)
    }

    private fun updateLocation(
        context: SimulationContext,
        targetLat: Double,
        targetLon: Double,
        distance: Double,
        orderId: Long,
        roomName: String
    ) {
        val routers = context.routersSimulator

        // Next index
        if (context.currentRouterIndex < routers.size - 1) {
            context.currentRouterIndex++
        }

        val newLat: Double
        val newLon: Double

        if (routers.isNotEmpty() && context.currentRouterIndex < routers.size) {
            newLat = routers[context.currentRouterIndex].lat
            newLon = routers[context.currentRouterIndex].lon
        } else {
            val (calculatedLat, calculatedLon) = calculateNextPosition(
                context.currentLat,
                context.currentLon,
                targetLat,
                targetLon
            )
            newLat = calculatedLat
            newLon = calculatedLon
        }

        context.currentLat = newLat
        context.currentLon = newLon

        val response = ShipperResponseDto(orderId, newLat, newLon, distance.toInt(), false)

        log.debug(
            "Order {} | lat={}, lon={}, remain={}m | routerIndex={}/{}",
            orderId,
            newLat,
            newLon,
            distance.toInt(),
            context.currentRouterIndex,
            routers.size
        )

        socket.getRoomOperations(roomName).sendEvent("shipper_location", response)
    }

    private fun calculateNextPosition(
        currentLat: Double,
        currentLon: Double,
        targetLat: Double,
        targetLon: Double
    ): Pair<Double, Double> {
        val distance = haversineMeters(currentLat, currentLon, targetLat, targetLon)

        if (distance <= SPEED_PER_TICK) {
            return Pair(targetLat, targetLon)
        }
        val ratio = SPEED_PER_TICK / distance

        val lat = currentLat + (targetLat - currentLat) * ratio
        val lon = currentLon + (targetLon - currentLon) * ratio

        return Pair(lat, lon)
    }

    private fun stopSimulation(orderId: Long) {
        activeSimulations.remove(orderId)?.let { context ->
            context.future.cancel(false)
            log.info("Stopped simulation for order {}", orderId)
        }
    }

    private fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS * c
    }
}