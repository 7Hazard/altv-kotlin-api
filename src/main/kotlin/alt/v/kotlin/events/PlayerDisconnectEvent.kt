package alt.v.kotlin.events

import alt.v.jvm.AltStringView
import alt.v.jvm.CAPI
import alt.v.kotlin.StringView
import alt.v.kotlin.entities.Player
import alt.v.kotlin.ptr
import jnr.ffi.Pointer

class PlayerDisconnectEvent internal constructor(pointer: Pointer) : Event(pointer) {
    val player
        get() = run {
            val ref = CAPI.alt_RefBase_RefStore_IPlayer()
            CAPI.func.alt_CPlayerDisconnectEvent_GetTarget(pointer, ref.ptr())
            Player(ref.ptr.get())
        }
    val reason: String
        get() = StringView { ptr -> CAPI.func.alt_CPlayerDisconnectEvent_GetReason(pointer, ptr) }
}
