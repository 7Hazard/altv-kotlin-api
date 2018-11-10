package alt.v.kotlin

import alt.v.c.*
import kotlinx.cinterop.CPointer

inline class Vehicle constructor(override val pointer: CPointer<alt_entity_t>) :
    Entity {
    companion object {
        fun create(model: uint32_t, position: Position, heading: Float): Vehicle? {
            val pointer = alt_server_create_vehicle(model, position.position, heading) ?: return null
            val entityPointer = alt_vehicle_to_entity(pointer) ?: return null
            return Vehicle(entityPointer)
        }
    }

    fun hasMod(id: uint16_t): Boolean {
        return alt_vehicle_get_mod(alt_entity_to_vehicle(pointer), id)
    }

    fun setMod(id: uint16_t, state: Boolean) {
        alt_vehicle_set_mod(alt_entity_to_vehicle(pointer), id, state)
    }
}