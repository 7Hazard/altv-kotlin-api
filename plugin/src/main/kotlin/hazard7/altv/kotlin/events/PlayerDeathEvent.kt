package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.entities.Entity
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.pointer
import jnr.ffi.Pointer

class PlayerDeathEvent internal constructor(pointer: Pointer) : Event(pointer) {
    val player = run {
        assert(thread == Thread.currentThread())
        val ref = CAPI.alt_RefBase_RefStore_IPlayer()
        CAPI.func.alt_CPlayerDeathEvent_GetTarget(pointer, ref.pointer)
        Player(ref.ptr.get())
    }
    val killer = run {
        val ref = CAPI.alt_RefBase_RefStore_IEntity()
        CAPI.func.alt_CPlayerDeathEvent_GetKiller(pointer, ref.pointer)
        Entity(ref.ptr.get())
    }
    val weapon = CAPI.func.alt_CPlayerDeathEvent_GetWeapon(pointer)
}
