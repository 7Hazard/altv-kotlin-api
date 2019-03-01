package alt.v.kotlin.entities

import alt.v.jvm.CAPI
import alt.v.kotlin.GetCString
import jnr.ffi.Pointer

class Player internal constructor(pointer: Pointer) {
    private val ptr: Pointer = pointer;

    val name: String
        get() = GetCString(50) { bufferptr -> CAPI.func.alt_player_get_name(ptr, bufferptr) }
}
