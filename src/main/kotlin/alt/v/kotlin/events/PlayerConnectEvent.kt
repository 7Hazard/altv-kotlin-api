package alt.v.kotlin.events

import alt.v.jvm.AltStringView
import alt.v.jvm.CAPI
import alt.v.kotlin.entities.Player
import jnr.ffi.Pointer

class PlayerConnectEvent internal constructor(ptr: Pointer) : Event(ptr) {
    val player = Player(CAPI.func.alt_CPlayerConnectEvent_GetTarget(ptr))
    val reason: String
        get() = AltStringView(CAPI.func.alt_CPlayerConnectEvent_GetReason(pointer)).use { it.str() }
}
