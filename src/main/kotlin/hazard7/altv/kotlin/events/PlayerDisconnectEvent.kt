package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.StringView
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.ptr
import jnr.ffi.Pointer

class PlayerDisconnectEvent internal constructor(pointer: Pointer) : Event(pointer) {
    val player by lazy {
        val ref = CAPI.alt_RefBase_RefStore_IPlayer()
        CAPI.func.alt_CPlayerDisconnectEvent_GetTarget(pointer, ref.ptr())
        Player(ref.ptr.get())
    }
    val reason by lazy { StringView { ptr -> CAPI.func.alt_CPlayerDisconnectEvent_GetReason(pointer, ptr) } }
}
