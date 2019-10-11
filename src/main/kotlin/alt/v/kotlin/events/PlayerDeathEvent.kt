package alt.v.kotlin.events

import alt.v.jvm.CAPI
import alt.v.kotlin.Log
import alt.v.kotlin.entities.Entity
import alt.v.kotlin.entities.Player
import jnr.ffi.Pointer

class PlayerDeathEvent internal constructor(pointer: Pointer) : Event(pointer) {
    val player = Player(CAPI.func.alt_CPlayerDeathEvent_GetTarget(pointer))
    val killer = Entity(CAPI.func.alt_CPlayerDeathEvent_GetKiller(pointer))
    val weapon = CAPI.func.alt_CPlayerDeathEvent_GetWeapon(pointer)
}
