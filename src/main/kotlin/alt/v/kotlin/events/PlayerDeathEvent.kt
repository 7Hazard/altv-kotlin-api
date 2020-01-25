package alt.v.kotlin.events

import alt.v.jvm.CAPI
import alt.v.kotlin.Log
import alt.v.kotlin.entities.Entity
import alt.v.kotlin.entities.Player
import jnr.ffi.Pointer

class PlayerDeathEvent internal constructor(pointer: Pointer) : Event(pointer) {
    val player
        get() = Player.fromRef(CAPI.func.alt_CPlayerDeathEvent_GetTarget(pointer))
    val killer
        get() = Entity.fromRef(CAPI.func.alt_CPlayerDeathEvent_GetKiller(pointer))
    val weapon
        get() = CAPI.func.alt_CPlayerDeathEvent_GetWeapon(pointer)
}
