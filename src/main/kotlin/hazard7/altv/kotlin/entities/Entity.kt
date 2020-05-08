package hazard7.altv.kotlin.entities

import hazard7.altv.jvm.CAPI
import hazard7.altv.kotlin.Vector3f
import hazard7.altv.kotlin.layout
import hazard7.altv.kotlin.math.Float3
import hazard7.altv.kotlin.ptr
import jnr.ffi.Pointer

open class Entity internal constructor(pointer: Pointer)
    : BaseObject(CAPI.func.alt_IEntity_to_alt_IBaseObject(pointer))
{
    private val entity: Pointer = pointer

    var pos: Float3
        get() = Vector3f { ptr -> CAPI.func.alt_IEntity_GetPosition(entity, ptr) }
        set(value) {
            CAPI.func.alt_IEntity_SetPosition(entity, value.layout().ptr())
        }

    var rot: Float3
        get() = Vector3f { ptr -> CAPI.func.alt_IEntity_GetRotation(entity, ptr) }
        set(value) {
            CAPI.func.alt_IEntity_SetRotation(entity, value.layout().ptr())
        }
}
