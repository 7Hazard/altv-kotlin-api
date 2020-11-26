package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.Resource
import hazard7.altv.kotlin.entities.Entity
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.pointer
import jnr.ffi.Pointer

class PlayerDiedEvent internal constructor(ceventptr: Pointer) : Event(ceventptr) {
    val pointer = CAPI.func.alt_CEvent_to_alt_CPlayerDeathEvent(ceventptr)
    internal val player = run {
        val ref = CAPI.alt_RefBase_RefStore_IPlayer()
        CAPI.func.alt_CPlayerDeathEvent_GetTarget(pointer, ref.pointer)
        ref.ptr.get()
    }
    fun getPlayer(resource: Resource) = resource.getOrCreatePlayer(player)

    internal val killer = run {
        val ref = CAPI.alt_RefBase_RefStore_IEntity()
        CAPI.func.alt_CPlayerDeathEvent_GetKiller(pointer, ref.pointer)
        if(ref.ptr.get() != null) ref.ptr.get()
        else null
    }
    fun getKiller(resource: Resource): Entity? {
        if (killer == null) return null
        else return resource.getOrCreateEntity(killer)
    }

    val weapon = CAPI.func.alt_CPlayerDeathEvent_GetWeapon(pointer)
}
