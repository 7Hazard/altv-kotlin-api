package hazard7.altv.kotlin.events

import alt.v.jvm.CAPI
import hazard7.altv.kotlin.StringView
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.ptr
import jnr.ffi.Pointer

class PlayerConnectEvent internal constructor(pointer: Pointer) : Event(pointer) {
    val player
        get() = run {
            val ref = CAPI.alt_RefBase_RefStore_IPlayer()
            CAPI.func.alt_CPlayerConnectEvent_GetTarget(pointer, ref.ptr())
            Player(ref.ptr.get())
        }
    val reason
        get() = StringView { ptr -> CAPI.func.alt_CPlayerConnectEvent_GetReason(pointer, ptr) }
}
