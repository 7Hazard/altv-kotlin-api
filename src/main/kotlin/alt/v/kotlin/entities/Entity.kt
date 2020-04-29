package alt.v.kotlin.entities

import alt.v.jvm.CAPI
import alt.v.kotlin.Vector3f
import alt.v.kotlin.layout
import alt.v.kotlin.math.Float3
import alt.v.kotlin.ptr
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

