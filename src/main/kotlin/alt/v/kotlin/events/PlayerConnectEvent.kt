package alt.v.kotlin.events

import alt.v.jvm.AltStringView
import alt.v.jvm.CAPI
import alt.v.kotlin.entities.Player
import jnr.ffi.Pointer

class PlayerConnectEvent internal constructor(ref: Pointer) : Event(ref) {
    val player = Player(
            CAPI.func.alt_RefBase_RefStore_IPlayer_Get(
                    CAPI.func.alt_CPlayerConnectEvent_GetTarget(ref)
            )
    )
    val reason: String
        get() = AltStringView(CAPI.func.alt_CPlayerConnectEvent_GetReason(pointer)).use { it.str() }
}
