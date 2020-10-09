package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.entities.Entity
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.pointer
import jnr.ffi.Pointer

class PlayerDiedEvent internal constructor(ceventptr: Pointer) : Event(ceventptr) {
    val pointer = CAPI.func.alt_CEvent_to_alt_CPlayerDeathEvent(ceventptr)
    val player = run {
        val ref = CAPI.alt_RefBase_RefStore_IPlayer()
        CAPI.func.alt_CPlayerDeathEvent_GetTarget(pointer, ref.pointer)
        Player(ref.ptr.get())
    }
    val killer = run {
        val ref = CAPI.alt_RefBase_RefStore_IEntity()
        CAPI.func.alt_CPlayerDeathEvent_GetKiller(pointer, ref.pointer)
        if(ref.ptr.get() != null) Entity(ref.ptr.get())
        else null
    }
    val weapon = CAPI.func.alt_CPlayerDeathEvent_GetWeapon(pointer)
}
