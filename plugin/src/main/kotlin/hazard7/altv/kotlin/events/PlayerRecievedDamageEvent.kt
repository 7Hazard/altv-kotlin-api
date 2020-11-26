package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.Resource
import hazard7.altv.kotlin.entities.Entity
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.pointer
import jnr.ffi.Pointer

class PlayerRecievedDamageEvent internal constructor(ceventptr: Pointer) : Event(ceventptr) {
    val pointer = CAPI.func.alt_CEvent_to_alt_CPlayerDamageEvent(ceventptr)

    internal val target = run {
        val ref = CAPI.alt_RefBase_RefStore_IPlayer()
        CAPI.func.alt_CPlayerDamageEvent_GetTarget(pointer, ref.pointer)
        ref.ptr.get()
    }
    fun getPlayer(resource: Resource) = resource.getOrCreatePlayer(target)

    internal val attacker = run {
        val ref = CAPI.alt_RefBase_RefStore_IEntity()
        CAPI.func.alt_CPlayerDamageEvent_GetAttacker(pointer, ref.pointer)
        if(ref.ptr.get() != null) ref.ptr.get()
        else null
    }
    fun getAttacker(resource: Resource): Entity? {
        if(attacker == null) return null
        else return resource.getOrCreateEntity(attacker)
    }

    val damage = CAPI.func.alt_CPlayerDamageEvent_GetDamage(pointer)
    val weapon = CAPI.func.alt_CPlayerDamageEvent_GetWeapon(pointer)
}
