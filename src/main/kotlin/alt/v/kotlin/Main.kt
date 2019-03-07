package alt.v.kotlin

import alt.v.jvm.*
import alt.v.kotlin.entities.Vehicle
import alt.v.kotlin.events.PlayerConnectEvent
import alt.v.kotlin.math.Float3
import jnr.ffi.Pointer
import alt.v.jvm.CAPI

fun main()
{
    Log.info("[Kotlin-JVM] Kotlin-JVM plugin loaded")

    val scriptRuntime = CAPI.func.alt_script_runtime_create(
            { info -> CreateResource(info) },
            { resource -> DeleteResource(resource) },
            { OnTick() }
    )
    CAPI.func.alt_server_register_script_runtime(CAPI.server, "kotlin-jvm", scriptRuntime)

    //CAPI.func.alt_server_subscribe_event(CAPI.server, CAPI.alt_event_type_t.PLAYER_CONNECT) { event -> OnConnect(event) }
}

//fun OnConnect(eptr: Pointer): Boolean {
//
//    val event = PlayerConnectEvent(eptr)
//    val player = event.player
//
//    Log.info("[KOTLIN] Player ${player.name} connected")
//
//    player.pos = Float3(1500f, 3200f, 40f)
//    val veh = Vehicle("deluxo", player.pos+2f, 0f)
//
//    return true
//}

// resources
fun CreateResource(infoptr: Pointer): Pointer?
{
    val info = CAPI.alt_resource_creation_info_t()
    info.useMemory(infoptr)
    Log.info("Loading resource ${info.name.get()}")
    val resource = Resource(info)

    if(resource.loaded) return resource.ptr
    else return null
}

fun DeleteResource(resource: Pointer) {

}

fun OnTick() {

}
