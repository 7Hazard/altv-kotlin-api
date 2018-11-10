package alt.v.kotlin

import alt.v.c.*
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString

inline class Player constructor(override val pointer: CPointer<alt_entity_t>) :
    Entity {
    var name: String
        get() = alt_player_get_name(alt_entity_to_player(pointer))?.toKString() ?: ""
        set(value) {
            alt_player_set_name(alt_entity_to_player(pointer), value)
        }
    var health: uint16_t
        get() = alt_player_get_health(alt_entity_to_player(pointer))
        set(value) {
            alt_player_set_health(alt_entity_to_player(pointer), value)
        }
    var armor: uint16_t
        get() = alt_player_get_armor(alt_entity_to_player(pointer))
        set(value) {
            alt_player_set_armor(alt_entity_to_player(pointer), value)
        }
    val seat: uint8_t
        get() = alt_player_get_seat(alt_entity_to_player(pointer))
    val headRotation: Rotation
        get() = Rotation(alt_player_get_head_rotation(alt_entity_to_player(pointer)))
    val aimPosition: Position
        get() = Position(alt_player_get_aim_pos(alt_entity_to_player(pointer)))
    val moveSpeed: Float
        get() = alt_player_get_move_speed(alt_entity_to_player(pointer))
    val ammo: uint16_t
        get() = alt_player_get_ammo(alt_entity_to_player(pointer))
    val isAiming: Boolean
        get() = alt_player_is_aiming(alt_entity_to_player(pointer))
    val isDead: Boolean
        get() = alt_player_is_dead(alt_entity_to_player(pointer))
    val isConnected: Boolean
        get() = alt_player_is_connected(alt_entity_to_player(pointer))
    val isInRagDoll: Boolean
        get() = alt_player_is_in_ragdoll(alt_entity_to_player(pointer))
    val isInVehicle: Boolean
        get() = alt_player_is_in_vehicle(alt_entity_to_player(pointer))
    val isJumping: Boolean
        get() = alt_player_is_jumping(alt_entity_to_player(pointer))
    val isReloading: Boolean
        get() = alt_player_is_reloading(alt_entity_to_player(pointer))
    val isShooting: Boolean
        get() = alt_player_is_shooting(alt_entity_to_player(pointer))
    val vehicle: Vehicle?
        get() = alt_player_get_vehicle(alt_entity_to_player(pointer))?.let {
            alt_vehicle_to_entity(it)?.let { vehiclePointer ->
                Vehicle(
                    vehiclePointer
                )
            }
        }

    fun spawn(position: Position) {
        alt_player_spawn(alt_entity_to_player(pointer), position.position)
    }

    fun despawn() {
        alt_player_despawn(alt_entity_to_player(pointer))
    }
}