package alt.v.kotlin

import alt.v.jvm.*
import alt.v.kotlin.events.PlayerConnectEvent
import jnr.ffi.Pointer
import jnr.ffi.Struct

val server: jnr.ffi.Pointer = CAPI.server;

fun main()
{
    Log.info("[Kotlin-JVM] Kotlin-JVM plugin loaded")

    val scriptRuntime = CAPI.func.alt_script_runtime_create(
            { info -> CreateResource(info) },
            { resource -> DeleteResource(resource) },
            { OnTick() }
    )
    CAPI.func.alt_server_register_script_runtime(server, "kotlin-jvm", scriptRuntime)

    CAPI.func.alt_server_subscribe_event(server, CAPI.alt_event_type_t.PLAYER_CONNECT) { event -> OnConnect(event) }
}

fun OnConnect(eptr: Pointer): Boolean {

    val event = PlayerConnectEvent(eptr)
    val player = event.player

    Log.info("[KOTLIN] PLAYER CONNECTED")
    Log.info("[KOTLIN] Player ${player.name} connected")

    return true
}

// resources
fun CreateResource(info: Pointer): Pointer {



    return info // Return resource
}

fun DeleteResource(resource: Pointer) {

}

fun OnTick() {

}
