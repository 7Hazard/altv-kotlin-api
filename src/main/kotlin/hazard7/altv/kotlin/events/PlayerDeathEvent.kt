package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.entities.Entity
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.ptr
import jnr.ffi.Pointer

class PlayerDeathEvent internal constructor(pointer: Pointer) : Event(pointer) {
    val player
        get() = run {
            val ref = CAPI.alt_RefBase_RefStore_IPlayer()
            CAPI.func.alt_CPlayerDeathEvent_GetTarget(pointer, ref.ptr())
            Player(ref.ptr.get())
        }
    val killer
        get() = run {
            val ref = CAPI.alt_RefBase_RefStore_IEntity()
            CAPI.func.alt_CPlayerDeathEvent_GetKiller(pointer, ref.ptr())
            Entity(ref.ptr.get())
        }
    val weapon
        get() = CAPI.func.alt_CPlayerDeathEvent_GetWeapon(pointer)
}
