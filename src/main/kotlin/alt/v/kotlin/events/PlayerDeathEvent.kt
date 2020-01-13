package alt.v.kotlin.events

import alt.v.jvm.CAPI
import alt.v.kotlin.Log
import alt.v.kotlin.entities.Entity
import alt.v.kotlin.entities.Player
import jnr.ffi.Pointer

class PlayerDeathEvent internal constructor(ref: Pointer) : Event(ref) {
    val player = Player(
            CAPI.func.alt_RefBase_RefStore_IPlayer_Get(
                    CAPI.func.alt_CPlayerDeathEvent_GetTarget(ref)
            )
    )
    val killer = Entity(
            CAPI.func.alt_RefBase_RefStore_IEntity_Get(
                    CAPI.func.alt_CPlayerDeathEvent_GetKiller(ref)
            )
    )
    val weapon = CAPI.func.alt_CPlayerDeathEvent_GetWeapon(ref)
}
