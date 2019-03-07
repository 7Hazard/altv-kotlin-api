package alt.v.kotlin.events

import alt.v.jvm.CAPI
import jnr.ffi.Pointer

open class Event internal constructor(instance: Pointer) {
    internal val ptr = instance

    constructor(event: Event) : this(event.ptr)

    val type: CAPI.alt_event_type_t
        get() = CAPI.func.alt_event_get_type(ptr)
}