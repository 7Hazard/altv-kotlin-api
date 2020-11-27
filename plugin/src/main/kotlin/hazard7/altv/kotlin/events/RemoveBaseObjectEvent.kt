package hazard7.altv.kotlin.events

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.Resource
import hazard7.altv.kotlin.pointer
import jnr.ffi.Pointer

class RemoveBaseObjectEvent internal constructor(ceventptr: Pointer) : Event(ceventptr) {
    internal val pointer = CAPI.func.alt_CEvent_to_alt_CRemoveBaseObjectEvent(ceventptr)
    internal val entity = run {
        val ref = CAPI.alt_RefBase_RefStore_IEntity()
        CAPI.func.alt_CRemoveBaseObjectEvent_GetObject(pointer, ref.pointer)
        ref.ptr.get()
    }
//    fun getEntity(resource: Resource) = resource.getOrCreateEntity(entity)
}
