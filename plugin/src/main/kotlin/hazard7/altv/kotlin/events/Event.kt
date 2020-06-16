package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import jnr.ffi.Pointer


open class Event internal constructor(pointer: Pointer) {
    internal val thread = Thread.currentThread()
    internal val type = CAPI.func.alt_CEvent_GetType(pointer)
    val wasCancelled = CAPI.func.alt_CEvent_WasCancelled(pointer)
}
