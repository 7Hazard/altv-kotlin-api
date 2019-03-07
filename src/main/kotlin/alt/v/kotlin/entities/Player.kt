package alt.v.kotlin.entities

import alt.v.jvm.CAPI as capi
import alt.v.kotlin.fromAltPosition
import alt.v.kotlin.fromCString
import alt.v.kotlin.math.Float3
import jnr.ffi.Pointer

class Player internal constructor(pointer: Pointer) {
    private val ptr: Pointer = pointer;

    val name: String
        get() = fromCString(50) { bufferptr -> capi.func.alt_player_get_name(ptr, bufferptr) }

    var pos: Float3
        get() = fromAltPosition { bufferptr -> capi.func.alt_player_get_position(ptr, bufferptr) }
        set(value) = capi.func.alt_player_set_position2(ptr, value.x, value.y, value.z)
}
