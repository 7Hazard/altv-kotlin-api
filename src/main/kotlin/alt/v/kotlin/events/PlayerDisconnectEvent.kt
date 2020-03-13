package alt.v.kotlin.events

import alt.v.jvm.AltStringView
import alt.v.jvm.CAPI
import alt.v.kotlin.entities.Player
import jnr.ffi.Pointer

class PlayerDisconnectEvent internal constructor(pointer: Pointer) : Event(pointer) {
    val player
        get() = Player.fromRef(CAPI.func.alt_CPlayerDisconnectEvent_GetTarget(pointer))
    val reason: String
        get() = AltStringView(CAPI.func.alt_CPlayerDisconnectEvent_GetReason(pointer)).str()
}
