package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import jnr.ffi.Pointer


open class Event internal constructor(pointer: Pointer) {
    internal val struct = CAPI.alt_CEvent(pointer)

    internal val type by lazy { struct.type.get() }

    val wasCancelled by lazy { CAPI.func.alt_CEvent_WasCancelled(pointer) }
}
