package alt.v.kotlin.entities

import alt.v.jvm.AltStringView
import alt.v.jvm.CAPI
//import alt.v.kotlin.fromAltPosition
//import alt.v.kotlin.fromCString
import alt.v.kotlin.math.Float3
//import alt.v.kotlin.toAltPosition
import jnr.ffi.Pointer
import jnr.ffi.Struct

class Player internal constructor(pointer: Pointer) {
    private val pointer: Pointer = pointer

    val name: String
        get() = AltStringView(CAPI.func.alt_IPlayer_GetName(pointer)).use { it.str() }

    var pos: Float3
        get() {
            val s = CAPI.alt_Vector_float_3_PointLayout()
            s.useMemory(CAPI.func.alt_IPlayer_GetPosition(pointer))
            return Float3(s.x.get(), s.y.get(), s.z.get())
        }
        set(value) {
            val s = CAPI.alt_Vector_float_3_PointLayout()
            s.x.set(value.x)
            s.y.set(value.y)
            s.z.set(value.z)
            CAPI.func.alt_IPlayer_SetPosition(pointer, Struct.getMemory(s))
        }

//    val name: String
//        get() = fromCString(50) { bufferptr -> CAPI.func.alt_player_get_name(ptr, bufferptr) }
//
//    var pos: Float3
//        get() = fromAltPosition { bufferptr -> CAPI.func.alt_player_get_position(ptr, bufferptr) }
//        set(value) = CAPI.func.alt_player_set_position(ptr, toAltPosition(value))
}

