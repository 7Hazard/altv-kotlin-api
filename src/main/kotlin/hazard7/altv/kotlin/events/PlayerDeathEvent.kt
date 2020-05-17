package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.entities.Entity
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.ptr
import jnr.ffi.Pointer

class PlayerDeathEvent internal constructor(pointer: Pointer) : Event(pointer) {
    val player by lazy {
        val ref = CAPI.alt_RefBase_RefStore_IPlayer()
        CAPI.func.alt_CPlayerDeathEvent_GetTarget(pointer, ref.ptr())
        Player(ref.ptr.get())
    }
    val killer by lazy {
        val ref = CAPI.alt_RefBase_RefStore_IEntity()
        CAPI.func.alt_CPlayerDeathEvent_GetKiller(pointer, ref.ptr())
        Entity(ref.ptr.get())
    }
    val weapon by lazy { CAPI.func.alt_CPlayerDeathEvent_GetWeapon(pointer) }
}
