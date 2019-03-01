package alt.v.kotlin.events

import alt.v.jvm.CAPI
import alt.v.kotlin.entities.Player
import jnr.ffi.Pointer

class PlayerConnectEvent internal constructor(pointer: Pointer) {
    internal val ptr = pointer;

    val player get() = Player(CAPI.func.alt_player_connect_event_get_target(ptr))
}