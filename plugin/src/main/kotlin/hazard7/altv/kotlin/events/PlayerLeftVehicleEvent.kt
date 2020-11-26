package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.Resource
import hazard7.altv.kotlin.StringView
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.entities.Vehicle
import hazard7.altv.kotlin.pointer
import jnr.ffi.Pointer

class PlayerLeftVehicleEvent internal constructor(ceventptr: Pointer) : Event(ceventptr) {
    val pointer = CAPI.func.alt_CEvent_to_alt_CPlayerLeaveVehicleEvent(ceventptr)
    internal val player = run {
        val ref = CAPI.alt_RefBase_RefStore_IPlayer()
        CAPI.func.alt_CPlayerLeaveVehicleEvent_GetPlayer(pointer, ref.pointer)
        ref.ptr.get()
    }
    fun getPlayer(resource: Resource) = resource.getOrCreatePlayer(player)
    internal val target = run {
        val ref = CAPI.alt_RefBase_RefStore_IVehicle()
        CAPI.func.alt_CPlayerLeaveVehicleEvent_GetTarget(pointer, ref.pointer)
        ref.ptr.get()
    }
    fun getVehicle(resource: Resource) = resource.getOrCreateVehicle(target)

    val seat = CAPI.func.alt_CPlayerLeaveVehicleEvent_GetSeat(pointer)
}
