package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.Resource
import jnr.ffi.Pointer

class ResourceStartEvent internal constructor(ceventptr: Pointer) : Event(ceventptr) {
    val pointer = CAPI.func.alt_CEvent_to_alt_CServerScriptEvent(ceventptr)
    val resource = Resource.ptrmap[CAPI.func.alt_CResourceStartEvent_GetResource(pointer)]
}
