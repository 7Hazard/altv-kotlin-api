package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.StringView
import hazard7.altv.kotlin.entities.Player
import hazard7.altv.kotlin.entities.Vehicle
import hazard7.altv.kotlin.pointer
import jnr.ffi.Pointer

class PlayerLeftVehicleEvent internal constructor(ceventptr: Pointer) : Event(ceventptr) {
    val pointer = CAPI.func.alt_CEvent_to_alt_CPlayerLeaveVehicleEvent(ceventptr)
    val player = run {
        val ref = CAPI.alt_RefBase_RefStore_IPlayer()
        CAPI.func.alt_CPlayerLeaveVehicleEvent_GetPlayer(pointer, ref.pointer)
        Player(ref.ptr.get())
    }
    val target = run {
        val ref = CAPI.alt_RefBase_RefStore_IVehicle()
        CAPI.func.alt_CPlayerLeaveVehicleEvent_GetTarget(pointer, ref.pointer)
        Vehicle(ref.ptr.get())
    }
    val seat = CAPI.func.alt_CPlayerLeaveVehicleEvent_GetSeat(pointer)
}
