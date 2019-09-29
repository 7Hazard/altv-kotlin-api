package alt.v.kotlin.entities

import alt.v.jvm.CAPI
//import alt.v.kotlin.fromAltPosition
//import alt.v.kotlin.fromCString
import alt.v.kotlin.math.Float3
//import alt.v.kotlin.toAltPosition
import jnr.ffi.Pointer

class Player internal constructor(pointer: Pointer) {
    private val ptr: Pointer = pointer;

//    val name: String
//        get() = fromCString(50) { bufferptr -> CAPI.func.alt_player_get_name(ptr, bufferptr) }
//
//    var pos: Float3
//        get() = fromAltPosition { bufferptr -> CAPI.func.alt_player_get_position(ptr, bufferptr) }
//        set(value) = CAPI.func.alt_player_set_position(ptr, toAltPosition(value))
}

