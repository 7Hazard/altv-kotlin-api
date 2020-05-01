package hazard7.altv.kotlin.events

import alt.v.jvm.CAPI
import jnr.ffi.Pointer


open class Event internal constructor(pointer: Pointer) {
    internal val pointer = pointer
    private val struct = run {
        val s = CAPI.alt_CEvent()
        s.useMemory(pointer)
        s
    }

    internal val capiType get() = struct.type.get()

    val wasCancelled: Boolean
        get() = CAPI.func.alt_CEvent_WasCancelled(pointer)
}
