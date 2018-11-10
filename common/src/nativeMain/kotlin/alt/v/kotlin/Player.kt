package alt.v.kotlin

import alt.v.c.*
import kotlinx.cinterop.CValue
import kotlinx.cinterop.toKString

class Player internal constructor(pointer: kotlinx.cinterop.COpaquePointer) :
    Entity(pointer) {
    var name: String
        get() = alt_player_get_name(pointer)?.toKString() ?: ""
        set(value) {
            alt_player_set_name(pointer, value)
        }
    var health: uint16_t
        get() = alt_player_get_health(pointer)
        set(value) {
            alt_player_set_health(pointer, health)
        }
    var armor: uint16_t
        get() = alt_player_get_armor(pointer)
        set(value) {
            alt_player_set_armor(pointer, value)
        }
    val seat: uint8_t
        get() = alt_player_get_seat(pointer)
    val headRotation: Rotation
        get() = Rotation(alt_player_get_head_rotation(pointer))
    val aimPosition: Position
        get() = Position(alt_player_get_aim_pos(pointer))
    val moveSpeed: Float
        get() = alt_player_get_move_speed(pointer)
    val ammo: uint16_t
        get() = alt_player_get_ammo(pointer)
    val isAiming: Boolean
        get() = alt_player_is_aiming(pointer)
    val isDead: Boolean
        get() = alt_player_is_dead(pointer)
    val isConnected: Boolean
        get() = alt_player_is_connected(pointer)
    val isInRagDoll: Boolean
        get() = alt_player_is_in_ragdoll(pointer)
    val isInVehicle: Boolean
        get() = alt_player_is_in_vehicle(pointer)
    val isJumping: Boolean
        get() = alt_player_is_jumping(pointer)
    val isReloading: Boolean
        get() = alt_player_is_reloading(pointer)
    val isShooting: Boolean
        get() = alt_player_is_shooting(pointer)
    val vehicle: Vehicle?
        get() = alt_player_get_vehicle(pointer)?.let { Vehicle(it) }//TODO: reuse objects?

    fun spawn(position: Position) {
        alt_player_spawn(pointer, position.position)
    }

    fun despawn() {
        alt_player_despawn(pointer)
    }
}