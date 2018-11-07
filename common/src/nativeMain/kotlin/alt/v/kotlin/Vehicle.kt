package alt.v.kotlin

import alt.v.c.*
import kotlinx.cinterop.CValue

class Vehicle internal constructor(private val pointer: kotlinx.cinterop.COpaquePointer) {
    companion object {
        fun create(model: uint32_t, position: CValue<alt_position_t>, heading: Float): Vehicle? {
            val pointer = alt_server_create_vehicle(model, position, heading) ?: return null
            return Vehicle(pointer)
        }
    }

    fun hasMod(id: uint16_t): Boolean {
        return alt_vehicle_get_mod(pointer, id)
    }

    fun setMod(id: uint16_t, state: Boolean) {
        alt_vehicle_set_mod(pointer, id, state)
    }
}