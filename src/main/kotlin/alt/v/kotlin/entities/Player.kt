package alt.v.kotlin.entities

import alt.v.jvm.AltStringView
import alt.v.jvm.CAPI
import alt.v.kotlin.Log
import alt.v.kotlin.math.Float3
import jnr.ffi.Pointer
import jnr.ffi.Struct

class Player internal constructor(pointer: Pointer)
    : Entity(CAPI.func.alt_IPlayer_to_alt_IEntity(pointer))
{
    private val player: Pointer = pointer

    val name = AltStringView(CAPI.func.alt_IPlayer_GetName(player)).use { it.str() }

    var health
        get() = CAPI.func.alt_IPlayer_GetHealth(player)
        set(value) = CAPI.func.alt_IPlayer_SetHealth(player, value)

    var model
        get() = CAPI.func.alt_IPlayer_GetModel(player)
        set(value) = CAPI.func.alt_IPlayer_SetModel(player, value)
}

